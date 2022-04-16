package com.ts.hmxy.world.item.food.elixir;

import java.util.List;
import javax.annotation.Nullable;
import com.ts.hmxy.world.item.Grade;
import com.ts.hmxy.world.item.Tabs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ElixirItem extends Item {

	public static enum GradeEnum {
		HUANG(Grade.create(ChatFormatting.GRAY, "elixir.huang"),0),
		XUAN(Grade.create(ChatFormatting.WHITE, "elixir.xuan"),1),
		DI(Grade.create(ChatFormatting.GREEN, "elixir.di"),2),
		TIAN(Grade.create(ChatFormatting.AQUA, "elixir.tian"),3),
		FANG(Grade.create(ChatFormatting.AQUA, "elixir.fang"),4),
		HONG(Grade.create(ChatFormatting.AQUA, "elixir.hong"),5),
		ZHOU(Grade.create(ChatFormatting.AQUA, "elixir.zhou"),6),
		YU(Grade.create(ChatFormatting.AQUA, "elixir.yu"),7);

		public final Grade grade;
		public final int index;

		GradeEnum(Grade grade,int index) {
			this.grade = grade;
			this.index=index;
		}
	}
	
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
	
	public Rarity getRarity(ItemStack pStack) {
		Grade q= this.getQuality(pStack);
		return q!=null?q.rarity:Rarity.COMMON;
	}
	
	public Grade getQuality(ItemStack stack) {
		int qua=stack.getOrCreateTag().getInt("elixirQuality");
		return GradeEnum.values()[qua].grade;
	}
}