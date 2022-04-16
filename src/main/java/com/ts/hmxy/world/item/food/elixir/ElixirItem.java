package com.ts.hmxy.world.item.food.elixir;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.ForgeI18n;

public class ElixirItem extends Item {
	public static final List<Quality> QUALITY = new ImmutableList.Builder<Quality>()
			.add(Quality.create(ChatFormatting.GRAY, "huang")).add(Quality.create(ChatFormatting.GRAY, "xuan"))
			.add(Quality.create(ChatFormatting.GRAY, "di")).add(Quality.create(ChatFormatting.GRAY, "tian"))
			.add(Quality.create(ChatFormatting.GRAY, "fang")).add(Quality.create(ChatFormatting.GRAY, "hong"))
			.add(Quality.create(ChatFormatting.GRAY, "zhou")).add(Quality.create(ChatFormatting.GRAY, "yu")).build();

	public ElixirItem(Properties pProperties) {
		super(pProperties);
	}

	public ElixirItem() {
		super(new Properties().fireResistant().tab(null)
				.food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(0.1F).build()));
	}

	/** 丹药的品质 */
	public static final class Quality {
		public final Rarity rarity;
		private final String name;
		public final int index;
		public static int count = 0;

		private Quality(Rarity rarity, String name, int index) {
			super();
			this.rarity = rarity;
			this.name = name;
			this.index = index;
		}

		public static Quality create(ChatFormatting formatting, String name) {
			return new Quality(Rarity.create("Rarity", formatting), "elixir.quality." + name, count++);
		}

		@OnlyIn(Dist.CLIENT)
		public String getLocalName() {
			return ForgeI18n.getPattern(name);
		}
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
	
	public Rarity getRarity(ItemStack pStack) {
		Quality q= this.getQuality(pStack);
		return q!=null?q.rarity:Rarity.COMMON;
	}
	
	public Quality getQuality(ItemStack stack) {
		int qua=stack.getOrCreateTag().getInt("elixirQuality");
		return QUALITY.get(qua);
	}
}