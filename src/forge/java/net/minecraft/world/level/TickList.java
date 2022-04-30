package net.minecraft.world.level;

import net.minecraft.core.BlockPos;

public interface TickList<T> {
   boolean hasScheduledTick(BlockPos pPos, T pItem);

   default void scheduleTick(BlockPos pPos, T pItem, int pScheduledTime) {
      this.scheduleTick(pPos, pItem, pScheduledTime, TickPriority.NORMAL);
   }

   void scheduleTick(BlockPos pPos, T pObject, int pScheduledTime, TickPriority pPriority);

   /**
    * Checks if this position/item is scheduled to be updated this tick
    */
   boolean willTickThisTick(BlockPos pPos, T pObject);

   int size();
}