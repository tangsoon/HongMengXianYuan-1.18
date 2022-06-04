package by.ts.hmxy.item;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * 丹药
 * @author tangsoon
 *
 */
public class ElixirItem extends Item {
	
	public static final List<Grade> GRADES=new ArrayList<>();
	public static final Grade GRADE_NINE=Grade.create(GRADES, "reiki_stone.nine","九品");
	public static final Grade GRADE_EIGHT=Grade.create(GRADES, "reiki_stone.eight","八品");
	public static final Grade GRADE_SEVEN=Grade.create(GRADES, "reiki_stone.seven","七品");
	public static final Grade GRADE_SIX=Grade.create(GRADES, "reiki_stone.six","六品");
	public static final Grade GRADE_FIVE=Grade.create(GRADES, "reiki_stone.five","五品");
	public static final Grade GRADE_FOUR=Grade.create(GRADES, "reiki_stone.four","四品");
	public static final Grade GRADE_THREE=Grade.create(GRADES, "reiki_stone.three","三品");
	public static final Grade GRADE_TWO=Grade.create(GRADES, "reiki_stone.two","二品");
	public static final Grade GRADE_ONE=Grade.create(GRADES, "reiki_stone.one","一品");
	
	public ElixirItem(Properties pProperties) {
		super(pProperties);
	}

	public ElixirItem() {
		this(new Properties().fireResistant().tab(Tabs.ELIXIR)
				.food(new FoodProperties.Builder().alwaysEat().nutrition(1).saturationMod(0.1F).build()));
	}
	
	public static class Data{
		private Grade grade;
		public Data(ItemStack stack) {
			grade= GRADES.get(stack.getOrCreateTag().getInt("grade"));
		}
		
		public void save(ItemStack stack) {
			stack.getOrCreateTag().putInt("grade", grade.INDEX);
		}

		public Grade getGrade() {
			return grade;
		}

		public void setGrade(Grade grade) {
			this.grade = grade;
		}
	}

	public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
		super.onCraftedBy(pStack, pLevel, pPlayer);
		// TODO 实现炼丹逻辑
	}

	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
			TooltipFlag pIsAdvanced) {
		// TODO 添加丹药信息
	}

	public boolean isFoil(ItemStack pStack) {
		Data data=new Data(pStack);
		return data.getGrade().FOIL;
	}
}