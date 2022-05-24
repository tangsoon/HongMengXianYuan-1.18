package by.ts.hmxy.item;

import by.ts.hmxy.block.LingZhiBlock;
import by.ts.hmxy.util.HmxyHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HerbHoeItem extends HoeItem {
	public HerbHoeItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
		super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
	}

	public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos,
			LivingEntity pMiningEntity) {
		super.mineBlock(pStack, pLevel, pState, pPos, pMiningEntity);
		if (pState.getBlock() instanceof LingZhiBlock lingZhiBlock && lingZhiBlock.getAge(pState) == 3) {
			ItemStack lingZhi = lingZhiBlock.getItem().createItemStack(pLevel, pPos, pState);
			HmxyHelper.dropItem(lingZhi, pLevel, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5);
			return true;
		}
		return false;
	}
}
