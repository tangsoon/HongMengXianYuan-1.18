package by.ts.hmxy.item;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * 丹药
 * @author tangsoon
 *
 */
public class ElixirItem extends Item {

	public ElixirItem(Properties pProperties) {
		super(pProperties);
	}

	public ElixirItem() {
		super(new Properties().fireResistant().tab(Tabs.ELIXIR)
				.food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(0.1F).build()));
	}

	public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
		super.onCraftedBy(pStack, pLevel, pPlayer);
		// TODO 实现炼丹逻辑
	}

	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
			TooltipFlag pIsAdvanced) {
		// TODO 添加丹药信息
	}

	public boolean isFoil(ItemStack pStack) {
		return super.isFoil(pStack);
	}
}