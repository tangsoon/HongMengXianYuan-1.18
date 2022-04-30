package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class ComparatorBlockEntity extends BlockEntity {
   private int output;

   public ComparatorBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(BlockEntityType.COMPARATOR, pWorldPosition, pBlockState);
   }

   public CompoundTag save(CompoundTag pCompound) {
      super.save(pCompound);
      pCompound.putInt("OutputSignal", this.output);
      return pCompound;
   }

   public void load(CompoundTag pTag) {
      super.load(pTag);
      this.output = pTag.getInt("OutputSignal");
   }

   public int getOutputSignal() {
      return this.output;
   }

   public void setOutputSignal(int pOutput) {
      this.output = pOutput;
   }
}