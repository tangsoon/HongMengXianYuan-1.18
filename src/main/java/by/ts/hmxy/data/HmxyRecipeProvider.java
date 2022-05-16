package by.ts.hmxy.data;

import java.util.function.Consumer;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.item.HmxyItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

public class HmxyRecipeProvider extends RecipeProvider {
	public HmxyRecipeProvider(DataGenerator pGenerator) {
		super(pGenerator);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(Items.LEATHER, 1).group(HmxyMod.MOD_ID).define('R', Items.ROTTEN_FLESH)
				.define('S', Items.SLIME_BALL).pattern("RSR").pattern("SRS").pattern("RSR")
				.unlockedBy("has_rotten_flesh", has(Items.LEATHER)).save(consumer);
		ShapedRecipeBuilder.shaped(HmxyItems.HERB_HOE.get(), 1).group(HmxyMod.MOD_ID).define('A',Items.IRON_INGOT)
				.define('B', Items.IRON_INGOT).pattern("BB ").pattern(" A ").pattern(" A ")
				.unlockedBy("has_iron_ingot", has(ItemTags.PLANKS)).save(consumer);
	}
}
