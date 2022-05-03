package by.ts.hmxy.item;

import java.util.Optional;
import java.util.function.Supplier;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.block.HmxyBlocks;
import by.ts.hmxy.fluid.HmxyFluids;
import by.ts.hmxy.item.fulu.LingFuItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HmxyItems {
	// -----------------------------------------------注册--------------------------------------------------------------
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HmxyMod.MOD_ID);
	/** 天然灵石 */
	public static final RegistryObject<Item> NATURE_REIKI_STONE = register("nature_reiki_stone",
			() -> new Item(new Properties().tab(Tabs.SUNDRY)));
	/** 标准灵石 */
	public static final RegistryObject<Item> LOW_GRADE_REIKI_STONE = register("low_grade_reiki_stone",
			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 1, ReikiStoneItem.GradeEnum.LOW_GRADE.index));
	public static final RegistryObject<Item> MEDIUM_GRADE_REIKI_STONE = register("medium_grade_reiki_stone",
			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 10,
					ReikiStoneItem.GradeEnum.MEDIUM_GRADE.index));
	public static final RegistryObject<Item> HIGH_GRADE_REIKI_STONE = register("high_grade_reiki_stone",
			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 1000,
					ReikiStoneItem.GradeEnum.HIGHT_GRADE.index));
	public static final RegistryObject<Item> TOP_GRADE_REIKI_STONE = register("top_grade_reiki_stone",
			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 10000,
					ReikiStoneItem.GradeEnum.TOP_GRADE.index));
	/** 灵石矿 */
	public static final RegistryObject<Item> REIKI_STONE_ORE = register("reiki_stone_ore",
			() -> new BlockItem(HmxyBlocks.REIKI_STONE_ORE.get(), new Properties().tab(Tabs.ORE)));
	/** 闪耀灵石矿 */
	public static final RegistryObject<Item> REIKI_STONE_ORE_FLICKER = register("reiki_stone_ore_flicker",
			() -> new BlockItem(HmxyBlocks.REIKI_STONE_ORE_FLICKER.get(), new Properties().tab(Tabs.ORE)));
	/** 往生石 */
	// public static final RegistryObject<Item>
	// PREVIOUS_LIFE_STONE=ITEMS.register("previous_life_stone", ()->new
	// TheMortalItem());
	/** 凡界传送门，往生泉 */
	public static final RegistryObject<Item> PREVIOUS_LIFE_WATER = register("previous_life_water",
			() -> new BlockItem(HmxyBlocks.PREVIOUS_LIFE_WATER.get(), new Properties().tab(Tabs.SUNDRY)));
	/** 桶装往生泉 */
	public static final RegistryObject<Item> PREVIOUS_LIFE_WATER_BUCKET = register("previous_life_water_bucket", () -> {
		return new BucketItem(() -> {
			return HmxyFluids.PREVIOUS_LIFE_WATER.get();
		}, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.EPIC).tab(Tabs.SUNDRY));
	});
	/** 灵气瓶 */
	public static final RegistryObject<Item> MINBUS_BOTTLE = register("minbus_bottle",
			() -> new MinbusBottleItem(new Properties().tab(Tabs.SUNDRY).rarity(Rarity.UNCOMMON)));
	/** 灵脉 */
	public static final RegistryObject<Item> LING_MAI = register("ling_mai",
			() -> new BlockItem(HmxyBlocks.LING_MAI.get(), new Properties().tab(Tabs.ORE)));
	/** 灵符 */
	public static final RegistryObject<Item> LING_FU = register("ling_fu",
			() -> new LingFuItem(new Properties().stacksTo(64).tab(Tabs.FU_LU)));
	// ---------------------------------------------------------------------------------------------------------------------
	
	private static final RegistryObject<Item> register(String name, Supplier<Item> s) {
		return ITEMS.register(name, s);
	}

	/** 复制一个Item的Properties */
	@SuppressWarnings({ "deprecation", "unused" })
	private static Properties clonePro(Item item) {
		Properties pro = new Properties().craftRemainder(item.getCraftingRemainingItem());
		if (item.getMaxDamage() == 0) {
			pro.stacksTo(item.getMaxStackSize());
		} else {
			pro.defaultDurability(item.getMaxDamage());
		}
		if (item.isFireResistant()) {
			pro.fireResistant();
		}
		if (item.getFoodProperties() != null) {
			pro.food(item.getFoodProperties());
		}
		pro.rarity(item.getRarity(ItemStack.EMPTY));
		if (!item.isRepairable(new ItemStack(item))) {
			pro.setNoRepair();
		}
		pro.tab(Optional.ofNullable(item.getCreativeTabs().isEmpty() ? null : item.getCreativeTabs().iterator().next())
				.orElseGet(() -> Tabs.SUNDRY));
		return pro;
	}
}
