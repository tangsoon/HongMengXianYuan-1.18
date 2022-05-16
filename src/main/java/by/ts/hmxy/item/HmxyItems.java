package by.ts.hmxy.item;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.logging.log4j.util.TriConsumer;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.block.HmxyBlocks;
import by.ts.hmxy.data.HmxyBlockStatesProvider;
import by.ts.hmxy.data.HmxyLanguageProvider;
import by.ts.hmxy.data.HmxyRecipeProvider;
import by.ts.hmxy.fluid.HmxyFluids;
import by.ts.hmxy.item.fulu.XunLingFuItem;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HmxyItems {
	// -----------------------------------------------注册--------------------------------------------------------------
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HmxyMod.MOD_ID);
	public static final RegistryObject<Item> NATURE_REIKI_STONE = register("nature_reiki_stone", "天然灵石",
			() -> new Item(new Properties().tab(Tabs.SUNDRY)), HmxyBlockStatesProvider::item,
			HmxyRecipeProvider::noRecipe);
	public static final RegistryObject<Item> LOW_GRADE_REIKI_STONE = register("low_grade_reiki_stone", "低级灵石",
			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 1, 0), HmxyBlockStatesProvider::item,
			HmxyRecipeProvider::noRecipe);
	public static final RegistryObject<Item> MEDIUM_GRADE_REIKI_STONE = register("medium_grade_reiki_stone", "中级灵石",
			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 10, 1), HmxyBlockStatesProvider::item,
			HmxyRecipeProvider::noRecipe);
	public static final RegistryObject<Item> HIGH_GRADE_REIKI_STONE = register("high_grade_reiki_stone", "高级灵石",
			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 1000, 2), HmxyBlockStatesProvider::item,
			HmxyRecipeProvider::noRecipe);
	public static final RegistryObject<Item> TOP_GRADE_REIKI_STONE = register("top_grade_reiki_stone", "顶级灵石",
			() -> new ReikiStoneItem(new Properties().tab(Tabs.SUNDRY), 10000, 3), HmxyBlockStatesProvider::item,
			HmxyRecipeProvider::noRecipe);
	public static final RegistryObject<Item> REIKI_STONE_ORE = register("reiki_stone_ore", "灵石矿",
			() -> new BlockItem(HmxyBlocks.REIKI_STONE_ORE.get(), new Properties().tab(Tabs.ORE)),
			HmxyBlockStatesProvider::itemAndBlock, HmxyRecipeProvider::noRecipe);
	public static final RegistryObject<Item> REIKI_STONE_ORE_FLICKER = register("reiki_stone_ore_flicker", "闪耀灵石",
			() -> new BlockItem(HmxyBlocks.REIKI_STONE_ORE_FLICKER.get(), new Properties().tab(Tabs.ORE)),
			HmxyBlockStatesProvider::itemAndBlock, HmxyRecipeProvider::noRecipe);
	public static final RegistryObject<Item> PREVIOUS_LIFE_WATER = register("previous_life_water", "往生泉",
			() -> new BlockItem(HmxyBlocks.PREVIOUS_LIFE_WATER.get(), new Properties().tab(Tabs.SUNDRY)),
			HmxyBlockStatesProvider::liquid, HmxyRecipeProvider::noRecipe);
	public static final RegistryObject<Item> PREVIOUS_LIFE_WATER_FLOWING = register("previous_life_water_flowing",
			"流动往生泉",
			() -> new BlockItem(HmxyBlocks.PREVIOUS_LIFE_WATER_FLOWING.get(), new Properties().tab(Tabs.SUNDRY)),
			HmxyBlockStatesProvider::liquid, HmxyRecipeProvider::noRecipe);
	public static final RegistryObject<Item> PREVIOUS_LIFE_WATER_BUCKET = register("previous_life_water_bucket",
			"桶装往生泉", () -> {
				return new BucketItem(() -> {
					return HmxyFluids.PREVIOUS_LIFE_WATER.get();
				}, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.EPIC).tab(Tabs.SUNDRY));
			}, (h, t) -> {
			}, HmxyRecipeProvider::noRecipe);
	public static final RegistryObject<Item> MINBUS_BOTTLE = register("minbus_bottle", "灵气瓶",
			() -> new MinbusBottleItem(new Properties().tab(Tabs.SUNDRY).rarity(Rarity.UNCOMMON)),
			HmxyBlockStatesProvider::item, HmxyRecipeProvider::noRecipe);
	public static final RegistryObject<Item> LING_MAI = register("ling_mai", "灵脉",
			() -> new BlockItem(HmxyBlocks.LING_MAI.get(), new Properties().tab(Tabs.ORE)),
			HmxyBlockStatesProvider::itemAndBlock, HmxyRecipeProvider::noRecipe);
	public static final RegistryObject<Item> XUN_LING_FU = register("xun_ling_fu", "寻灵符",
			() -> new XunLingFuItem(new Properties().stacksTo(64).tab(Tabs.FU_LU)), HmxyBlockStatesProvider::noModel,
			HmxyRecipeProvider::noRecipe);
	public static final RegistryObject<Item> DENG_XIN_CAO = register("deng_xin_cao", "灯心草",
			() -> new BlockItem(HmxyBlocks.DENG_XIN_CAO.get(), new Properties().tab(Tabs.LING_ZHI).stacksTo(64)),
			HmxyBlockStatesProvider::lingZhi, HmxyRecipeProvider::noRecipe);
	public static final RegistryObject<Item> HERB_HOE = register("herb_hoe", "药锄",
			() -> new HerbHoeItem(Tiers.DIAMOND, -3, 0.0F,
					(new Item.Properties()).tab(Tabs.TOOL).defaultDurability(256)),
			HmxyBlockStatesProvider::item, (h, i, f) -> {
				h.builder(i, 0).define('A', Items.IRON_INGOT).define('B', Items.IRON_INGOT).pattern("BB ")
						.pattern(" A ").pattern(" A ").unlockedBy("has_iron_ingot", h.hasItem(ItemTags.PLANKS)).save(f);
			});

	// ---------------------------------------------------------------------------------------------------------------------

	private static final RegistryObject<Item> register(String name, String nameZh, Supplier<Item> itemSupplier,
			BiConsumer<HmxyBlockStatesProvider, Item> modelGen,
			TriConsumer<HmxyRecipeProvider, Item, Consumer<FinishedRecipe>> recipeGen) {
		RegistryObject<Item> obj = ITEMS.register(name, itemSupplier);
		if (DatagenModLoader.isRunningDataGen()) {
			HmxyLanguageProvider.ITEM_NAMES.put(obj, nameZh);
			HmxyBlockStatesProvider.MODEL_HANDLERS.put(obj, modelGen);
			HmxyRecipeProvider.RECIPE_GENS.put(obj, recipeGen);
		}
		return obj;
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
