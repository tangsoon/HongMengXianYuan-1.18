package by.ts.hmxy.item;

import by.ts.hmxy.entity.ThrownMinbusBottle;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MinbusBottleItem extends Item {
   public MinbusBottleItem(Item.Properties pro) {
      super(pro);
   }

   public boolean isFoil(ItemStack pStack) {
      return true;
   }

   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      pLevel.playSound((Player)null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
      if (!pLevel.isClientSide) {
         ThrownMinbusBottle bottle = new ThrownMinbusBottle(pLevel, pPlayer);
         bottle.setItem(itemstack);
         bottle.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), -20.0F, 0.7F, 1.0F);
         pLevel.addFreshEntity(bottle);
      }

      pPlayer.awardStat(Stats.ITEM_USED.get(this));
      if (!pPlayer.getAbilities().instabuild) {
         itemstack.shrink(1);
      }
      return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
   }
}