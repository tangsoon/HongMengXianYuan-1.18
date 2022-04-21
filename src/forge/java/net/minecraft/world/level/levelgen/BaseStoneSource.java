package net.minecraft.world.level.levelgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface BaseStoneSource {
   default BlockState getBaseBlock(BlockPos pPos) {
      return this.getBaseBlock(pPos.getX(), pPos.getY(), pPos.getZ());
   }

   BlockState getBaseBlock(int pX, int pY, int pZ);
}