package by.ts.hmxy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeI18n;

public final class Grade {
	public final Rarity rarity;
	private final String name;

	private Grade(Rarity rarity, String name) {
		super();
		this.rarity = rarity;
		this.name = name;
	}

	public static Grade create(ChatFormatting formatting, String name) {
		return new Grade(Rarity.create("Rarity", formatting),"grade."+ name);
	}

	@OnlyIn(Dist.CLIENT)
	public String getTransferedName() {
		return ForgeI18n.getPattern(name);
	}
	
	public String getName() {
		return this.name;
	}
}