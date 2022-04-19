package by.ts.hmxy.world.item;

import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.world.item.level.block.HmxyBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TestItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HmxyMod.MOD_ID);
	/** 天然灵石 */
//	public static final RegistryObject<Item> NATURE_REIKI_STONE = ITEMS.register("nature_reiki_stone",
//			() -> new Item(new Properties().tab(Tabs.SUNDRY)));
//	/** 标准灵石 */
//	public static final RegistryObject<Item> LOW_GRADE_REIKI_STONE = ITEMS.register("low_grade_reiki_stone",
//			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 1, ReikiStoneItem.GradeEnum.LOW_GRADE.index));
//	public static final RegistryObject<Item> MEDIUM_GRADE_REIKI_STONE = ITEMS.register("medium_grade_reiki_stone",
//			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 10, ReikiStoneItem.GradeEnum.MEDIUM_GRADE.index));
//	public static final RegistryObject<Item> HIGH_GRADE_REIKI_STONE = ITEMS.register("high_grade_reiki_stone",
//			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 1000, ReikiStoneItem.GradeEnum.HIGHT_GRADE.index));
//	public static final RegistryObject<Item> TOP_GRADE_REIKI_STONE = ITEMS.register("top_grade_reiki_stone",
//			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 10000, ReikiStoneItem.GradeEnum.TOP_GRADE.index));
//	/**灵石矿*/
//	public static final RegistryObject<Item> REIKI_STONE_ORE=ITEMS.register("reiki_stone_ore", ()->new BlockItem(HmxyBlocks.REIKI_STONE_ORE.get(), new Properties().tab(Tabs.ORE)));
	
	public static final RegistryObject<Item> TEST_DIMENSION=ITEMS.register("test_dimension", ()->new TestDimensionItem());
}
