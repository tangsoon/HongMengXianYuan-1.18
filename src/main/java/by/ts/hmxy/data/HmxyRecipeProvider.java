package by.ts.hmxy.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.logging.log4j.util.TriConsumer;
import by.ts.hmxy.HmxyMod;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;

public class HmxyRecipeProvider extends RecipeProvider {
	public HmxyRecipeProvider(DataGenerator pGenerator) {
		super(pGenerator);
	}

	public static final Map<RegistryObject<Item>, TriConsumer<HmxyRecipeProvider, Item,Consumer<FinishedRecipe>>> RECIPE_GENS = new HashMap<>();
	
	@Override	
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> finishedRecip) {
		for(Map.Entry<RegistryObject<Item>, TriConsumer<HmxyRecipeProvider, Item,Consumer<FinishedRecipe>>> entry:RECIPE_GENS.entrySet()) {
			entry.getValue().accept(this, entry.getKey().get(),finishedRecip);
		}
		RECIPE_GENS.clear();
		ShapedRecipeBuilder.shaped(Items.LEATHER, 1).group(HmxyMod.MOD_ID).define('R', Items.ROTTEN_FLESH)
				.define('S', Items.SLIME_BALL).pattern("RSR").pattern("SRS").pattern("RSR")
				.unlockedBy("has_rotten_flesh", has(Items.LEATHER)).save(finishedRecip);
	}
	
	/**
	 * 这个方法不会生成任何配方
	 * @param item
	 */
	public void noRecipe(Item item,Consumer<FinishedRecipe> finished) {
		
	}
	
	/**
	 * 创建一个触发器，玩家获得对应物品时会自动触发
	 * @param itemlike
	 * @return
	 */
	public InventoryChangeTrigger.TriggerInstance hasItem(ItemLike itemlike) {
		return has(itemlike);
	}
	/**
	 * 创建一个触发器，玩家获得对应物品时会自动触发
	 * @param pTag
	 * @return
	 */
	public InventoryChangeTrigger.TriggerInstance hasItem(TagKey<Item> pTag) {
		return has(pTag);
	}
	
	/**
	 * 创建一个ShapedRecipeBuilder并完成一些固定的步骤
	 * @param <T>
	 * @param result
	 * @param count
	 * @return
	 */
	public <T extends ItemLike & IForgeRegistryEntry<?>> ShapedRecipeBuilder builder(T result,int count) {
			ShapedRecipeBuilder builder= ShapedRecipeBuilder.shaped(result, count).group(result.getRegistryName().toString());			
			return builder;
	}
}
