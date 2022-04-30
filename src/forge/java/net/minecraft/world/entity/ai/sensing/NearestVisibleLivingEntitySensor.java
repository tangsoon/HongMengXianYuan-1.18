package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public abstract class NearestVisibleLivingEntitySensor extends Sensor<LivingEntity> {
   /**
    * @return if the second entity is hostile to the axlotl or is huntable by it
    */
   protected abstract boolean isMatchingEntity(LivingEntity pAttacker, LivingEntity pTarget);

   protected abstract MemoryModuleType<LivingEntity> getMemory();

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(this.getMemory());
   }

   protected void doTick(ServerLevel pLevel, LivingEntity pAttacker) {
      pAttacker.getBrain().setMemory(this.getMemory(), this.getNearestEntity(pAttacker));
   }

   private Optional<LivingEntity> getNearestEntity(LivingEntity pEntity) {
      return this.getVisibleEntities(pEntity).flatMap((p_148296_) -> {
         return p_148296_.stream().filter((p_148301_) -> {
            return this.isMatchingEntity(pEntity, p_148301_);
         }).min(Comparator.comparingDouble(pEntity::distanceToSqr));
      });
   }

   protected Optional<List<LivingEntity>> getVisibleEntities(LivingEntity pEntity) {
      return pEntity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
   }
}