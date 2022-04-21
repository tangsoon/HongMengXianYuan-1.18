package net.minecraft.world.level;

import java.util.Comparator;
import net.minecraft.core.BlockPos;

public class TickNextTickData<T> {
   private static long counter;
   private final T type;
   public final BlockPos pos;
   public final long triggerTick;
   public final TickPriority priority;
   private final long c;

   public TickNextTickData(BlockPos pPos, T pType) {
      this(pPos, pType, 0L, TickPriority.NORMAL);
   }

   public TickNextTickData(BlockPos pPos, T pType, long pTriggerTick, TickPriority pPriority) {
      this.c = (long)(counter++);
      this.pos = pPos.immutable();
      this.type = pType;
      this.triggerTick = pTriggerTick;
      this.priority = pPriority;
   }

   public boolean equals(Object pOther) {
      if (!(pOther instanceof TickNextTickData)) {
         return false;
      } else {
         TickNextTickData<?> ticknexttickdata = (TickNextTickData)pOther;
         return this.pos.equals(ticknexttickdata.pos) && this.type == ticknexttickdata.type;
      }
   }

   public int hashCode() {
      return this.pos.hashCode();
   }

   public static <T> Comparator<TickNextTickData<T>> createTimeComparator() {
      return Comparator.<TickNextTickData<T>>comparingLong((p_47344_) -> {
         return p_47344_.triggerTick;
      }).thenComparing((p_47342_) -> {
         return p_47342_.priority;
      }).thenComparingLong((p_47339_) -> {
         return p_47339_.c;
      });
   }

   public String toString() {
      return this.type + ": " + this.pos + ", " + this.triggerTick + ", " + this.priority + ", " + this.c;
   }

   public T getType() {
      return this.type;
   }
}