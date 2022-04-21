package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.Fluid;
import org.apache.logging.log4j.LogManager;

public interface ChunkAccess extends BlockGetter, FeatureAccess {
   default GameEventDispatcher getEventDispatcher(int pSectionY) {
      return GameEventDispatcher.NOOP;
   }

   @Nullable
   BlockState setBlockState(BlockPos pPos, BlockState pState, boolean pIsMoving);

   void setBlockEntity(BlockEntity pBlockEntity);

   void addEntity(Entity pEntity);

   @Nullable
   default LevelChunkSection getHighestSection() {
      LevelChunkSection[] alevelchunksection = this.getSections();

      for(int i = alevelchunksection.length - 1; i >= 0; --i) {
         LevelChunkSection levelchunksection = alevelchunksection[i];
         if (!LevelChunkSection.isEmpty(levelchunksection)) {
            return levelchunksection;
         }
      }

      return null;
   }

   default int getHighestSectionPosition() {
      LevelChunkSection levelchunksection = this.getHighestSection();
      return levelchunksection == null ? this.getMinBuildHeight() : levelchunksection.bottomBlockY();
   }

   Set<BlockPos> getBlockEntitiesPos();

   LevelChunkSection[] getSections();

   default LevelChunkSection getOrCreateSection(int pIndex) {
      LevelChunkSection[] alevelchunksection = this.getSections();
      if (alevelchunksection[pIndex] == LevelChunk.EMPTY_SECTION) {
         alevelchunksection[pIndex] = new LevelChunkSection(this.getSectionYFromSectionIndex(pIndex));
      }

      return alevelchunksection[pIndex];
   }

   Collection<Entry<Heightmap.Types, Heightmap>> getHeightmaps();

   default void setHeightmap(Heightmap.Types pType, long[] pData) {
      this.getOrCreateHeightmapUnprimed(pType).setRawData(this, pType, pData);
   }

   Heightmap getOrCreateHeightmapUnprimed(Heightmap.Types pType);

   int getHeight(Heightmap.Types pType, int pX, int pZ);

   BlockPos getHeighestPosition(Heightmap.Types pType);

   ChunkPos getPos();

   Map<StructureFeature<?>, StructureStart<?>> getAllStarts();

   void setAllStarts(Map<StructureFeature<?>, StructureStart<?>> pStructureStarts);

   default boolean isYSpaceEmpty(int pStartY, int pEndY) {
      if (pStartY < this.getMinBuildHeight()) {
         pStartY = this.getMinBuildHeight();
      }

      if (pEndY >= this.getMaxBuildHeight()) {
         pEndY = this.getMaxBuildHeight() - 1;
      }

      for(int i = pStartY; i <= pEndY; i += 16) {
         if (!LevelChunkSection.isEmpty(this.getSections()[this.getSectionIndex(i)])) {
            return false;
         }
      }

      return true;
   }

   @Nullable
   ChunkBiomeContainer getBiomes();

   void setUnsaved(boolean pUnsaved);

   boolean isUnsaved();

   ChunkStatus getStatus();

   void removeBlockEntity(BlockPos pPos);

   default void markPosForPostprocessing(BlockPos pPos) {
      LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", (Object)pPos);
   }

   ShortList[] getPostProcessing();

   default void addPackedPostProcess(short pPackedPosition, int pIndex) {
      getOrCreateOffsetList(this.getPostProcessing(), pIndex).add(pPackedPosition);
   }

   default void setBlockEntityNbt(CompoundTag pTag) {
      LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
   }

   @Nullable
   CompoundTag getBlockEntityNbt(BlockPos pPos);

   @Nullable
   CompoundTag getBlockEntityNbtForSaving(BlockPos pPos);

   Stream<BlockPos> getLights();

   TickList<Block> getBlockTicks();

   TickList<Fluid> getLiquidTicks();

   UpgradeData getUpgradeData();

   void setInhabitedTime(long pInhabitedTime);

   long getInhabitedTime();

   static ShortList getOrCreateOffsetList(ShortList[] pPackedPositions, int pIndex) {
      if (pPackedPositions[pIndex] == null) {
         pPackedPositions[pIndex] = new ShortArrayList();
      }

      return pPackedPositions[pIndex];
   }

   boolean isLightCorrect();

   void setLightCorrect(boolean pLightCorrect);

   @Nullable
   default net.minecraft.world.level.LevelAccessor getWorldForge() {
      return null;
   }
}
