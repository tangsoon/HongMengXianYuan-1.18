package com.ts.hmxy.data;

import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class HmxyRecipeProvider extends RecipeProvider {
	public HmxyRecipeProvider(DataGenerator pGenerator) {
		super(pGenerator);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(Items.LEATHER, 1).define('R', Items.ROTTEN_FLESH).define('S', Items.SLIME_BALL)
				.pattern("RSR").pattern("SRS").pattern("RSR").unlockedBy("has_rotten_flesh", has(Blocks.RAIL))
				.save(consumer);
	}		
}
