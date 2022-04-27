package by.ts.hmxy.world.item;

import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.world.item.level.block.HmxyBlocks;
import by.ts.hmxy.world.item.level.material.HmxyFluids;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class HmxyItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HmxyMod.MOD_ID);
	/** 天然灵石 */
	public static final RegistryObject<Item> NATURE_REIKI_STONE = ITEMS.register("nature_reiki_stone",
			() -> new Item(new Properties().tab(Tabs.SUNDRY)));
	/** 标准灵石 */
	public static final RegistryObject<Item> LOW_GRADE_REIKI_STONE = ITEMS.register("low_grade_reiki_stone",
			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 1, ReikiStoneItem.GradeEnum.LOW_GRADE.index));
	public static final RegistryObject<Item> MEDIUM_GRADE_REIKI_STONE = ITEMS.register("medium_grade_reiki_stone",
			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 10, ReikiStoneItem.GradeEnum.MEDIUM_GRADE.index));
	public static final RegistryObject<Item> HIGH_GRADE_REIKI_STONE = ITEMS.register("high_grade_reiki_stone",
			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 1000, ReikiStoneItem.GradeEnum.HIGHT_GRADE.index));
	public static final RegistryObject<Item> TOP_GRADE_REIKI_STONE = ITEMS.register("top_grade_reiki_stone",
			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 10000, ReikiStoneItem.GradeEnum.TOP_GRADE.index));
	/**灵石矿*/
	public static final RegistryObject<Item> REIKI_STONE_ORE=ITEMS.register("reiki_stone_ore", ()->new BlockItem(HmxyBlocks.REIKI_STONE_ORE.get(), new Properties().tab(Tabs.ORE)));
	public static final RegistryObject<Item> REIKI_STONE_ORE_FLICKER=ITEMS.register("reiki_stone_ore_flicker", ()->new BlockItem(HmxyBlocks.REIKI_STONE_ORE_FLICKER.get(), new Properties().tab(Tabs.ORE)));
	/**往生石*/
	//public static final RegistryObject<Item> PREVIOUS_LIFE_STONE=ITEMS.register("previous_life_stone", ()->new TheMortalItem());
	/**凡界传送门，往生泉*/
	public static final RegistryObject<Item> PREVIOUS_LIFE_WATER = ITEMS.register("previous_life_water",
			() -> new BlockItem(HmxyBlocks.PREVIOUS_LIFE_WATER.get(),new Properties().tab(Tabs.SUNDRY)));
	/**桶装往生泉*/
	public static final RegistryObject<Item> PREVIOUS_LIFE_WATER_BUCKET=ITEMS.register("previous_life_water_bucket", ()->{
		return new BucketItem(()->{return HmxyFluids.PREVIOUS_LIFE_WATER.get();},new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.EPIC).tab(Tabs.SUNDRY));
	});
	/**灵气瓶*/
	public static final RegistryObject<Item> MINBUS_BOTTLE = ITEMS.register("minbus_bottle",
			() -> new MinbusBottleItem(new Properties().tab(Tabs.SUNDRY).rarity(Rarity.UNCOMMON)));
	
}
