package by.ts.hmxy.world.item;

import org.apache.logging.log4j.util.Supplier;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class Tabs {
	
	/**丹药*/
	public static final CreativeModeTab ELIXIR = create("elixir",()->new ItemStack(Blocks.PEONY));
	
	/**杂项*/
	public static final CreativeModeTab SUNDRY = create("sundry",()->new ItemStack(HmxyItems.NATURE_REIKI_STONE.get()));
	
	/**矿石*/
	public static final CreativeModeTab ORE = create("ore",()->new ItemStack(HmxyItems.REIKI_STONE_ORE.get()));
	
	private static CreativeModeTab create(String name,Supplier<ItemStack> supplier) {
		return new CreativeModeTab(-1, name) {
			@Override
			public ItemStack makeIcon() {
				return supplier.get();
			}
		};
	}
}
