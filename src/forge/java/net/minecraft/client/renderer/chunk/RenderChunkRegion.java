package net.minecraft.client.renderer.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderChunkRegion implements BlockAndTintGetter {
   protected final int centerX;
   protected final int centerZ;
   protected final BlockPos start;
   protected final int xLength;
   protected final int yLength;
   protected final int zLength;
   protected final LevelChunk[][] chunks;
   protected final BlockState[] blockStates;
   protected final Level level;

   /**
    * @return a new {@code RenderChunkRegion}, or {@code null} if the chunk is empty (only contains air)
    */
   @Nullable
   public static RenderChunkRegion createIfNotEmpty(Level pLevel, BlockPos pFrom, BlockPos pTo, int pPadding) {
      int i = SectionPos.blockToSectionCoord(pFrom.getX() - pPadding);
      int j = SectionPos.blockToSectionCoord(pFrom.getZ() - pPadding);
      int k = SectionPos.blockToSectionCoord(pTo.getX() + pPadding);
      int l = SectionPos.blockToSectionCoord(pTo.getZ() + pPadding);
      LevelChunk[][] alevelchunk = new LevelChunk[k - i + 1][l - j + 1];

      for(int i1 = i; i1 <= k; ++i1) {
         for(int j1 = j; j1 <= l; ++j1) {
            alevelchunk[i1 - i][j1 - j] = pLevel.getChunk(i1, j1);
         }
      }

      if (isAllEmpty(pFrom, pTo, i, j, alevelchunk)) {
         return null;
      } else {
         int k1 = 1;
         BlockPos blockpos1 = pFrom.offset(-1, -1, -1);
         BlockPos blockpos = pTo.offset(1, 1, 1);
         return new RenderChunkRegion(pLevel, i, j, alevelchunk, blockpos1, blockpos);
      }
   }

   public static boolean isAllEmpty(BlockPos pFrom, BlockPos pTo, int pX, int pZ, LevelChunk[][] pChunks) {
      for(int i = SectionPos.blockToSectionCoord(pFrom.getX()); i <= SectionPos.blockToSectionCoord(pTo.getX()); ++i) {
         for(int j = SectionPos.blockToSectionCoord(pFrom.getZ()); j <= SectionPos.blockToSectionCoord(pTo.getZ()); ++j) {
            LevelChunk levelchunk = pChunks[i - pX][j - pZ];
            if (!levelchunk.isYSpaceEmpty(pFrom.getY(), pTo.getY())) {
               return false;
            }
         }
      }

      return true;
   }

   public RenderChunkRegion(Level pLevel, int pCenterX, int pCenterZ, LevelChunk[][] pChunks, BlockPos pStart, BlockPos pEnd) {
      this.level = pLevel;
      this.centerX = pCenterX;
      this.centerZ = pCenterZ;
      this.chunks = pChunks;
      this.start = pStart;
      this.xLength = pEnd.getX() - pStart.getX() + 1;
      this.yLength = pEnd.getY() - pStart.getY() + 1;
      this.zLength = pEnd.getZ() - pStart.getZ() + 1;
      this.blockStates = new BlockState[this.xLength * this.yLength * this.zLength];

      for(BlockPos blockpos : BlockPos.betweenClosed(pStart, pEnd)) {
         int i = SectionPos.blockToSectionCoord(blockpos.getX()) - pCenterX;
         int j = SectionPos.blockToSectionCoord(blockpos.getZ()) - pCenterZ;
         LevelChunk levelchunk = pChunks[i][j];
         int k = this.index(blockpos);
         this.blockStates[k] = levelchunk.getBlockState(blockpos);
      }

   }

   protected final int index(BlockPos pPos) {
      return this.index(pPos.getX(), pPos.getY(), pPos.getZ());
   }

   protected int index(int pX, int pY, int pZ) {
      int i = pX - this.start.getX();
      int j = pY - this.start.getY();
      int k = pZ - this.start.getZ();
      return k * this.xLength * this.yLength + j * this.xLength + i;
   }

   public BlockState getBlockState(BlockPos pPos) {
      return this.blockStates[this.index(pPos)];
   }

   public FluidState getFluidState(BlockPos pPos) {
      return this.blockStates[this.index(pPos)].getFluidState();
   }

   public float getShade(Direction pDirection, boolean pShade) {
      return this.level.getShade(pDirection, pShade);
   }

   public LevelLightEngine getLightEngine() {
      return this.level.getLightEngine();
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pPos) {
      return this.getBlockEntity(pPos, LevelChunk.EntityCreationType.IMMEDIATE);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pPos, LevelChunk.EntityCreationType pCreationType) {
      int i = SectionPos.blockToSectionCoord(pPos.getX()) - this.centerX;
      int j = SectionPos.blockToSectionCoord(pPos.getZ()) - this.centerZ;
      return this.chunks[i][j].getBlockEntity(pPos, pCreationType);
   }

   public int getBlockTint(BlockPos pPos, ColorResolver pColorResolver) {
      return this.level.getBlockTint(pPos, pColorResolver);
   }

   public int getMinBuildHeight() {
      return this.level.getMinBuildHeight();
   }

   public int getHeight() {
      return this.level.getHeight();
   }
}