package net.minecraft.world.phys.shapes;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class EntityCollisionContext implements CollisionContext {
   protected static final CollisionContext EMPTY = new EntityCollisionContext(false, -Double.MAX_VALUE, ItemStack.EMPTY, ItemStack.EMPTY, (p_82891_) -> {
      return false;
   }, Optional.empty()) {
      public boolean isAbove(VoxelShape p_82898_, BlockPos p_82899_, boolean p_82900_) {
         return p_82900_;
      }
   };
   private final boolean descending;
   private final double entityBottom;
   private final ItemStack heldItem;
   private final ItemStack footItem;
   private final Predicate<Fluid> canStandOnFluid;
   private final Optional<Entity> entity;

   protected EntityCollisionContext(boolean pDescending, double pEntityBottom, ItemStack pFootItem, ItemStack pHeldItem, Predicate<Fluid> pCanStandOnFluid, Optional<Entity> pEntity) {
      this.descending = pDescending;
      this.entityBottom = pEntityBottom;
      this.footItem = pFootItem;
      this.heldItem = pHeldItem;
      this.canStandOnFluid = pCanStandOnFluid;
      this.entity = pEntity;
   }

   @Deprecated
   protected EntityCollisionContext(Entity pEntity) {
      this(pEntity.isDescending(), pEntity.getY(), pEntity instanceof LivingEntity ? ((LivingEntity)pEntity).getItemBySlot(EquipmentSlot.FEET) : ItemStack.EMPTY, pEntity instanceof LivingEntity ? ((LivingEntity)pEntity).getMainHandItem() : ItemStack.EMPTY, pEntity instanceof LivingEntity ? ((LivingEntity)pEntity)::canStandOnFluid : (p_82881_) -> {
         return false;
      }, Optional.of(pEntity));
   }

   public boolean hasItemOnFeet(Item pItem) {
      return this.footItem.is(pItem);
   }

   public boolean isHoldingItem(Item pItem) {
      return this.heldItem.is(pItem);
   }

   public boolean canStandOnFluid(FluidState pState, FlowingFluid pFlowing) {
      return this.canStandOnFluid.test(pFlowing) && !pState.getType().isSame(pFlowing);
   }

   public boolean isDescending() {
      return this.descending;
   }

   public boolean isAbove(VoxelShape pShape, BlockPos pPos, boolean pCanAscend) {
      return this.entityBottom > (double)pPos.getY() + pShape.max(Direction.Axis.Y) - (double)1.0E-5F;
   }

   public Optional<Entity> getEntity() {
      return this.entity;
   }
}