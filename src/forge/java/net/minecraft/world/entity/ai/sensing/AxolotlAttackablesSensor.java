package net.minecraft.world.entity.ai.sensing;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class AxolotlAttackablesSensor extends NearestVisibleLivingEntitySensor {
   public static final float TARGET_DETECTION_DISTANCE = 8.0F;

   /**
    * @return if the second entity is hostile to the axlotl or is huntable by it
    */
   protected boolean isMatchingEntity(LivingEntity pAttacker, LivingEntity pTarget) {
      if (Sensor.isEntityAttackable(pAttacker, pTarget) && (this.isHostileTarget(pTarget) || this.isHuntTarget(pAttacker, pTarget))) {
         return this.isClose(pAttacker, pTarget) && pTarget.isInWaterOrBubble();
      } else {
         return false;
      }
   }

   private boolean isHuntTarget(LivingEntity pAttacker, LivingEntity pTarget) {
      return !pAttacker.getBrain().hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN) && EntityTypeTags.AXOLOTL_HUNT_TARGETS.contains(pTarget.getType());
   }

   private boolean isHostileTarget(LivingEntity pTarget) {
      return EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES.contains(pTarget.getType());
   }

   private boolean isClose(LivingEntity pAttacker, LivingEntity pTarget) {
      return pTarget.distanceToSqr(pAttacker) <= 64.0D;
   }

   protected MemoryModuleType<LivingEntity> getMemory() {
      return MemoryModuleType.NEAREST_ATTACKABLE;
   }
}