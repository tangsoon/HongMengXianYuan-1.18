package by.ts.hmxy.tags;

import net.minecraft.core.Registry;
import net.minecraft.tags.StaticTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public class HmxyItemTags {
	/**所有灵石会被打上这个标签*/
	public static final Tag.Named<Item> REIKI_STONE = bind("reiki_stone");
	
	public static Tag.Named<Item> bind(String pName) {
		return StaticTags.create(Registry.ITEM_REGISTRY, "tags/items").bind(pName);
	}
}
