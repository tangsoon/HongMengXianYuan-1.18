package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public class PanicGoal extends Goal {
   protected final PathfinderMob mob;
   protected final double speedModifier;
   protected double posX;
   protected double posY;
   protected double posZ;
   protected boolean isRunning;

   public PanicGoal(PathfinderMob pMob, double pSpeedModifier) {
      this.mob = pMob;
      this.speedModifier = pSpeedModifier;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean canUse() {
      if (this.mob.getLastHurtByMob() == null && !this.mob.isOnFire()) {
         return false;
      } else {
         if (this.mob.isOnFire()) {
            BlockPos blockpos = this.lookForWater(this.mob.level, this.mob, 5, 4);
            if (blockpos != null) {
               this.posX = (double)blockpos.getX();
               this.posY = (double)blockpos.getY();
               this.posZ = (double)blockpos.getZ();
               return true;
            }
         }

         return this.findRandomPosition();
      }
   }

   protected boolean findRandomPosition() {
      Vec3 vec3 = DefaultRandomPos.getPos(this.mob, 5, 4);
      if (vec3 == null) {
         return false;
      } else {
         this.posX = vec3.x;
         this.posY = vec3.y;
         this.posZ = vec3.z;
         return true;
      }
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void start() {
      this.mob.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
      this.isRunning = true;
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void stop() {
      this.isRunning = false;
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean canContinueToUse() {
      return !this.mob.getNavigation().isDone();
   }

   @Nullable
   protected BlockPos lookForWater(BlockGetter pLevel, Entity pEntity, int pHorizontalRange, int pVerticalRange) {
      BlockPos blockpos = pEntity.blockPosition();
      int i = blockpos.getX();
      int j = blockpos.getY();
      int k = blockpos.getZ();
      float f = (float)(pHorizontalRange * pHorizontalRange * pVerticalRange * 2);
      BlockPos blockpos1 = null;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int l = i - pHorizontalRange; l <= i + pHorizontalRange; ++l) {
         for(int i1 = j - pVerticalRange; i1 <= j + pVerticalRange; ++i1) {
            for(int j1 = k - pHorizontalRange; j1 <= k + pHorizontalRange; ++j1) {
               blockpos$mutableblockpos.set(l, i1, j1);
               if (pLevel.getFluidState(blockpos$mutableblockpos).is(FluidTags.WATER)) {
                  float f1 = (float)((l - i) * (l - i) + (i1 - j) * (i1 - j) + (j1 - k) * (j1 - k));
                  if (f1 < f) {
                     f = f1;
                     blockpos1 = new BlockPos(blockpos$mutableblockpos);
                  }
               }
            }
         }
      }

      return blockpos1;
   }
}