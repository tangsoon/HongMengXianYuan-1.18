package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class NoiseColumn {
   private final int minY;
   private final BlockState[] column;

   public NoiseColumn(int pMinY, BlockState[] pColumn) {
      this.minY = pMinY;
      this.column = pColumn;
   }

   public BlockState getBlockState(BlockPos pPos) {
      int i = pPos.getY() - this.minY;
      return i >= 0 && i < this.column.length ? this.column[i] : Blocks.AIR.defaultBlockState();
   }
}