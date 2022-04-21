package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * During world generation, adjacent chunks may be fully generated (and thus be level chunks), but are often needed in
 * proto chunk form. This wraps a completely generated chunk as a proto chunk.
 */
public class ImposterProtoChunk extends ProtoChunk {
   private final LevelChunk wrapped;

   public ImposterProtoChunk(LevelChunk pWrapped) {
      super(pWrapped.getPos(), UpgradeData.EMPTY, pWrapped);
      this.wrapped = pWrapped;
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pPos) {
      return this.wrapped.getBlockEntity(pPos);
   }

   @Nullable
   public BlockState getBlockState(BlockPos pPos) {
      return this.wrapped.getBlockState(pPos);
   }

   public FluidState getFluidState(BlockPos pPos) {
      return this.wrapped.getFluidState(pPos);
   }

   public int getMaxLightLevel() {
      return this.wrapped.getMaxLightLevel();
   }

   @Nullable
   public BlockState setBlockState(BlockPos pPos, BlockState pState, boolean pIsMoving) {
      return null;
   }

   public void setBlockEntity(BlockEntity pBlockEntity) {
   }

   public void addEntity(Entity pEntity) {
   }

   public void setStatus(ChunkStatus pStatus) {
   }

   public LevelChunkSection[] getSections() {
      return this.wrapped.getSections();
   }

   public void setHeightmap(Heightmap.Types pType, long[] pData) {
   }

   private Heightmap.Types fixType(Heightmap.Types pType) {
      if (pType == Heightmap.Types.WORLD_SURFACE_WG) {
         return Heightmap.Types.WORLD_SURFACE;
      } else {
         return pType == Heightmap.Types.OCEAN_FLOOR_WG ? Heightmap.Types.OCEAN_FLOOR : pType;
      }
   }

   public int getHeight(Heightmap.Types pType, int pX, int pZ) {
      return this.wrapped.getHeight(this.fixType(pType), pX, pZ);
   }

   public BlockPos getHeighestPosition(Heightmap.Types pType) {
      return this.wrapped.getHeighestPosition(this.fixType(pType));
   }

   public ChunkPos getPos() {
      return this.wrapped.getPos();
   }

   @Nullable
   public StructureStart<?> getStartForFeature(StructureFeature<?> pStructure) {
      return this.wrapped.getStartForFeature(pStructure);
   }

   public void setStartForFeature(StructureFeature<?> pStructure, StructureStart<?> pStart) {
   }

   public Map<StructureFeature<?>, StructureStart<?>> getAllStarts() {
      return this.wrapped.getAllStarts();
   }

   public void setAllStarts(Map<StructureFeature<?>, StructureStart<?>> pStructureStarts) {
   }

   public LongSet getReferencesForFeature(StructureFeature<?> pStructure) {
      return this.wrapped.getReferencesForFeature(pStructure);
   }

   public void addReferenceForFeature(StructureFeature<?> pStructure, long pReference) {
   }

   public Map<StructureFeature<?>, LongSet> getAllReferences() {
      return this.wrapped.getAllReferences();
   }

   public void setAllReferences(Map<StructureFeature<?>, LongSet> pStructureReferences) {
   }

   public ChunkBiomeContainer getBiomes() {
      return this.wrapped.getBiomes();
   }

   public void setUnsaved(boolean pUnsaved) {
   }

   public boolean isUnsaved() {
      return false;
   }

   public ChunkStatus getStatus() {
      return this.wrapped.getStatus();
   }

   public void removeBlockEntity(BlockPos pPos) {
   }

   public void markPosForPostprocessing(BlockPos pPos) {
   }

   public void setBlockEntityNbt(CompoundTag pTag) {
   }

   @Nullable
   public CompoundTag getBlockEntityNbt(BlockPos pPos) {
      return this.wrapped.getBlockEntityNbt(pPos);
   }

   @Nullable
   public CompoundTag getBlockEntityNbtForSaving(BlockPos pPos) {
      return this.wrapped.getBlockEntityNbtForSaving(pPos);
   }

   public void setBiomes(ChunkBiomeContainer pBiomes) {
   }

   public Stream<BlockPos> getLights() {
      return this.wrapped.getLights();
   }

   public ProtoTickList<Block> getBlockTicks() {
      return new ProtoTickList<>((p_62694_) -> {
         return p_62694_.defaultBlockState().isAir();
      }, this.getPos(), this);
   }

   public ProtoTickList<Fluid> getLiquidTicks() {
      return new ProtoTickList<>((p_62717_) -> {
         return p_62717_ == Fluids.EMPTY;
      }, this.getPos(), this);
   }

   public BitSet getCarvingMask(GenerationStep.Carving pStep) {
      throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Meaningless in this context"));
   }

   public BitSet getOrCreateCarvingMask(GenerationStep.Carving pStep) {
      throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Meaningless in this context"));
   }

   public LevelChunk getWrapped() {
      return this.wrapped;
   }

   public boolean isLightCorrect() {
      return this.wrapped.isLightCorrect();
   }

   public void setLightCorrect(boolean pLightCorrect) {
      this.wrapped.setLightCorrect(pLightCorrect);
   }
}