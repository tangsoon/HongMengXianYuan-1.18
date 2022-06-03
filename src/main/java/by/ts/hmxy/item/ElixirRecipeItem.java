package by.ts.hmxy.item;

import by.ts.hmxy.util.HmxyHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * 丹方
 * @author tangsoon
 *
 */
public class ElixirRecipeItem extends Item{

	private NonNullList<RecipeElement> recipe;
	
	private ElixirItem result;
	
	private int resultCounot;
	
	public ElixirRecipeItem(ElixirItem result,int resultCount,RecipeElement... elements) {
		this(new Properties().tab(Tabs.ELIXIR_RECIPE).durability(128),result,resultCount,elements);
	}
	
	public ElixirRecipeItem(Properties pProperties,ElixirItem result,int resultCount,RecipeElement... elements) {
		super(pProperties);
		this.result=result;
		recipe=HmxyHelper.nonullList(9, DEFAULT_RECIPE_ELEMENT, elements);
		this.resultCounot=resultCount;
	}
	
	public RecipeElement recipeElement(int index) {
		return recipe.get(index);
	}
	
	public ElixirItem getResult() {
		return result;
	}

	public void setResult(ElixirItem result) {
		this.result = result;
	}

	public int getResultCounot() {
		return resultCounot;
	}

	public void setResultCounot(int resultCounot) {
		this.resultCounot = resultCounot;
	}



	public static class RecipeElement {
		/**灵植*/
		public final Item LING_ZHI;
		/**数量*/
		public final int COUNT;
		/**最佳含量*/
		public final float PURITY;
		public RecipeElement(Item lING_ZHI, int cOUNT, float pURITY) {
			LING_ZHI = lING_ZHI;
			COUNT = cOUNT;
			PURITY = pURITY;
		}
	}
	
	public static final RecipeElement DEFAULT_RECIPE_ELEMENT=new RecipeElement(Items.AIR, 0, 0);  
}
