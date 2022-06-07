package by.ts.hmxy.item;

import by.ts.hmxy.item.ElixirRecipeItem.RecipeElement;
import net.minecraft.core.NonNullList;

public interface ElixirRecipe {

	RecipeElement recipeElement(int index);

	ElixirItem getResult();

	int getResultCounot();

	NonNullList<RecipeElement> getRecipe();

}