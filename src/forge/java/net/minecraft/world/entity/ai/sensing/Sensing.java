package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class Sensing {
   private final Mob mob;
   private final List<Entity> seen = Lists.newArrayList();
   private final List<Entity> unseen = Lists.newArrayList();

   public Sensing(Mob pMob) {
      this.mob = pMob;
   }

   /**
    * Clears seen and unseen.
    */
   public void tick() {
      this.seen.clear();
      this.unseen.clear();
   }

   /**
    * Updates list of visible and not visible entities for the given entity
    */
   public boolean hasLineOfSight(Entity pEntity) {
      if (this.seen.contains(pEntity)) {
         return true;
      } else if (this.unseen.contains(pEntity)) {
         return false;
      } else {
         this.mob.level.getProfiler().push("hasLineOfSight");
         boolean flag = this.mob.hasLineOfSight(pEntity);
         this.mob.level.getProfiler().pop();
         if (flag) {
            this.seen.add(pEntity);
         } else {
            this.unseen.add(pEntity);
         }

         return flag;
      }
   }
}