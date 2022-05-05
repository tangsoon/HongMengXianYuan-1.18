package by.ts.hmxy.item;

import by.ts.hmxy.data.HmxyLanguageProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.data.loading.DatagenModLoader;

public final class Grade {
	public final Rarity rarity;
	private final String name;

	private Grade(Rarity rarity, String name) {
		super();
		this.rarity = rarity;
		this.name = name;
	}

	public static Grade create(ChatFormatting formatting, String name,String nameZh) {
		Grade grade=new Grade(Rarity.create("Rarity", formatting),"grade."+ name);
		if(DatagenModLoader.isRunningDataGen()) {
			HmxyLanguageProvider.GRADE_NAMES.put(grade, nameZh);	
		}
		return grade;
	}

	@OnlyIn(Dist.CLIENT)
	public String getTransferedName() {
		return ForgeI18n.getPattern(name);
	}
	
	public String getName() {
		return this.name;
	}
	
//	public static enum GradeEnum {
//		HUANG(Grade.create(ChatFormatting.GRAY, "elixir.huang", "黄")),
//		XUAN(Grade.create(ChatFormatting.WHITE, "elixir.xuan", "玄")),
//		DI(Grade.create(ChatFormatting.GREEN, "elixir.di", "地")),
//		TIAN(Grade.create(ChatFormatting.AQUA, "elixir.tian", "天")),
//		FANG(Grade.create(ChatFormatting.AQUA, "elixir.fang", "荒")),
//		HONG(Grade.create(ChatFormatting.AQUA, "elixir.hong", "洪")),
//		ZHOU(Grade.create(ChatFormatting.AQUA, "elixir.zhou", "宙")),
//		YU(Grade.create(ChatFormatting.AQUA, "elixir.yu", "宇"));
//
//		public final Grade grade;
//
//		GradeEnum(Grade grade) {
//			this.grade = grade;
//		}
//	}
	
	public static enum ReikiStoneGrade {
		LOW_GRADE(Grade.create(ChatFormatting.GRAY, "reiki_stone.low_grade","低级")),
		MEDIUM_GRADE(Grade.create(ChatFormatting.WHITE, "reiki_stone.medium_grade","中级")),
		HIGHT_GRADE(Grade.create(ChatFormatting.GREEN, "reiki_stone.high_grade","高级")),
		TOP_GRADE(Grade.create(ChatFormatting.AQUA, "reiki_stone.top_grade","顶级"));

		public final Grade grade;

		ReikiStoneGrade(Grade grade) {
			this.grade = grade;
		}
	}
	
	@SuppressWarnings("unused")
	public static void init() {
		ReikiStoneGrade r=ReikiStoneGrade.LOW_GRADE;
	}
}