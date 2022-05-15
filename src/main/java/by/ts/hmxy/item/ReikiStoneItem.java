package by.ts.hmxy.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class ReikiStoneItem extends Item implements IReiki {

	private final float reiki;
	public final int grade;

	public ReikiStoneItem(Properties pProperties, float reiki, int grade) {
		super(pProperties);
		this.reiki = reiki;
		this.grade = grade;
	}

	@Override
	public float getRiki() {
		return this.reiki;
	}

	public Rarity getRarity(ItemStack pStack) {
		return Grade.ReikiStoneGrade.values()[grade].grade.rarity;
	}

	public boolean isFoil(ItemStack pStack) {
		return grade>=2?true:false;
	}
}
