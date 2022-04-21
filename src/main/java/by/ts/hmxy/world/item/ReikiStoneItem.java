package by.ts.hmxy.world.item;

import net.minecraft.ChatFormatting;
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
		//这是测试1的注释。
	}

	@Override
	public float getRiki() {
		return this.reiki;
	}

	public static enum GradeEnum {
		LOW_GRADE(Grade.create(ChatFormatting.GRAY, "reiki_stone.low_grade"),0),
		MEDIUM_GRADE(Grade.create(ChatFormatting.WHITE, "reiki_stone.medium_grade"),1),
		HIGHT_GRADE(Grade.create(ChatFormatting.GREEN, "reiki_stone.high_grade"),2),
		TOP_GRADE(Grade.create(ChatFormatting.AQUA, "reiki_stone.top_grade"),3);

		public final Grade grade;
		public final int index;

		GradeEnum(Grade grade,int index) {
			this.grade = grade;
			this.index=index;
		}
	}

	public Rarity getRarity(ItemStack pStack) {
		return GradeEnum.values()[grade].grade.rarity;
	}
}
