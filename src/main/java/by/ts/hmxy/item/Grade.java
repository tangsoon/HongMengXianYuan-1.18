package by.ts.hmxy.item;

import java.util.List;

import com.google.common.collect.ImmutableList;

import by.ts.hmxy.data.HmxyLanguageProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.data.loading.DatagenModLoader;

public final class Grade {
	
	/**The default color of grade*/
	public static final ImmutableList<ChatFormatting> DEFAULT_FORMATTINGS=new ImmutableList.Builder<ChatFormatting>()
			.add(ChatFormatting.GRAY,ChatFormatting.WHITE,ChatFormatting.GREEN,ChatFormatting.AQUA,ChatFormatting.BLUE,ChatFormatting.LIGHT_PURPLE,ChatFormatting.YELLOW,ChatFormatting.GOLD,ChatFormatting.RED)
			.build();
	
	public final Rarity RARITY;
	public final String NAME;
	public final boolean FOIL;
	public final int INDEX;

	private Grade(Rarity rarity, String name,boolean foil,int index) {
		this.RARITY = rarity;
		this.NAME = name;
		this.FOIL=foil;
		this.INDEX=index;
	}
	
	public static Grade create(List<Grade> grades, String name,String nameZh) {
		return create(grades, name, false, nameZh);
	}

	public static Grade create(List<Grade> grades, String name,boolean foil,String nameZh) {
		int index=grades.size();
		ChatFormatting formatting=null;
		formatting=index<DEFAULT_FORMATTINGS.size()?DEFAULT_FORMATTINGS.get(index):ChatFormatting.WHITE;
		return create(grades, formatting, name, foil, nameZh);
	}
	
	public static Grade create(List<Grade> grades,ChatFormatting formatting, String name,boolean foil,String nameZh) {
		String newName="grade."+ name;
		Grade grade=new Grade(Rarity.create(newName, formatting),newName,foil,grades.size());
		if(DatagenModLoader.isRunningDataGen()) {
			HmxyLanguageProvider.GRADE_NAMES.put(grade, nameZh);	
		}
		grades.add(grade);
		return grade;
	}
	
	public String getName() {
		return this.NAME;
	}
}