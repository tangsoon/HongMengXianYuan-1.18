package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderDispatcher {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int MAX_WORKERS_32_BIT = 4;
   private static final VertexFormat VERTEX_FORMAT = DefaultVertexFormat.BLOCK;
   private final PriorityQueue<ChunkRenderDispatcher.RenderChunk.ChunkCompileTask> toBatch = Queues.newPriorityQueue();
   private final Queue<ChunkBufferBuilderPack> freeBuffers;
   private final Queue<Runnable> toUpload = Queues.newConcurrentLinkedQueue();
   private volatile int toBatchCount;
   private volatile int freeBufferCount;
   final ChunkBufferBuilderPack fixedBuffers;
   private final ProcessorMailbox<Runnable> mailbox;
   private final Executor executor;
   Level level;
   final LevelRenderer renderer;
   private Vec3 camera = Vec3.ZERO;

   public ChunkRenderDispatcher(Level pLevel, LevelRenderer pRenderer, Executor pExecutor, boolean pIs64Bit, ChunkBufferBuilderPack pFixedBuffers) {
      this(pLevel, pRenderer, pExecutor, pIs64Bit, pFixedBuffers, -1);
   }
   public ChunkRenderDispatcher(Level pLevel, LevelRenderer pRenderer, Executor pExecutor, boolean pIs64Bit, ChunkBufferBuilderPack pFixedBuffers, int countRenderBuilders) {
      this.level = pLevel;
      this.renderer = pRenderer;
      int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3D) / (RenderType.chunkBufferLayers().stream().mapToInt(RenderType::bufferSize).sum() * 4) - 1);
      int j = Runtime.getRuntime().availableProcessors();
      int k = pIs64Bit ? j : Math.min(j, 4);
      int l = countRenderBuilders < 0 ? Math.max(1, Math.min(k, i)) : countRenderBuilders;
      this.fixedBuffers = pFixedBuffers;
      List<ChunkBufferBuilderPack> list = Lists.newArrayListWithExpectedSize(l);

      try {
         for(int i1 = 0; i1 < l; ++i1) {
            list.add(new ChunkBufferBuilderPack());
         }
      } catch (OutOfMemoryError outofmemoryerror) {
         LOGGER.warn("Allocated only {}/{} buffers", list.size(), l);
         int j1 = Math.min(list.size() * 2 / 3, list.size() - 1);

         for(int k1 = 0; k1 < j1; ++k1) {
            list.remove(list.size() - 1);
         }

         System.gc();
      }

      this.freeBuffers = Queues.newArrayDeque(list);
      this.freeBufferCount = this.freeBuffers.size();
      this.executor = pExecutor;
      this.mailbox = ProcessorMailbox.create(pExecutor, "Chunk Renderer");
      this.mailbox.tell(this::runTask);
   }

   public void setLevel(Level pLevel) {
      this.level = pLevel;
   }

   private void runTask() {
      if (!this.freeBuffers.isEmpty()) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask chunkrenderdispatcher$renderchunk$chunkcompiletask = this.toBatch.poll();
         if (chunkrenderdispatcher$renderchunk$chunkcompiletask != null) {
            ChunkBufferBuilderPack chunkbufferbuilderpack = this.freeBuffers.poll();
            this.toBatchCount = this.toBatch.size();
            this.freeBufferCount = this.freeBuffers.size();
            CompletableFuture.runAsync(() -> {
            }, this.executor).thenCompose((p_112714_) -> {
               return chunkrenderdispatcher$renderchunk$chunkcompiletask.doTask(chunkbufferbuilderpack);
            }).whenComplete((p_112704_, p_112705_) -> {
               if (p_112705_ != null) {
                  CrashReport crashreport = CrashReport.forThrowable(p_112705_, "Batching chunks");
                  Minecraft.getInstance().delayCrash(Minecraft.getInstance().fillReport(crashreport));
               } else {
                  this.mailbox.tell(() -> {
                     if (p_112704_ == ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL) {
                        chunkbufferbuilderpack.clearAll();
                     } else {
                        chunkbufferbuilderpack.discardAll();
                     }

                     this.freeBuffers.add(chunkbufferbuilderpack);
                     this.freeBufferCount = this.freeBuffers.size();
                     this.runTask();
                  });
               }
            });
         }
      }
   }

   public String getStats() {
      return String.format("pC: %03d, pU: %02d, aB: %02d", this.toBatchCount, this.toUpload.size(), this.freeBufferCount);
   }

   public int getToBatchCount() {
      return this.toBatchCount;
   }

   public int getToUpload() {
      return this.toUpload.size();
   }

   public int getFreeBufferCount() {
      return this.freeBufferCount;
   }

   public void setCamera(Vec3 pCamera) {
      this.camera = pCamera;
   }

   public Vec3 getCameraPosition() {
      return this.camera;
   }

   public boolean uploadAllPendingUploads() {
      boolean flag;
      Runnable runnable;
      for(flag = false; (runnable = this.toUpload.poll()) != null; flag = true) {
         runnable.run();
      }

      return flag;
   }

   public void rebuildChunkSync(ChunkRenderDispatcher.RenderChunk pRenderChunk) {
      pRenderChunk.compileSync();
   }

   public void blockUntilClear() {
      this.clearBatchQueue();
   }

   public void schedule(ChunkRenderDispatcher.RenderChunk.ChunkCompileTask pTask) {
      this.mailbox.tell(() -> {
         this.toBatch.offer(pTask);
         this.toBatchCount = this.toBatch.size();
         this.runTask();
      });
   }

   public CompletableFuture<Void> uploadChunkLayer(BufferBuilder pBuilder, VertexBuffer pBuffer) {
      return CompletableFuture.runAsync(() -> {
      }, this.toUpload::add).thenCompose((p_112701_) -> {
         return this.doUploadChunkLayer(pBuilder, pBuffer);
      });
   }

   private CompletableFuture<Void> doUploadChunkLayer(BufferBuilder pBuilder, VertexBuffer pBuffer) {
      return pBuffer.uploadLater(pBuilder);
   }

   private void clearBatchQueue() {
      while(!this.toBatch.isEmpty()) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask chunkrenderdispatcher$renderchunk$chunkcompiletask = this.toBatch.poll();
         if (chunkrenderdispatcher$renderchunk$chunkcompiletask != null) {
            chunkrenderdispatcher$renderchunk$chunkcompiletask.cancel();
         }
      }

      this.toBatchCount = 0;
   }

   public boolean isQueueEmpty() {
      return this.toBatchCount == 0 && this.toUpload.isEmpty();
   }

   public void dispose() {
      this.clearBatchQueue();
      this.mailbox.close();
      this.freeBuffers.clear();
   }

   @OnlyIn(Dist.CLIENT)
   static enum ChunkTaskResult {
      SUCCESSFUL,
      CANCELLED;
   }

   @OnlyIn(Dist.CLIENT)
   public static class CompiledChunk {
      public static final ChunkRenderDispatcher.CompiledChunk UNCOMPILED = new ChunkRenderDispatcher.CompiledChunk() {
         public boolean facesCanSeeEachother(Direction p_112782_, Direction p_112783_) {
            return false;
         }
      };
      final Set<RenderType> hasBlocks = new ObjectArraySet<>();
      final Set<RenderType> hasLayer = new ObjectArraySet<>();
      boolean isCompletelyEmpty = true;
      final List<BlockEntity> renderableBlockEntities = Lists.newArrayList();
      VisibilitySet visibilitySet = new VisibilitySet();
      @Nullable
      BufferBuilder.SortState transparencyState;

      public boolean hasNoRenderableLayers() {
         return this.isCompletelyEmpty;
      }

      public boolean isEmpty(RenderType pRenderType) {
         return !this.hasBlocks.contains(pRenderType);
      }

      public List<BlockEntity> getRenderableBlockEntities() {
         return this.renderableBlockEntities;
      }

      public boolean facesCanSeeEachother(Direction pFace, Direction pOtherFace) {
         return this.visibilitySet.visibilityBetween(pFace, pOtherFace);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class RenderChunk implements net.minecraftforge.client.extensions.IForgeRenderChunk {
      public static final int SIZE = 16;
      public final int index;
      public final AtomicReference<ChunkRenderDispatcher.CompiledChunk> compiled = new AtomicReference<>(ChunkRenderDispatcher.CompiledChunk.UNCOMPILED);
      @Nullable
      private ChunkRenderDispatcher.RenderChunk.RebuildTask lastRebuildTask;
      @Nullable
      private ChunkRenderDispatcher.RenderChunk.ResortTransparencyTask lastResortTransparencyTask;
      private final Set<BlockEntity> globalBlockEntities = Sets.newHashSet();
      private final Map<RenderType, VertexBuffer> buffers = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((p_112837_) -> {
         return p_112837_;
      }, (p_112834_) -> {
         return new VertexBuffer();
      }));
      public AABB bb;
      private int lastFrame = -1;
      private boolean dirty = true;
      final BlockPos.MutableBlockPos origin = new BlockPos.MutableBlockPos(-1, -1, -1);
      private final BlockPos.MutableBlockPos[] relativeOrigins = Util.make(new BlockPos.MutableBlockPos[6], (p_112831_) -> {
         for(int i = 0; i < p_112831_.length; ++i) {
            p_112831_[i] = new BlockPos.MutableBlockPos();
         }

      });
      private boolean playerChanged;

      public RenderChunk(int p_173720_) {
         this.index = p_173720_;
      }

      private boolean doesChunkExistAt(BlockPos pPos) {
         return ChunkRenderDispatcher.this.level.getChunk(SectionPos.blockToSectionCoord(pPos.getX()), SectionPos.blockToSectionCoord(pPos.getZ()), ChunkStatus.FULL, false) != null;
      }

      public boolean hasAllNeighbors() {
         int i = 24;
         if (!(this.getDistToPlayerSqr() > 576.0D)) {
            return true;
         } else {
            return this.doesChunkExistAt(this.relativeOrigins[Direction.WEST.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.NORTH.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.EAST.ordinal()]) && this.doesChunkExistAt(this.relativeOrigins[Direction.SOUTH.ordinal()]);
         }
      }

      public boolean setFrame(int pLastFrame) {
         if (this.lastFrame == pLastFrame) {
            return false;
         } else {
            this.lastFrame = pLastFrame;
            return true;
         }
      }

      public VertexBuffer getBuffer(RenderType pRenderType) {
         return this.buffers.get(pRenderType);
      }

      public void setOrigin(int pX, int pY, int pZ) {
         if (pX != this.origin.getX() || pY != this.origin.getY() || pZ != this.origin.getZ()) {
            this.reset();
            this.origin.set(pX, pY, pZ);
            this.bb = new AABB((double)pX, (double)pY, (double)pZ, (double)(pX + 16), (double)(pY + 16), (double)(pZ + 16));

            for(Direction direction : Direction.values()) {
               this.relativeOrigins[direction.ordinal()].set(this.origin).move(direction, 16);
            }

         }
      }

      protected double getDistToPlayerSqr() {
         Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
         double d0 = this.bb.minX + 8.0D - camera.getPosition().x;
         double d1 = this.bb.minY + 8.0D - camera.getPosition().y;
         double d2 = this.bb.minZ + 8.0D - camera.getPosition().z;
         return d0 * d0 + d1 * d1 + d2 * d2;
      }

      void beginLayer(BufferBuilder pBuilder) {
         pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
      }

      public ChunkRenderDispatcher.CompiledChunk getCompiledChunk() {
         return this.compiled.get();
      }

      private void reset() {
         this.cancelTasks();
         this.compiled.set(ChunkRenderDispatcher.CompiledChunk.UNCOMPILED);
         this.dirty = true;
      }

      public void releaseBuffers() {
         this.reset();
         this.buffers.values().forEach(VertexBuffer::close);
      }

      public BlockPos getOrigin() {
         return this.origin;
      }

      public void setDirty(boolean pReRenderOnMainThread) {
         boolean flag = this.dirty;
         this.dirty = true;
         this.playerChanged = pReRenderOnMainThread | (flag && this.playerChanged);
      }

      public void setNotDirty() {
         this.dirty = false;
         this.playerChanged = false;
      }

      public boolean isDirty() {
         return this.dirty;
      }

      public boolean isDirtyFromPlayer() {
         return this.dirty && this.playerChanged;
      }

      public BlockPos getRelativeOrigin(Direction pDirection) {
         return this.relativeOrigins[pDirection.ordinal()];
      }

      public boolean resortTransparency(RenderType pType, ChunkRenderDispatcher pDispatcher) {
         ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk = this.getCompiledChunk();
         if (this.lastResortTransparencyTask != null) {
            this.lastResortTransparencyTask.cancel();
         }

         if (!chunkrenderdispatcher$compiledchunk.hasLayer.contains(pType)) {
            return false;
         } else {
            this.lastResortTransparencyTask = new ChunkRenderDispatcher.RenderChunk.ResortTransparencyTask(new net.minecraft.world.level.ChunkPos(getOrigin()), this.getDistToPlayerSqr(), chunkrenderdispatcher$compiledchunk);
            pDispatcher.schedule(this.lastResortTransparencyTask);
            return true;
         }
      }

      protected void cancelTasks() {
         if (this.lastRebuildTask != null) {
            this.lastRebuildTask.cancel();
            this.lastRebuildTask = null;
         }

         if (this.lastResortTransparencyTask != null) {
            this.lastResortTransparencyTask.cancel();
            this.lastResortTransparencyTask = null;
         }

      }

      public ChunkRenderDispatcher.RenderChunk.ChunkCompileTask createCompileTask() {
         this.cancelTasks();
         BlockPos blockpos = this.origin.immutable();
         int i = 1;
         RenderChunkRegion renderchunkregion = createRegionRenderCache(ChunkRenderDispatcher.this.level, blockpos.offset(-1, -1, -1), blockpos.offset(16, 16, 16), 1);
         this.lastRebuildTask = new ChunkRenderDispatcher.RenderChunk.RebuildTask(new net.minecraft.world.level.ChunkPos(getOrigin()), this.getDistToPlayerSqr(), renderchunkregion);
         return this.lastRebuildTask;
      }

      public void rebuildChunkAsync(ChunkRenderDispatcher pDispatcher) {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask chunkrenderdispatcher$renderchunk$chunkcompiletask = this.createCompileTask();
         pDispatcher.schedule(chunkrenderdispatcher$renderchunk$chunkcompiletask);
      }

      void updateGlobalBlockEntities(Set<BlockEntity> pBlockEntities) {
         Set<BlockEntity> set = Sets.newHashSet(pBlockEntities);
         Set<BlockEntity> set1 = Sets.newHashSet(this.globalBlockEntities);
         set.removeAll(this.globalBlockEntities);
         set1.removeAll(pBlockEntities);
         this.globalBlockEntities.clear();
         this.globalBlockEntities.addAll(pBlockEntities);
         ChunkRenderDispatcher.this.renderer.updateGlobalBlockEntities(set1, set);
      }

      public void compileSync() {
         ChunkRenderDispatcher.RenderChunk.ChunkCompileTask chunkrenderdispatcher$renderchunk$chunkcompiletask = this.createCompileTask();
         chunkrenderdispatcher$renderchunk$chunkcompiletask.doTask(ChunkRenderDispatcher.this.fixedBuffers);
      }

      @OnlyIn(Dist.CLIENT)
      abstract class ChunkCompileTask implements Comparable<ChunkRenderDispatcher.RenderChunk.ChunkCompileTask> {
         protected final double distAtCreation;
         protected final AtomicBoolean isCancelled = new AtomicBoolean(false);
         protected java.util.Map<net.minecraft.core.BlockPos, net.minecraftforge.client.model.data.IModelData> modelData;

         public ChunkCompileTask(double p_112852_) {
            this(null, p_112852_);
         }

         public ChunkCompileTask(@Nullable net.minecraft.world.level.ChunkPos pos, double p_112852_) {
            this.distAtCreation = p_112852_;
            if (pos == null) {
               this.modelData = java.util.Collections.emptyMap();
            } else {
               this.modelData = net.minecraftforge.client.model.ModelDataManager.getModelData(net.minecraft.client.Minecraft.getInstance().level, pos);
            }
         }

         public abstract CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> doTask(ChunkBufferBuilderPack pBuffers);

         public abstract void cancel();

         public int compareTo(ChunkRenderDispatcher.RenderChunk.ChunkCompileTask pOther) {
            return Doubles.compare(this.distAtCreation, pOther.distAtCreation);
         }

         public net.minecraftforge.client.model.data.IModelData getModelData(net.minecraft.core.BlockPos pos) {
            return modelData.getOrDefault(pos, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
         }
      }

      @OnlyIn(Dist.CLIENT)
      class RebuildTask extends ChunkRenderDispatcher.RenderChunk.ChunkCompileTask {
         @Nullable
         protected RenderChunkRegion region;

         @Deprecated
         public RebuildTask(@Nullable double p_112862_, RenderChunkRegion p_112863_) {
            this(null, p_112862_, p_112863_);
         }

         public RebuildTask(@Nullable net.minecraft.world.level.ChunkPos pos, double p_112862_, @Nullable RenderChunkRegion p_112863_) {
            super(pos, p_112862_);
            this.region = p_112863_;
         }

         public CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> doTask(ChunkBufferBuilderPack pBuffers) {
            if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (!RenderChunk.this.hasAllNeighbors()) {
               this.region = null;
               RenderChunk.this.setDirty(false);
               this.isCancelled.set(true);
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else {
               Vec3 vec3 = ChunkRenderDispatcher.this.getCameraPosition();
               float f = (float)vec3.x;
               float f1 = (float)vec3.y;
               float f2 = (float)vec3.z;
               ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk = new ChunkRenderDispatcher.CompiledChunk();
               Set<BlockEntity> set = this.compile(f, f1, f2, chunkrenderdispatcher$compiledchunk, pBuffers);
               RenderChunk.this.updateGlobalBlockEntities(set);
               if (this.isCancelled.get()) {
                  return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
               } else {
                  List<CompletableFuture<Void>> list = Lists.newArrayList();
                  chunkrenderdispatcher$compiledchunk.hasLayer.forEach((p_112884_) -> {
                     list.add(ChunkRenderDispatcher.this.uploadChunkLayer(pBuffers.builder(p_112884_), RenderChunk.this.getBuffer(p_112884_)));
                  });
                  return Util.sequenceFailFast(list).handle((p_112875_, p_112876_) -> {
                     if (p_112876_ != null && !(p_112876_ instanceof CancellationException) && !(p_112876_ instanceof InterruptedException)) {
                        Minecraft.getInstance().delayCrash(CrashReport.forThrowable(p_112876_, "Rendering chunk"));
                     }

                     if (this.isCancelled.get()) {
                        return ChunkRenderDispatcher.ChunkTaskResult.CANCELLED;
                     } else {
                        RenderChunk.this.compiled.set(chunkrenderdispatcher$compiledchunk);
                        return ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL;
                     }
                  });
               }
            }
         }

         private Set<BlockEntity> compile(float pX, float pY, float pZ, ChunkRenderDispatcher.CompiledChunk pCompiledChunk, ChunkBufferBuilderPack pBuffers) {
            int i = 1;
            BlockPos blockpos = RenderChunk.this.origin.immutable();
            BlockPos blockpos1 = blockpos.offset(15, 15, 15);
            VisGraph visgraph = new VisGraph();
            Set<BlockEntity> set = Sets.newHashSet();
            RenderChunkRegion renderchunkregion = this.region;
            this.region = null;
            PoseStack posestack = new PoseStack();
            if (renderchunkregion != null) {
               ModelBlockRenderer.enableCaching();
               Random random = new Random();
               BlockRenderDispatcher blockrenderdispatcher = Minecraft.getInstance().getBlockRenderer();

               for(BlockPos blockpos2 : BlockPos.betweenClosed(blockpos, blockpos1)) {
                  BlockState blockstate = renderchunkregion.getBlockState(blockpos2);
                  if (blockstate.isSolidRender(renderchunkregion, blockpos2)) {
                     visgraph.setOpaque(blockpos2);
                  }

                  if (blockstate.hasBlockEntity()) {
                     BlockEntity blockentity = renderchunkregion.getBlockEntity(blockpos2, LevelChunk.EntityCreationType.CHECK);
                     if (blockentity != null) {
                        this.handleBlockEntity(pCompiledChunk, set, blockentity);
                     }
                  }

                  FluidState fluidstate = renderchunkregion.getFluidState(blockpos2);
                  net.minecraftforge.client.model.data.IModelData modelData = getModelData(blockpos2);
                  for (RenderType rendertype : RenderType.chunkBufferLayers()) {
                     net.minecraftforge.client.ForgeHooksClient.setRenderLayer(rendertype);
                  if (!fluidstate.isEmpty() && ItemBlockRenderTypes.canRenderInLayer(fluidstate, rendertype)) {
                     BufferBuilder bufferbuilder = pBuffers.builder(rendertype);
                     if (pCompiledChunk.hasLayer.add(rendertype)) {
                        RenderChunk.this.beginLayer(bufferbuilder);
                     }

                     if (blockrenderdispatcher.renderLiquid(blockpos2, renderchunkregion, bufferbuilder, fluidstate)) {
                        pCompiledChunk.isCompletelyEmpty = false;
                        pCompiledChunk.hasBlocks.add(rendertype);
                     }
                  }

                  if (blockstate.getRenderShape() != RenderShape.INVISIBLE && ItemBlockRenderTypes.canRenderInLayer(blockstate, rendertype)) {
                     RenderType rendertype1 = rendertype;
                     BufferBuilder bufferbuilder2 = pBuffers.builder(rendertype1);
                     if (pCompiledChunk.hasLayer.add(rendertype1)) {
                        RenderChunk.this.beginLayer(bufferbuilder2);
                     }

                     posestack.pushPose();
                     posestack.translate((double)(blockpos2.getX() & 15), (double)(blockpos2.getY() & 15), (double)(blockpos2.getZ() & 15));
                     if (blockrenderdispatcher.renderBatched(blockstate, blockpos2, renderchunkregion, posestack, bufferbuilder2, true, random, modelData)) {
                        pCompiledChunk.isCompletelyEmpty = false;
                        pCompiledChunk.hasBlocks.add(rendertype1);
                     }

                     posestack.popPose();
                  }
                  }
               }
               net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);

               if (pCompiledChunk.hasBlocks.contains(RenderType.translucent())) {
                  BufferBuilder bufferbuilder1 = pBuffers.builder(RenderType.translucent());
                  bufferbuilder1.setQuadSortOrigin(pX - (float)blockpos.getX(), pY - (float)blockpos.getY(), pZ - (float)blockpos.getZ());
                  pCompiledChunk.transparencyState = bufferbuilder1.getSortState();
               }

               pCompiledChunk.hasLayer.stream().map(pBuffers::builder).forEach(BufferBuilder::end);
               ModelBlockRenderer.clearCache();
            }

            pCompiledChunk.visibilitySet = visgraph.resolve();
            return set;
         }

         private <E extends BlockEntity> void handleBlockEntity(ChunkRenderDispatcher.CompiledChunk pCompiledChunk, Set<BlockEntity> pOffScreenBlockEntitiesToRender, E pBlockEntity) {
            BlockEntityRenderer<E> blockentityrenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(pBlockEntity);
            if (blockentityrenderer != null) {
               if (blockentityrenderer.shouldRenderOffScreen(pBlockEntity)) {
                  pOffScreenBlockEntitiesToRender.add(pBlockEntity);
               }
               else pCompiledChunk.renderableBlockEntities.add(pBlockEntity); //FORGE: Fix MC-112730
            }

         }

         public void cancel() {
            this.region = null;
            if (this.isCancelled.compareAndSet(false, true)) {
               RenderChunk.this.setDirty(false);
            }

         }
      }

      @OnlyIn(Dist.CLIENT)
      class ResortTransparencyTask extends ChunkRenderDispatcher.RenderChunk.ChunkCompileTask {
         private final ChunkRenderDispatcher.CompiledChunk compiledChunk;

         @Deprecated
         public ResortTransparencyTask(double p_112889_, ChunkRenderDispatcher.CompiledChunk p_112890_) {
            this(null, p_112889_, p_112890_);
         }

         public ResortTransparencyTask(@Nullable net.minecraft.world.level.ChunkPos pos, double p_112889_, ChunkRenderDispatcher.CompiledChunk p_112890_) {
            super(pos, p_112889_);
            this.compiledChunk = p_112890_;
         }

         public CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> doTask(ChunkBufferBuilderPack pBuffers) {
            if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (!RenderChunk.this.hasAllNeighbors()) {
               this.isCancelled.set(true);
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (this.isCancelled.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else {
               Vec3 vec3 = ChunkRenderDispatcher.this.getCameraPosition();
               float f = (float)vec3.x;
               float f1 = (float)vec3.y;
               float f2 = (float)vec3.z;
               BufferBuilder.SortState bufferbuilder$sortstate = this.compiledChunk.transparencyState;
               if (bufferbuilder$sortstate != null && this.compiledChunk.hasBlocks.contains(RenderType.translucent())) {
                  BufferBuilder bufferbuilder = pBuffers.builder(RenderType.translucent());
                  RenderChunk.this.beginLayer(bufferbuilder);
                  bufferbuilder.restoreSortState(bufferbuilder$sortstate);
                  bufferbuilder.setQuadSortOrigin(f - (float)RenderChunk.this.origin.getX(), f1 - (float)RenderChunk.this.origin.getY(), f2 - (float)RenderChunk.this.origin.getZ());
                  this.compiledChunk.transparencyState = bufferbuilder.getSortState();
                  bufferbuilder.end();
                  if (this.isCancelled.get()) {
                     return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                  } else {
                     CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> completablefuture = ChunkRenderDispatcher.this.uploadChunkLayer(pBuffers.builder(RenderType.translucent()), RenderChunk.this.getBuffer(RenderType.translucent())).thenApply((p_112898_) -> {
                        return ChunkRenderDispatcher.ChunkTaskResult.CANCELLED;
                     });
                     return completablefuture.handle((p_112895_, p_112896_) -> {
                        if (p_112896_ != null && !(p_112896_ instanceof CancellationException) && !(p_112896_ instanceof InterruptedException)) {
                           Minecraft.getInstance().delayCrash(CrashReport.forThrowable(p_112896_, "Rendering chunk"));
                        }

                        return this.isCancelled.get() ? ChunkRenderDispatcher.ChunkTaskResult.CANCELLED : ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL;
                     });
                  }
               } else {
                  return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
               }
            }
         }

         public void cancel() {
            this.isCancelled.set(true);
         }
      }
   }
}
