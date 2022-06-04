package by.ts.hmxy.item;

import java.util.ArrayList;
import java.util.List;
import by.ts.hmxy.util.ContainLingQi;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class ReikiStoneItem extends Item implements ContainLingQi {

	public static final List<Grade> GRADES=new ArrayList<>();
	public static final Grade LOW_GRADE=Grade.create(GRADES, "reiki_stone.low_grade",false,"低级");
	public static final Grade MEDIUM_GRADE=Grade.create(GRADES, "reiki_stone.medium_grade",false,"中级");
	public static final Grade HIGH_GRADE=Grade.create(GRADES, "reiki_stone.high_grade",true,"高级");
	public static final Grade TOP_GRADE=Grade.create(GRADES, "reiki_stone.top_grade",true,"顶级");
	
	private float lingQi;
	private Grade grade;

	public ReikiStoneItem(float lingQi, Grade grade) {
		this(new Properties().stacksTo(64).tab(Tabs.SUNDRY).rarity(grade.RARITY),lingQi,grade);
	}
	
	public ReikiStoneItem(Properties pProperties, float lingQi, Grade grade) {
		super(pProperties);
		this.lingQi = lingQi;
		this.grade = grade;
	}

	public Rarity getRarity(ItemStack pStack) {
		return grade.RARITY;
	}

	public boolean isFoil(ItemStack pStack) {
		return grade.FOIL;
	}

	@Override
	public float getLingQi() {
		return lingQi;
	}

	@Override
	public void setLingQi(float lingQi) {
		
	}
}
