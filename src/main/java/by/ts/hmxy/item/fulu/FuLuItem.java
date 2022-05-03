package by.ts.hmxy.item.fulu;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class FuLuItem extends Item {
	public FuLuItem(Properties pProperties) {
		super(pProperties);
	}

	public abstract InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand);
}
