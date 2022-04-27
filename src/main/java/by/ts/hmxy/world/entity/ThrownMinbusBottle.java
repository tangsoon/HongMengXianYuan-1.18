package by.ts.hmxy.world.entity;

import by.ts.hmxy.world.item.HmxyItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ThrownMinbusBottle extends ThrowableItemProjectile {
   public ThrownMinbusBottle(EntityType<? extends ThrownMinbusBottle> type, Level level) {
      super(type, level);
   }

   public ThrownMinbusBottle(Level level, LivingEntity livingEntity_) {
      super(HmxyEntities.THROWN_MINBUS_BOTTLE.get(), livingEntity_, level);
   }

   public ThrownMinbusBottle(Level level, double p_37514_, double p_37515_, double p_37516_) {
      super(HmxyEntities.THROWN_MINBUS_BOTTLE.get(), p_37514_, p_37515_, p_37516_, level);
   }

   protected Item getDefaultItem() {
      return HmxyItems.MINBUS_BOTTLE.get();
   }

   protected float getGravity() {
      return 0.07F;
   }

   protected void onHit(HitResult pResult) {
      super.onHit(pResult);
      if (this.level instanceof ServerLevel) {
         this.level.levelEvent(2002, this.blockPosition(), PotionUtils.getColor(Potions.WATER));
         int i = 3 + this.level.random.nextInt(5) + this.level.random.nextInt(5);
         MinbusOrb.award((ServerLevel)this.level, this.position(), i);
         this.discard();
      }
   }
}