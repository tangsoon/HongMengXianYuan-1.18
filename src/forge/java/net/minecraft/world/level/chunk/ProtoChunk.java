package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProtoChunk implements ChunkAccess {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkPos chunkPos;
   private volatile boolean isDirty;
   @Nullable
   private ChunkBiomeContainer biomes;
   @Nullable
   private volatile LevelLightEngine lightEngine;
   private final Map<Heightmap.Types, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Types.class);
   private volatile ChunkStatus status = ChunkStatus.EMPTY;
   private final Map<BlockPos, BlockEntity> blockEntities = Maps.newHashMap();
   private final Map<BlockPos, CompoundTag> blockEntityNbts = Maps.newHashMap();
   private final LevelChunkSection[] sections;
   private final List<CompoundTag> entities = Lists.newArrayList();
   private final List<BlockPos> lights = Lists.newArrayList();
   private final ShortList[] postProcessing;
   private final Map<StructureFeature<?>, StructureStart<?>> structureStarts = Maps.newHashMap();
   private final Map<StructureFeature<?>, LongSet> structuresRefences = Maps.newHashMap();
   private final UpgradeData upgradeData;
   private final ProtoTickList<Block> blockTicks;
   private final ProtoTickList<Fluid> liquidTicks;
   private final LevelHeightAccessor levelHeightAccessor;
   private long inhabitedTime;
   private final Map<GenerationStep.Carving, BitSet> carvingMasks = new Object2ObjectArrayMap<>();
   private volatile boolean isLightCorrect;

   public ProtoChunk(ChunkPos pPos, UpgradeData pData, LevelHeightAccessor pLevelHeightAccessor) {
      this(pPos, pData, (LevelChunkSection[])null, new ProtoTickList<>((p_63185_) -> {
         return p_63185_ == null || p_63185_.defaultBlockState().isAir();
      }, pPos, pLevelHeightAccessor), new ProtoTickList<>((p_63212_) -> {
         return p_63212_ == null || p_63212_ == Fluids.EMPTY;
      }, pPos, pLevelHeightAccessor), pLevelHeightAccessor);
   }

   public ProtoChunk(ChunkPos pPos, UpgradeData pUpgradeData, @Nullable LevelChunkSection[] pSections, ProtoTickList<Block> pBlockTicks, ProtoTickList<Fluid> pLiquidTicks, LevelHeightAccessor pLevelHeightAccessor) {
      this.chunkPos = pPos;
      this.upgradeData = pUpgradeData;
      this.blockTicks = pBlockTicks;
      this.liquidTicks = pLiquidTicks;
      this.levelHeightAccessor = pLevelHeightAccessor;
      this.sections = new LevelChunkSection[pLevelHeightAccessor.getSectionsCount()];
      if (pSections != null) {
         if (this.sections.length == pSections.length) {
            System.arraycopy(pSections, 0, this.sections, 0, this.sections.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", pSections.length, this.sections.length);
         }
      }

      this.postProcessing = new ShortList[pLevelHeightAccessor.getSectionsCount()];
   }

   public BlockState getBlockState(BlockPos pPos) {
      int i = pPos.getY();
      if (this.isOutsideBuildHeight(i)) {
         return Blocks.VOID_AIR.defaultBlockState();
      } else {
         LevelChunkSection levelchunksection = this.getSections()[this.getSectionIndex(i)];
         return LevelChunkSection.isEmpty(levelchunksection) ? Blocks.AIR.defaultBlockState() : levelchunksection.getBlockState(pPos.getX() & 15, i & 15, pPos.getZ() & 15);
      }
   }

   public FluidState getFluidState(BlockPos pPos) {
      int i = pPos.getY();
      if (this.isOutsideBuildHeight(i)) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         LevelChunkSection levelchunksection = this.getSections()[this.getSectionIndex(i)];
         return LevelChunkSection.isEmpty(levelchunksection) ? Fluids.EMPTY.defaultFluidState() : levelchunksection.getFluidState(pPos.getX() & 15, i & 15, pPos.getZ() & 15);
      }
   }

   public Stream<BlockPos> getLights() {
      return this.lights.stream();
   }

   public ShortList[] getPackedLights() {
      ShortList[] ashortlist = new ShortList[this.getSectionsCount()];

      for(BlockPos blockpos : this.lights) {
         ChunkAccess.getOrCreateOffsetList(ashortlist, this.getSectionIndex(blockpos.getY())).add(packOffsetCoordinates(blockpos));
      }

      return ashortlist;
   }

   public void addLight(short pPackedPosition, int pLightValue) {
      this.addLight(unpackOffsetCoordinates(pPackedPosition, this.getSectionYFromSectionIndex(pLightValue), this.chunkPos));
   }

   public void addLight(BlockPos pLightPos) {
      this.lights.add(pLightPos.immutable());
   }

   @Nullable
   public BlockState setBlockState(BlockPos pPos, BlockState pState, boolean pIsMoving) {
      int i = pPos.getX();
      int j = pPos.getY();
      int k = pPos.getZ();
      if (j >= this.getMinBuildHeight() && j < this.getMaxBuildHeight()) {
         int l = this.getSectionIndex(j);
         if (this.sections[l] == LevelChunk.EMPTY_SECTION && pState.is(Blocks.AIR)) {
            return pState;
         } else {
            if (pState.getLightEmission(this, pPos) > 0) {
               this.lights.add(new BlockPos((i & 15) + this.getPos().getMinBlockX(), j, (k & 15) + this.getPos().getMinBlockZ()));
            }

            LevelChunkSection levelchunksection = this.getOrCreateSection(l);
            BlockState blockstate = levelchunksection.setBlockState(i & 15, j & 15, k & 15, pState);
            if (this.status.isOrAfter(ChunkStatus.FEATURES) && pState != blockstate && (pState.getLightBlock(this, pPos) != blockstate.getLightBlock(this, pPos) || pState.getLightEmission(this, pPos) != blockstate.getLightEmission(this, pPos) || pState.useShapeForLightOcclusion() || blockstate.useShapeForLightOcclusion())) {
               this.lightEngine.checkBlock(pPos);
            }

            EnumSet<Heightmap.Types> enumset = this.getStatus().heightmapsAfter();
            EnumSet<Heightmap.Types> enumset1 = null;

            for(Heightmap.Types heightmap$types : enumset) {
               Heightmap heightmap = this.heightmaps.get(heightmap$types);
               if (heightmap == null) {
                  if (enumset1 == null) {
                     enumset1 = EnumSet.noneOf(Heightmap.Types.class);
                  }

                  enumset1.add(heightmap$types);
               }
            }

            if (enumset1 != null) {
               Heightmap.primeHeightmaps(this, enumset1);
            }

            for(Heightmap.Types heightmap$types1 : enumset) {
               this.heightmaps.get(heightmap$types1).update(i & 15, j, k & 15, pState);
            }

            return blockstate;
         }
      } else {
         return Blocks.VOID_AIR.defaultBlockState();
      }
   }

   public void setBlockEntity(BlockEntity pBlockEntity) {
      this.blockEntities.put(pBlockEntity.getBlockPos(), pBlockEntity);
   }

   public Set<BlockPos> getBlockEntitiesPos() {
      Set<BlockPos> set = Sets.newHashSet(this.blockEntityNbts.keySet());
      set.addAll(this.blockEntities.keySet());
      return set;
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pPos) {
      return this.blockEntities.get(pPos);
   }

   public Map<BlockPos, BlockEntity> getBlockEntities() {
      return this.blockEntities;
   }

   public void addEntity(CompoundTag pTag) {
      this.entities.add(pTag);
   }

   public void addEntity(Entity pEntity) {
      if (!pEntity.isPassenger()) {
         CompoundTag compoundtag = new CompoundTag();
         pEntity.save(compoundtag);
         this.addEntity(compoundtag);
      }
   }

   public List<CompoundTag> getEntities() {
      return this.entities;
   }

   public void setBiomes(ChunkBiomeContainer pBiomes) {
      this.biomes = pBiomes;
   }

   @Nullable
   public ChunkBiomeContainer getBiomes() {
      return this.biomes;
   }

   public void setUnsaved(boolean pUnsaved) {
      this.isDirty = pUnsaved;
   }

   public boolean isUnsaved() {
      return this.isDirty;
   }

   public ChunkStatus getStatus() {
      return this.status;
   }

   public void setStatus(ChunkStatus pStatus) {
      this.status = pStatus;
      this.setUnsaved(true);
   }

   public LevelChunkSection[] getSections() {
      return this.sections;
   }

   public Collection<Entry<Heightmap.Types, Heightmap>> getHeightmaps() {
      return Collections.unmodifiableSet(this.heightmaps.entrySet());
   }

   public Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types pType) {
      return this.heightmaps.computeIfAbsent(pType, (p_63253_) -> {
         return new Heightmap(this, p_63253_);
      });
   }

   public int getHeight(Heightmap.Types pType, int pX, int pZ) {
      Heightmap heightmap = this.heightmaps.get(pType);
      if (heightmap == null) {
         Heightmap.primeHeightmaps(this, EnumSet.of(pType));
         heightmap = this.heightmaps.get(pType);
      }

      return heightmap.getFirstAvailable(pX & 15, pZ & 15) - 1;
   }

   public BlockPos getHeighestPosition(Heightmap.Types pType) {
      int i = this.getMinBuildHeight();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int j = this.chunkPos.getMinBlockX(); j <= this.chunkPos.getMaxBlockX(); ++j) {
         for(int k = this.chunkPos.getMinBlockZ(); k <= this.chunkPos.getMaxBlockZ(); ++k) {
            int l = this.getHeight(pType, j & 15, k & 15);
            if (l > i) {
               i = l;
               blockpos$mutableblockpos.set(j, l, k);
            }
         }
      }

      return blockpos$mutableblockpos.immutable();
   }

   public ChunkPos getPos() {
      return this.chunkPos;
   }

   @Nullable
   public StructureStart<?> getStartForFeature(StructureFeature<?> pStructure) {
      return this.structureStarts.get(pStructure);
   }

   public void setStartForFeature(StructureFeature<?> pStructure, StructureStart<?> pStart) {
      this.structureStarts.put(pStructure, pStart);
      this.isDirty = true;
   }

   public Map<StructureFeature<?>, StructureStart<?>> getAllStarts() {
      return Collections.unmodifiableMap(this.structureStarts);
   }

   public void setAllStarts(Map<StructureFeature<?>, StructureStart<?>> pStructureStarts) {
      this.structureStarts.clear();
      this.structureStarts.putAll(pStructureStarts);
      this.isDirty = true;
   }

   public LongSet getReferencesForFeature(StructureFeature<?> pStructure) {
      return this.structuresRefences.computeIfAbsent(pStructure, (p_63260_) -> {
         return new LongOpenHashSet();
      });
   }

   public void addReferenceForFeature(StructureFeature<?> pStructure, long pReference) {
      this.structuresRefences.computeIfAbsent(pStructure, (p_63255_) -> {
         return new LongOpenHashSet();
      }).add(pReference);
      this.isDirty = true;
   }

   public Map<StructureFeature<?>, LongSet> getAllReferences() {
      return Collections.unmodifiableMap(this.structuresRefences);
   }

   public void setAllReferences(Map<StructureFeature<?>, LongSet> pStructureReferences) {
      this.structuresRefences.clear();
      this.structuresRefences.putAll(pStructureReferences);
      this.isDirty = true;
   }

   public static short packOffsetCoordinates(BlockPos pPos) {
      int i = pPos.getX();
      int j = pPos.getY();
      int k = pPos.getZ();
      int l = i & 15;
      int i1 = j & 15;
      int j1 = k & 15;
      return (short)(l | i1 << 4 | j1 << 8);
   }

   public static BlockPos unpackOffsetCoordinates(short pPackedPos, int pYOffset, ChunkPos pChunkPos) {
      int i = SectionPos.sectionToBlockCoord(pChunkPos.x, pPackedPos & 15);
      int j = SectionPos.sectionToBlockCoord(pYOffset, pPackedPos >>> 4 & 15);
      int k = SectionPos.sectionToBlockCoord(pChunkPos.z, pPackedPos >>> 8 & 15);
      return new BlockPos(i, j, k);
   }

   public void markPosForPostprocessing(BlockPos pPos) {
      if (!this.isOutsideBuildHeight(pPos)) {
         ChunkAccess.getOrCreateOffsetList(this.postProcessing, this.getSectionIndex(pPos.getY())).add(packOffsetCoordinates(pPos));
      }

   }

   public ShortList[] getPostProcessing() {
      return this.postProcessing;
   }

   public void addPackedPostProcess(short pPackedPosition, int pIndex) {
      ChunkAccess.getOrCreateOffsetList(this.postProcessing, pIndex).add(pPackedPosition);
   }

   public ProtoTickList<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public ProtoTickList<Fluid> getLiquidTicks() {
      return this.liquidTicks;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public void setInhabitedTime(long pInhabitedTime) {
      this.inhabitedTime = pInhabitedTime;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setBlockEntityNbt(CompoundTag pTag) {
      this.blockEntityNbts.put(new BlockPos(pTag.getInt("x"), pTag.getInt("y"), pTag.getInt("z")), pTag);
   }

   public Map<BlockPos, CompoundTag> getBlockEntityNbts() {
      return Collections.unmodifiableMap(this.blockEntityNbts);
   }

   public CompoundTag getBlockEntityNbt(BlockPos pPos) {
      return this.blockEntityNbts.get(pPos);
   }

   @Nullable
   public CompoundTag getBlockEntityNbtForSaving(BlockPos pPos) {
      BlockEntity blockentity = this.getBlockEntity(pPos);
      return blockentity != null ? blockentity.save(new CompoundTag()) : this.blockEntityNbts.get(pPos);
   }

   public void removeBlockEntity(BlockPos pPos) {
      this.blockEntities.remove(pPos);
      this.blockEntityNbts.remove(pPos);
   }

   @Nullable
   public BitSet getCarvingMask(GenerationStep.Carving pStep) {
      return this.carvingMasks.get(pStep);
   }

   public BitSet getOrCreateCarvingMask(GenerationStep.Carving pStep) {
      return this.carvingMasks.computeIfAbsent(pStep, (p_63251_) -> {
         return new BitSet(65536);
      });
   }

   public void setCarvingMask(GenerationStep.Carving pStep, BitSet pCarvingMask) {
      this.carvingMasks.put(pStep, pCarvingMask);
   }

   public void setLightEngine(LevelLightEngine pLightEngine) {
      this.lightEngine = pLightEngine;
   }

   public boolean isLightCorrect() {
      return this.isLightCorrect;
   }

   public void setLightCorrect(boolean pLightCorrect) {
      this.isLightCorrect = pLightCorrect;
      this.setUnsaved(true);
   }

   public int getMinBuildHeight() {
      return this.levelHeightAccessor.getMinBuildHeight();
   }

   public int getHeight() {
      return this.levelHeightAccessor.getHeight();
   }
}
