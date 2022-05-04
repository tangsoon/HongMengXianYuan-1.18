package by.ts.hmxy.item;

import java.util.function.Supplier;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class Tabs {

	/** 丹药 */
	public static final CreativeModeTab ELIXIR = create("elixir", ()->Blocks.PEONY);

	/** 杂项 */
	public static final CreativeModeTab SUNDRY = create("sundry", ()->HmxyItems.LOW_GRADE_REIKI_STONE.get());

	/** 矿石 */
	public static final CreativeModeTab ORE = create("ore", () -> HmxyItems.REIKI_STONE_ORE.get());

	/** 符箓 */
	public static final CreativeModeTab FU_LU = create("fu_lu", () -> HmxyItems.XUN_LING_FU.get());

	private static CreativeModeTab create(String name, Supplier<ItemLike> s) {
		return new CreativeModeTab(-1, name) {
			@Override
			public ItemStack makeIcon() {
				return new ItemStack(s.get());
			}
		};
	}
}
