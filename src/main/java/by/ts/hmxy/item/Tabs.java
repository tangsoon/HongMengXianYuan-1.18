package by.ts.hmxy.item;

import java.util.function.Supplier;

import by.ts.hmxy.data.HmxyLanguageProvider;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.data.loading.DatagenModLoader;

public class Tabs {
	public static final CreativeModeTab ELIXIR = create("elixir", "丹药",()->Blocks.PEONY);
	public static final CreativeModeTab SUNDRY = create("sundry", "杂项",()->HmxyItems.LOW_GRADE_REIKI_STONE.get());
	public static final CreativeModeTab ORE = create("ore", "矿石",() -> HmxyItems.REIKI_STONE_ORE.get());
	public static final CreativeModeTab FU_LU = create("fu_lu", "符箓",() -> HmxyItems.XUN_LING_FU.get());
	public static final CreativeModeTab LING_ZHI = create("ling_zhi", "灵植",() -> HmxyItems.DENG_XIN_CAO.get());
	public static final CreativeModeTab TOOL = create("tool", "工具",() -> HmxyItems.HERB_HOE.get());
	public static final CreativeModeTab GENE = create("gene", "基因",() -> Items.DIAMOND);

	private static CreativeModeTab create(String name,String nameZh, Supplier<ItemLike> s) {
		CreativeModeTab tab=new CreativeModeTab(-1, name) {
			@Override
			public ItemStack makeIcon() {
				return new ItemStack(s.get());
			}
		};
		if(DatagenModLoader.isRunningDataGen()) {
			HmxyLanguageProvider.TAB_NAMES.put(tab, "鸿蒙仙缘："+nameZh);	
		}
		return tab;
	}
}
