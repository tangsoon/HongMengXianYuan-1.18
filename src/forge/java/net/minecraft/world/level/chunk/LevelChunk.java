package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ChunkTickList;
import net.minecraft.world.level.EmptyTickList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.EuclideanGameEventDispatcher;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelChunk extends net.minecraftforge.common.capabilities.CapabilityProvider<LevelChunk> implements ChunkAccess, net.minecraftforge.common.extensions.IForgeLevelChunk {
   static final Logger LOGGER = LogManager.getLogger();
   private static final TickingBlockEntity NULL_TICKER = new TickingBlockEntity() {
      public void tick() {
      }

      public boolean isRemoved() {
         return true;
      }

      public BlockPos getPos() {
         return BlockPos.ZERO;
      }

      public String getType() {
         return "<null>";
      }
   };
   @Nullable
   public static final LevelChunkSection EMPTY_SECTION = null;
   private final LevelChunkSection[] sections;
   private ChunkBiomeContainer biomes;
   private final Map<BlockPos, CompoundTag> pendingBlockEntities = Maps.newHashMap();
   private final Map<BlockPos, LevelChunk.RebindableTickingBlockEntityWrapper> tickersInLevel = Maps.newHashMap();
   private boolean loaded;
   final Level level;
   private final Map<Heightmap.Types, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Types.class);
   private final UpgradeData upgradeData;
   /** A Map of ChunkPositions to TileEntities in this chunk */
   private final Map<BlockPos, BlockEntity> blockEntities = Maps.newHashMap();
   private final Map<StructureFeature<?>, StructureStart<?>> structureStarts = Maps.newHashMap();
   private final Map<StructureFeature<?>, LongSet> structuresRefences = Maps.newHashMap();
   private final ShortList[] postProcessing;
   private TickList<Block> blockTicks;
   private TickList<Fluid> liquidTicks;
   private volatile boolean unsaved;
   /** the cumulative number of ticks players have been in this chunk */
   private long inhabitedTime;
   @Nullable
   private Supplier<ChunkHolder.FullChunkStatus> fullStatus;
   @Nullable
   private Consumer<LevelChunk> postLoad;
   private final ChunkPos chunkPos;
   private volatile boolean isLightCorrect;
   private final Int2ObjectMap<GameEventDispatcher> gameEventDispatcherSections;

   public LevelChunk(Level pLevel, ChunkPos pPos, ChunkBiomeContainer pBiomes) {
      this(pLevel, pPos, pBiomes, UpgradeData.EMPTY, EmptyTickList.empty(), EmptyTickList.empty(), 0L, (LevelChunkSection[])null, (Consumer<LevelChunk>)null);
   }

   public LevelChunk(Level pLevel, ChunkPos pPos, ChunkBiomeContainer pBiomes, UpgradeData pData, TickList<Block> pBlockTicks, TickList<Fluid> pLiquidTicks, long pInhabitedTime, @Nullable LevelChunkSection[] pSections, @Nullable Consumer<LevelChunk> pPostLoad) {
      super(LevelChunk.class);
      this.level = pLevel;
      this.chunkPos = pPos;
      this.upgradeData = pData;
      this.gameEventDispatcherSections = new Int2ObjectOpenHashMap<>();

      for(Heightmap.Types heightmap$types : Heightmap.Types.values()) {
         if (ChunkStatus.FULL.heightmapsAfter().contains(heightmap$types)) {
            this.heightmaps.put(heightmap$types, new Heightmap(this, heightmap$types));
         }
      }

      this.biomes = pBiomes;
      this.blockTicks = pBlockTicks;
      this.liquidTicks = pLiquidTicks;
      this.inhabitedTime = pInhabitedTime;
      this.postLoad = pPostLoad;
      this.sections = new LevelChunkSection[pLevel.getSectionsCount()];
      if (pSections != null) {
         if (this.sections.length == pSections.length) {
            System.arraycopy(pSections, 0, this.sections, 0, this.sections.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", pSections.length, this.sections.length);
         }
      }

      this.postProcessing = new ShortList[pLevel.getSectionsCount()];
      this.gatherCapabilities();
   }

   public LevelChunk(ServerLevel pLevel, ProtoChunk pChunk, @Nullable Consumer<LevelChunk> pPostLoad) {
      this(pLevel, pChunk.getPos(), pChunk.getBiomes(), pChunk.getUpgradeData(), pChunk.getBlockTicks(), pChunk.getLiquidTicks(), pChunk.getInhabitedTime(), pChunk.getSections(), pPostLoad);

      for(BlockEntity blockentity : pChunk.getBlockEntities().values()) {
         this.setBlockEntity(blockentity);
      }

      this.pendingBlockEntities.putAll(pChunk.getBlockEntityNbts());

      for(int i = 0; i < pChunk.getPostProcessing().length; ++i) {
         this.postProcessing[i] = pChunk.getPostProcessing()[i];
      }

      this.setAllStarts(pChunk.getAllStarts());
      this.setAllReferences(pChunk.getAllReferences());

      for(Entry<Heightmap.Types, Heightmap> entry : pChunk.getHeightmaps()) {
         if (ChunkStatus.FULL.heightmapsAfter().contains(entry.getKey())) {
            this.setHeightmap(entry.getKey(), entry.getValue().getRawData());
         }
      }

      this.setLightCorrect(pChunk.isLightCorrect());
      this.unsaved = true;
   }

   public GameEventDispatcher getEventDispatcher(int pSectionY) {
      return this.gameEventDispatcherSections.computeIfAbsent(pSectionY, (p_156395_) -> {
         return new EuclideanGameEventDispatcher(this.level);
      });
   }

   public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types pType) {
      return this.heightmaps.computeIfAbsent(pType, (p_62908_) -> {
         return new Heightmap(this, p_62908_);
      });
   }

   public Set<BlockPos> getBlockEntitiesPos() {
      Set<BlockPos> set = Sets.newHashSet(this.pendingBlockEntities.keySet());
      set.addAll(this.blockEntities.keySet());
      return set;
   }

   public LevelChunkSection[] getSections() {
      return this.sections;
   }

   public BlockState getBlockState(BlockPos pPos) {
      int i = pPos.getX();
      int j = pPos.getY();
      int k = pPos.getZ();
      if (this.level.isDebug()) {
         BlockState blockstate = null;
         if (j == 60) {
            blockstate = Blocks.BARRIER.defaultBlockState();
         }

         if (j == 70) {
            blockstate = DebugLevelSource.getBlockStateFor(i, k);
         }

         return blockstate == null ? Blocks.AIR.defaultBlockState() : blockstate;
      } else {
         try {
            int l = this.getSectionIndex(j);
            if (l >= 0 && l < this.sections.length) {
               LevelChunkSection levelchunksection = this.sections[l];
               if (!LevelChunkSection.isEmpty(levelchunksection)) {
                  return levelchunksection.getBlockState(i & 15, j & 15, k & 15);
               }
            }

            return Blocks.AIR.defaultBlockState();
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting block state");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being got");
            crashreportcategory.setDetail("Location", () -> {
               return CrashReportCategory.formatLocation(this, i, j, k);
            });
            throw new ReportedException(crashreport);
         }
      }
   }

   public FluidState getFluidState(BlockPos pPos) {
      return this.getFluidState(pPos.getX(), pPos.getY(), pPos.getZ());
   }

   public FluidState getFluidState(int pX, int pY, int pZ) {
      try {
         int i = this.getSectionIndex(pY);
         if (i >= 0 && i < this.sections.length) {
            LevelChunkSection levelchunksection = this.sections[i];
            if (!LevelChunkSection.isEmpty(levelchunksection)) {
               return levelchunksection.getFluidState(pX & 15, pY & 15, pZ & 15);
            }
         }

         return Fluids.EMPTY.defaultFluidState();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting fluid state");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Block being got");
         crashreportcategory.setDetail("Location", () -> {
            return CrashReportCategory.formatLocation(this, pX, pY, pZ);
         });
         throw new ReportedException(crashreport);
      }
   }

   @Nullable
   public BlockState setBlockState(BlockPos pPos, BlockState pState, boolean pIsMoving) {
      int i = pPos.getY();
      int j = this.getSectionIndex(i);
      LevelChunkSection levelchunksection = this.sections[j];
      if (levelchunksection == EMPTY_SECTION) {
         if (pState.isAir()) {
            return null;
         }

         levelchunksection = new LevelChunkSection(SectionPos.blockToSectionCoord(i));
         this.sections[j] = levelchunksection;
      }

      boolean flag = levelchunksection.isEmpty();
      int k = pPos.getX() & 15;
      int l = i & 15;
      int i1 = pPos.getZ() & 15;
      BlockState blockstate = levelchunksection.setBlockState(k, l, i1, pState);
      if (blockstate == pState) {
         return null;
      } else {
         Block block = pState.getBlock();
         this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING).update(k, i, i1, pState);
         this.heightmaps.get(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES).update(k, i, i1, pState);
         this.heightmaps.get(Heightmap.Types.OCEAN_FLOOR).update(k, i, i1, pState);
         this.heightmaps.get(Heightmap.Types.WORLD_SURFACE).update(k, i, i1, pState);
         boolean flag1 = levelchunksection.isEmpty();
         if (flag != flag1) {
            this.level.getChunkSource().getLightEngine().updateSectionStatus(pPos, flag1);
         }

         boolean flag2 = blockstate.hasBlockEntity();
         if (!this.level.isClientSide) {
            blockstate.onRemove(this.level, pPos, pState, pIsMoving);
         } else if ((!blockstate.is(block) || !pState.hasBlockEntity()) && flag2) {
            this.removeBlockEntity(pPos);
         }

         if (!levelchunksection.getBlockState(k, l, i1).is(block)) {
            return null;
         } else {
            if (!this.level.isClientSide && !this.level.captureBlockSnapshots) {
               pState.onPlace(this.level, pPos, blockstate, pIsMoving);
            }

            if (pState.hasBlockEntity()) {
               BlockEntity blockentity = this.getBlockEntity(pPos, LevelChunk.EntityCreationType.CHECK);
               if (blockentity == null) {
                  blockentity = ((EntityBlock)block).newBlockEntity(pPos, pState);
                  if (blockentity != null) {
                     this.addAndRegisterBlockEntity(blockentity);
                  }
               } else {
                  blockentity.setBlockState(pState);
                  this.updateBlockEntityTicker(blockentity);
               }
            }

            this.unsaved = true;
            return blockstate;
         }
      }
   }

   @Deprecated
   public void addEntity(Entity pEntity) {
   }

   public int getHeight(Heightmap.Types pType, int pX, int pZ) {
      return this.heightmaps.get(pType).getFirstAvailable(pX & 15, pZ & 15) - 1;
   }

   public BlockPos getHeighestPosition(Heightmap.Types pType) {
      ChunkPos chunkpos = this.getPos();
      int i = this.getMinBuildHeight();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int j = chunkpos.getMinBlockX(); j <= chunkpos.getMaxBlockX(); ++j) {
         for(int k = chunkpos.getMinBlockZ(); k <= chunkpos.getMaxBlockZ(); ++k) {
            int l = this.getHeight(pType, j & 15, k & 15);
            if (l > i) {
               i = l;
               blockpos$mutableblockpos.set(j, l, k);
            }
         }
      }

      return blockpos$mutableblockpos.immutable();
   }

   @Nullable
   private BlockEntity createBlockEntity(BlockPos pPos) {
      BlockState blockstate = this.getBlockState(pPos);
      return !blockstate.hasBlockEntity() ? null : ((EntityBlock)blockstate.getBlock()).newBlockEntity(pPos, blockstate);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pPos) {
      return this.getBlockEntity(pPos, LevelChunk.EntityCreationType.CHECK);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pPos, LevelChunk.EntityCreationType pCreationType) {
      BlockEntity blockentity = this.blockEntities.get(pPos);
      if (blockentity != null && blockentity.isRemoved()) {
         blockEntities.remove(pPos);
         blockentity = null;
      }
      if (blockentity == null) {
         CompoundTag compoundtag = this.pendingBlockEntities.remove(pPos);
         if (compoundtag != null) {
            BlockEntity blockentity1 = this.promotePendingBlockEntity(pPos, compoundtag);
            if (blockentity1 != null) {
               return blockentity1;
            }
         }
      }

      if (blockentity == null) {
         if (pCreationType == LevelChunk.EntityCreationType.IMMEDIATE) {
            blockentity = this.createBlockEntity(pPos);
            if (blockentity != null) {
               this.addAndRegisterBlockEntity(blockentity);
            }
         }
      }

      return blockentity;
   }

   public void addAndRegisterBlockEntity(BlockEntity pBlockEntity) {
      this.setBlockEntity(pBlockEntity);
      if (this.isInLevel()) {
         this.addGameEventListener(pBlockEntity);
         this.updateBlockEntityTicker(pBlockEntity);
         pBlockEntity.onLoad();
      }

   }

   private boolean isInLevel() {
      return this.loaded || this.level.isClientSide();
   }

   boolean isTicking(BlockPos pPos) {
      if (!this.level.getWorldBorder().isWithinBounds(pPos)) {
         return false;
      } else if (!(this.level instanceof ServerLevel)) {
         return true;
      } else {
         return this.getFullStatus().isOrAfter(ChunkHolder.FullChunkStatus.TICKING) && ((ServerLevel)this.level).areEntitiesLoaded(ChunkPos.asLong(pPos));
      }
   }

   public void setBlockEntity(BlockEntity pBlockEntity) {
      BlockPos blockpos = pBlockEntity.getBlockPos();
      if (this.getBlockState(blockpos).hasBlockEntity()) {
         pBlockEntity.setLevel(this.level);
         pBlockEntity.clearRemoved();
         BlockEntity blockentity = this.blockEntities.put(blockpos.immutable(), pBlockEntity);
         if (blockentity != null && blockentity != pBlockEntity) {
            blockentity.setRemoved();
         }

      }
   }

   public void setBlockEntityNbt(CompoundTag pTag) {
      this.pendingBlockEntities.put(new BlockPos(pTag.getInt("x"), pTag.getInt("y"), pTag.getInt("z")), pTag);
   }

   @Nullable
   public CompoundTag getBlockEntityNbtForSaving(BlockPos pPos) {
      BlockEntity blockentity = this.getBlockEntity(pPos);
      if (blockentity != null && !blockentity.isRemoved()) {
         try {
         CompoundTag compoundtag1 = blockentity.save(new CompoundTag());
         compoundtag1.putBoolean("keepPacked", false);
         return compoundtag1;
         } catch (Exception e) {
            LogManager.getLogger().error("A BlockEntity type {} has thrown an exception trying to write state. It will not persist, Report this to the mod author", blockentity.getClass().getName(), e);
            return null;
         }
      } else {
         CompoundTag compoundtag = this.pendingBlockEntities.get(pPos);
         if (compoundtag != null) {
            compoundtag = compoundtag.copy();
            compoundtag.putBoolean("keepPacked", true);
         }

         return compoundtag;
      }
   }

   public void removeBlockEntity(BlockPos pPos) {
      if (this.isInLevel()) {
         BlockEntity blockentity = this.blockEntities.remove(pPos);
         if (blockentity != null) {
            this.removeGameEventListener(blockentity);
            blockentity.setRemoved();
         }
      }

      this.removeBlockEntityTicker(pPos);
   }

   private <T extends BlockEntity> void removeGameEventListener(T pBlockEntity) {
      if (!this.level.isClientSide) {
         Block block = pBlockEntity.getBlockState().getBlock();
         if (block instanceof EntityBlock) {
            GameEventListener gameeventlistener = ((EntityBlock)block).getListener(this.level, pBlockEntity);
            if (gameeventlistener != null) {
               int i = SectionPos.blockToSectionCoord(pBlockEntity.getBlockPos().getY());
               GameEventDispatcher gameeventdispatcher = this.getEventDispatcher(i);
               gameeventdispatcher.unregister(gameeventlistener);
               if (gameeventdispatcher.isEmpty()) {
                  this.gameEventDispatcherSections.remove(i);
               }
            }
         }

      }
   }

   private void removeBlockEntityTicker(BlockPos pPos) {
      LevelChunk.RebindableTickingBlockEntityWrapper levelchunk$rebindabletickingblockentitywrapper = this.tickersInLevel.remove(pPos);
      if (levelchunk$rebindabletickingblockentitywrapper != null) {
         levelchunk$rebindabletickingblockentitywrapper.rebind(NULL_TICKER);
      }

   }

   public void runPostLoad() {
      if (this.postLoad != null) {
         this.postLoad.accept(this);
         this.postLoad = null;
      }

   }

   public void markUnsaved() {
      this.unsaved = true;
   }

   public boolean isEmpty() {
      return false;
   }

   public ChunkPos getPos() {
      return this.chunkPos;
   }

   public void replaceWithPacketData(@Nullable ChunkBiomeContainer pBiomes, FriendlyByteBuf pBuffer, CompoundTag pTag, BitSet pData) {
      boolean flag = pBiomes != null;
      if (flag) {
         this.blockEntities.values().forEach(this::onBlockEntityRemove);
         this.blockEntities.clear();
      } else {
         this.blockEntities.values().removeIf((p_156390_) -> {
            int j = this.getSectionIndex(p_156390_.getBlockPos().getY());
            if (pData.get(j)) {
               p_156390_.setRemoved();
               return true;
            } else {
               return false;
            }
         });
      }

      for(int i = 0; i < this.sections.length; ++i) {
         LevelChunkSection levelchunksection = this.sections[i];
         if (!pData.get(i)) {
            if (flag && levelchunksection != EMPTY_SECTION) {
               this.sections[i] = EMPTY_SECTION;
            }
         } else {
            if (levelchunksection == EMPTY_SECTION) {
               levelchunksection = new LevelChunkSection(this.getSectionYFromSectionIndex(i));
               this.sections[i] = levelchunksection;
            }

            levelchunksection.read(pBuffer);
         }
      }

      if (pBiomes != null) {
         this.biomes = pBiomes;
      }

      for(Heightmap.Types heightmap$types : Heightmap.Types.values()) {
         String s = heightmap$types.getSerializationKey();
         if (pTag.contains(s, 12)) {
            this.setHeightmap(heightmap$types, pTag.getLongArray(s));
         }
      }

   }

   private void onBlockEntityRemove(BlockEntity p_156401_) {
      p_156401_.setRemoved();
      this.tickersInLevel.remove(p_156401_.getBlockPos());
   }

   public ChunkBiomeContainer getBiomes() {
      return this.biomes;
   }

   public void setLoaded(boolean pLoaded) {
      this.loaded = pLoaded;
   }

   public Level getLevel() {
      return this.level;
   }

   public Collection<Entry<Heightmap.Types, Heightmap>> getHeightmaps() {
      return Collections.unmodifiableSet(this.heightmaps.entrySet());
   }

   public Map<BlockPos, BlockEntity> getBlockEntities() {
      return this.blockEntities;
   }

   public CompoundTag getBlockEntityNbt(BlockPos pPos) {
      return this.pendingBlockEntities.get(pPos);
   }

   public Stream<BlockPos> getLights() {
      return StreamSupport.stream(BlockPos.betweenClosed(this.chunkPos.getMinBlockX(), this.getMinBuildHeight(), this.chunkPos.getMinBlockZ(), this.chunkPos.getMaxBlockX(), this.getMaxBuildHeight() - 1, this.chunkPos.getMaxBlockZ()).spliterator(), false).filter((p_156419_) -> {
         return this.getBlockState(p_156419_).getLightEmission(getLevel(), p_156419_) != 0;
      });
   }

   public TickList<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public TickList<Fluid> getLiquidTicks() {
      return this.liquidTicks;
   }

   public void setUnsaved(boolean pUnsaved) {
      this.unsaved = pUnsaved;
   }

   public boolean isUnsaved() {
      return this.unsaved;
   }

   @Nullable
   public StructureStart<?> getStartForFeature(StructureFeature<?> pStructure) {
      return this.structureStarts.get(pStructure);
   }

   public void setStartForFeature(StructureFeature<?> pStructure, StructureStart<?> pStart) {
      this.structureStarts.put(pStructure, pStart);
   }

   public Map<StructureFeature<?>, StructureStart<?>> getAllStarts() {
      return this.structureStarts;
   }

   public void setAllStarts(Map<StructureFeature<?>, StructureStart<?>> pStructureStarts) {
      this.structureStarts.clear();
      this.structureStarts.putAll(pStructureStarts);
   }

   public LongSet getReferencesForFeature(StructureFeature<?> pStructure) {
      return this.structuresRefences.computeIfAbsent(pStructure, (p_156403_) -> {
         return new LongOpenHashSet();
      });
   }

   public void addReferenceForFeature(StructureFeature<?> pStructure, long pReference) {
      this.structuresRefences.computeIfAbsent(pStructure, (p_156399_) -> {
         return new LongOpenHashSet();
      }).add(pReference);
   }

   public Map<StructureFeature<?>, LongSet> getAllReferences() {
      return this.structuresRefences;
   }

   public void setAllReferences(Map<StructureFeature<?>, LongSet> pStructureReferences) {
      this.structuresRefences.clear();
      this.structuresRefences.putAll(pStructureReferences);
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setInhabitedTime(long pInhabitedTime) {
      this.inhabitedTime = pInhabitedTime;
   }

   public void postProcessGeneration() {
      ChunkPos chunkpos = this.getPos();

      for(int i = 0; i < this.postProcessing.length; ++i) {
         if (this.postProcessing[i] != null) {
            for(Short oshort : this.postProcessing[i]) {
               BlockPos blockpos = ProtoChunk.unpackOffsetCoordinates(oshort, this.getSectionYFromSectionIndex(i), chunkpos);
               BlockState blockstate = this.getBlockState(blockpos);
               BlockState blockstate1 = Block.updateFromNeighbourShapes(blockstate, this.level, blockpos);
               this.level.setBlock(blockpos, blockstate1, 20);
            }

            this.postProcessing[i].clear();
         }
      }

      this.unpackTicks();

      for(BlockPos blockpos1 : ImmutableList.copyOf(this.pendingBlockEntities.keySet())) {
         this.getBlockEntity(blockpos1);
      }

      this.pendingBlockEntities.clear();
      this.upgradeData.upgrade(this);
   }

   @Nullable
   private BlockEntity promotePendingBlockEntity(BlockPos pPos, CompoundTag pTag) {
      BlockState blockstate = this.getBlockState(pPos);
      BlockEntity blockentity;
      if ("DUMMY".equals(pTag.getString("id"))) {
         if (blockstate.hasBlockEntity()) {
            blockentity = ((EntityBlock)blockstate.getBlock()).newBlockEntity(pPos, blockstate);
         } else {
            blockentity = null;
            LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", pPos, blockstate);
         }
      } else {
         blockentity = BlockEntity.loadStatic(pPos, blockstate, pTag);
      }

      if (blockentity != null) {
         blockentity.setLevel(this.level);
         this.addAndRegisterBlockEntity(blockentity);
      } else {
         LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", blockstate, pPos);
      }

      return blockentity;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public ShortList[] getPostProcessing() {
      return this.postProcessing;
   }

   public void unpackTicks() {
      if (this.blockTicks instanceof ProtoTickList) {
         ((ProtoTickList)this.blockTicks).copyOut(this.level.getBlockTicks(), (p_156417_) -> {
            return this.getBlockState((BlockPos)p_156417_).getBlock();
         });
         this.blockTicks = EmptyTickList.empty();
      } else if (this.blockTicks instanceof ChunkTickList) {
         ((ChunkTickList)this.blockTicks).copyOut(this.level.getBlockTicks());
         this.blockTicks = EmptyTickList.empty();
      }

      if (this.liquidTicks instanceof ProtoTickList) {
         ((ProtoTickList)this.liquidTicks).copyOut(this.level.getLiquidTicks(), (p_156415_) -> {
            return this.getFluidState((BlockPos)p_156415_).getType();
         });
         this.liquidTicks = EmptyTickList.empty();
      } else if (this.liquidTicks instanceof ChunkTickList) {
         ((ChunkTickList)this.liquidTicks).copyOut(this.level.getLiquidTicks());
         this.liquidTicks = EmptyTickList.empty();
      }

   }

   public void packTicks(ServerLevel pLevel) {
      if (this.blockTicks == EmptyTickList.<Block>empty()) {
         this.blockTicks = new ChunkTickList<>(Registry.BLOCK::getKey, pLevel.getBlockTicks().fetchTicksInChunk(this.chunkPos, true, false), pLevel.getGameTime());
         this.setUnsaved(true);
      }

      if (this.liquidTicks == EmptyTickList.<Fluid>empty()) {
         this.liquidTicks = new ChunkTickList<>(Registry.FLUID::getKey, pLevel.getLiquidTicks().fetchTicksInChunk(this.chunkPos, true, false), pLevel.getGameTime());
         this.setUnsaved(true);
      }

   }

   public int getMinBuildHeight() {
      return this.level.getMinBuildHeight();
   }

   public int getHeight() {
      return this.level.getHeight();
   }

   public ChunkStatus getStatus() {
      return ChunkStatus.FULL;
   }

   public ChunkHolder.FullChunkStatus getFullStatus() {
      return this.fullStatus == null ? ChunkHolder.FullChunkStatus.BORDER : this.fullStatus.get();
   }

   public void setFullStatus(Supplier<ChunkHolder.FullChunkStatus> pFullStatus) {
      this.fullStatus = pFullStatus;
   }

   public boolean isLightCorrect() {
      return this.isLightCorrect;
   }

   public void setLightCorrect(boolean pLightCorrect) {
      this.isLightCorrect = pLightCorrect;
      this.setUnsaved(true);
   }

   public void invalidateAllBlockEntities() {
      this.blockEntities.values().forEach(BlockEntity::onChunkUnloaded);
      this.blockEntities.values().forEach(this::onBlockEntityRemove);
   }

   public void registerAllBlockEntitiesAfterLevelLoad() {
      this.level.addFreshBlockEntities(this.blockEntities.values());
      this.blockEntities.values().forEach((p_156409_) -> {
         this.addGameEventListener(p_156409_);
         this.updateBlockEntityTicker(p_156409_);
      });
   }

   private <T extends BlockEntity> void addGameEventListener(T pBlockEntity) {
      if (!this.level.isClientSide) {
         Block block = pBlockEntity.getBlockState().getBlock();
         if (block instanceof EntityBlock) {
            GameEventListener gameeventlistener = ((EntityBlock)block).getListener(this.level, pBlockEntity);
            if (gameeventlistener != null) {
               GameEventDispatcher gameeventdispatcher = this.getEventDispatcher(SectionPos.blockToSectionCoord(pBlockEntity.getBlockPos().getY()));
               gameeventdispatcher.register(gameeventlistener);
            }
         }

      }
   }

   private <T extends BlockEntity> void updateBlockEntityTicker(T pBlockEntity) {
      BlockState blockstate = pBlockEntity.getBlockState();
      BlockEntityTicker<T> blockentityticker = (BlockEntityTicker<T>)blockstate.getTicker(this.level, pBlockEntity.getType());
      if (blockentityticker == null) {
         this.removeBlockEntityTicker(pBlockEntity.getBlockPos());
      } else {
         this.tickersInLevel.compute(pBlockEntity.getBlockPos(), (p_156381_, p_156382_) -> {
            TickingBlockEntity tickingblockentity = this.createTicker(pBlockEntity, blockentityticker);
            if (p_156382_ != null) {
               p_156382_.rebind(tickingblockentity);
               return p_156382_;
            } else if (this.isInLevel()) {
               LevelChunk.RebindableTickingBlockEntityWrapper levelchunk$rebindabletickingblockentitywrapper = new LevelChunk.RebindableTickingBlockEntityWrapper(tickingblockentity);
               this.level.addBlockEntityTicker(levelchunk$rebindabletickingblockentitywrapper);
               return levelchunk$rebindabletickingblockentitywrapper;
            } else {
               return null;
            }
         });
      }

   }

   private <T extends BlockEntity> TickingBlockEntity createTicker(T pBlockEntity, BlockEntityTicker<T> pTicker) {
      return new LevelChunk.BoundTickingBlockEntity<>(pBlockEntity, pTicker);
   }

   class BoundTickingBlockEntity<T extends BlockEntity> implements TickingBlockEntity {
      private final T blockEntity;
      private final BlockEntityTicker<T> ticker;
      private boolean loggedInvalidBlockState;

      BoundTickingBlockEntity(T p_156433_, BlockEntityTicker<T> p_156434_) {
         this.blockEntity = p_156433_;
         this.ticker = p_156434_;
      }

      public void tick() {
         if (!this.blockEntity.isRemoved() && this.blockEntity.hasLevel()) {
            BlockPos blockpos = this.blockEntity.getBlockPos();
            if (LevelChunk.this.isTicking(blockpos)) {
               try {
                  ProfilerFiller profilerfiller = LevelChunk.this.level.getProfiler();
                  net.minecraftforge.server.timings.TimeTracker.BLOCK_ENTITY_UPDATE.trackStart(blockEntity);
                  profilerfiller.push(this::getType);
                  BlockState blockstate = LevelChunk.this.getBlockState(blockpos);
                  if (this.blockEntity.getType().isValid(blockstate)) {
                     this.ticker.tick(LevelChunk.this.level, this.blockEntity.getBlockPos(), blockstate, this.blockEntity);
                     this.loggedInvalidBlockState = false;
                  } else if (!this.loggedInvalidBlockState) {
                     this.loggedInvalidBlockState = true;
                     LevelChunk.LOGGER.warn("Block entity {} @ {} state {} invalid for ticking:", this::getType, this::getPos, () -> {
                        return blockstate;
                     });
                  }

                  profilerfiller.pop();
               } catch (Throwable throwable) {
                  CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking block entity");
                  CrashReportCategory crashreportcategory = crashreport.addCategory("Block entity being ticked");
                  this.blockEntity.fillCrashReportCategory(crashreportcategory);

                  if (net.minecraftforge.common.ForgeConfig.SERVER.removeErroringBlockEntities.get()) {
                     LogManager.getLogger().fatal("{}", crashreport.getFriendlyReport());
                     blockEntity.setRemoved();
                     LevelChunk.this.removeBlockEntity(blockEntity.getBlockPos());
                  } else
                  throw new ReportedException(crashreport);
               }
            }
         }

      }

      public boolean isRemoved() {
         return this.blockEntity.isRemoved();
      }

      public BlockPos getPos() {
         return this.blockEntity.getBlockPos();
      }

      public String getType() {
         return BlockEntityType.getKey(this.blockEntity.getType()).toString();
      }

      public String toString() {
         return "Level ticker for " + this.getType() + "@" + this.getPos();
      }
   }

   public static enum EntityCreationType {
      IMMEDIATE,
      QUEUED,
      CHECK;
   }

   /**
    * <strong>FOR INTERNAL USE ONLY</strong>
    * <p>
    * Only public for use in {@link net.minecraft.world.level.chunk.storage.ChunkSerializer}.
    */
   @java.lang.Deprecated
   @javax.annotation.Nullable
   public final CompoundTag writeCapsToNBT() {
      return this.serializeCaps();
   }

   /**
    * <strong>FOR INTERNAL USE ONLY</strong>
    * <p>
    * Only public for use in {@link net.minecraft.world.level.chunk.storage.ChunkSerializer}.
    */
   @java.lang.Deprecated
   public final void readCapsFromNBT(CompoundTag tag) {
      this.deserializeCaps(tag);
   }

   @Override
   public Level getWorldForge() {
      return getLevel();
   }

   class RebindableTickingBlockEntityWrapper implements TickingBlockEntity {
      private TickingBlockEntity ticker;

      RebindableTickingBlockEntityWrapper(TickingBlockEntity p_156447_) {
         this.ticker = p_156447_;
      }

      void rebind(TickingBlockEntity pTicker) {
         this.ticker = pTicker;
      }

      public void tick() {
         this.ticker.tick();
      }

      public boolean isRemoved() {
         return this.ticker.isRemoved();
      }

      public BlockPos getPos() {
         return this.ticker.getPos();
      }

      public String getType() {
         return this.ticker.getType();
      }

      public String toString() {
         return this.ticker.toString() + " <wrapped>";
      }
   }
}
