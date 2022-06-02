package by.ts.hmxy.item;

import by.ts.hmxy.util.ContainLingQi;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class ReikiStoneItem extends Item implements ContainLingQi {

	private final float lingQi;
	public final int grade;

	public ReikiStoneItem(Properties pProperties, float lingQi, int grade) {
		super(pProperties);
		this.lingQi = lingQi;
		this.grade = grade;
	}

	public Rarity getRarity(ItemStack pStack) {
		return Grade.ReikiStoneGrade.values()[grade].grade.rarity;
	}

	public boolean isFoil(ItemStack pStack) {
		return grade>=2?true:false;
	}

	@Override
	public float getLingQi() {
		return lingQi;
	}

	@Override
	public void setLingQi(float lingQi) {
		
	}
}
