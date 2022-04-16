package com.ts.hmxy.world.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class Tabs {
	public static final CreativeModeTab TAB_ElIXIR = new CreativeModeTab(1, "elixir") {
		public ItemStack makeIcon() {
			//TODO 改为丹药图标
			return new ItemStack(Blocks.PEONY);
		}
	};
}
