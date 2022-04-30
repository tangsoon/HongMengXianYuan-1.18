package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.block.state.BlockState;

public class SingleBaseStoneSource implements BaseStoneSource {
   private final BlockState state;

   public SingleBaseStoneSource(BlockState pState) {
      this.state = pState;
   }

   public BlockState getBaseBlock(int pX, int pY, int pZ) {
      return this.state;
   }
}