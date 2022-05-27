package by.ts.hmxy.util;

import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.data.HmxyLanguageProvider;
import by.ts.hmxy.item.gene.GeneItem;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.data.loading.DatagenModLoader;

public class TransMsg {
	
	public static final TransMsg XUN_LING_FU = create("str.xun_ling_fu","区块 (%d,%d) 灵气: %.2f");
	public static final TransMsg GENES_A = create("str.genes_a","基因序列A");
	public static final TransMsg GENES_B = create("str.genes_b","基因序列B");
	public static final TransMsg MEDICINE_BOTTLE_QUALITY = create("str.medicine_bottle_quality","质量: %d/%d");
	public static final TransMsg MEDICINE_BOTTLE_EFFECTIVE = create("str.medicine_bottle_effective","有效成分: %.2f");
	public static final TransMsg CONTAINER_MORTAR=create("container.mortar", "臼");
	public static final TransMsg CONTAINER_ELIXIR_FURNACE_ROOT=create("container.elixir_furnace_root", "炉基");
	public static final TransMsg SLIDER_LING_QI_CONSUME=create("slider.ling_qi_valve","灵气消耗: %.2f");
	
	
	private final String key;
	private TransMsg(String key) {
		this.key=key;
	}
	
 	public TranslatableComponent create(Object... args) {
 		return new TranslatableComponent(key,args);
 	}

	public String getKey() {
		return key;
	}
	
	public static TransMsg create(String key,String textZh) {
		TransMsg msg=new TransMsg(key);
		if(DatagenModLoader.isRunningDataGen()) {
			HmxyLanguageProvider.MSG_TEXT.put(msg, textZh);	
		}
		return msg;
	}
	
	public static TranslatableComponent itemLocallizedName(Item item) {
		return new TranslatableComponent("item."+HmxyMod.MOD_ID+"."+item.getRegistryName().getPath());
	}
	
	public static TranslatableComponent blockLocallizedName(Block block) {
		return new TranslatableComponent("block."+HmxyMod.MOD_ID+"."+block.getRegistryName().getPath());
	}
	
	public static MutableComponent geneDetail(GeneItem<?> gene) {
		return itemLocallizedName(gene).append(new TextComponent(": "+gene.VALUE));
	}
	
	public static void init() {
		
	}
}
