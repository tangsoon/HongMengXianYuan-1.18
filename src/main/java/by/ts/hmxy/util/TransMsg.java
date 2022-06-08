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
	public static final TransMsg CONTAINER_MORTAR=create("screen.mortar", "臼");
	public static final TransMsg CONTAINER_ELIXIR_FURNACE_ROOT=create("screen.elixir_furnace_root", "炉基");
	public static final TransMsg CONTAINER_ELIXIR_FURNACE=create("screen.elixir_furnace", "炼丹炉");
	public static final TransMsg SLIDER_LING_QI_CONSUME=create("str.ling_qi_valve","灵气消耗: %f");
//	public static final TransMsg ELIXIR_FURNACE_TEMPERATURE=create("str.elixir_furnace_temperature","温度");
//	public static final TransMsg ELIXIR_FURNACE_DURATION=create("str.elixir_furnace_duration","耐久");
//	public static final TransMsg ELIXIR_FURNACE_HARDNESS=create("str.elixir_furnace_hardness","硬度");
	public static final TransMsg PROGRESS_BAR=create("progress_bar", "进度条");
	public static final TransMsg ELIXIR_FURNACE_PROGRESS_TEMPERATURE=create("str.elixir_furnace_progress_temperature","温度: %.2f");
	public static final TransMsg ELIXIR_FURNACE_PROGRESS_DURATION=create("str.elixir_furnace_progress_duration","耐久: %.2f");
	public static final TransMsg ELIXIR_FURNACE_PROGRESS_HARDNESS=create("str.elixir_furnace_progress_hardness","硬度: %.2f");
	public static final TransMsg ELIXIR_FURNACE_LING_ZHI_TIP=create("str.elixir_furnace_ling_zhi_tip","灵植");
	public static final TransMsg ELIXIR_FURNACE_BOTTLE_TIP=create("str.elixir_furnace_bottle_tip","药罐");
	public static final TransMsg ELIXIR_FURNACE_EXTRACT_PROGRESS=create("str.elixir_furnace_extract_progress","提炼进度: %.2f");
	public static final TransMsg ELIXIR_FURNACE_COVER=create("str.elixir_furnace_cover","炉盖");
	public static final TransMsg ELIXIR_FURNACE_RECIPE=create("str.elixir_furnace_recipe","丹方");
	public static final TransMsg ELIXIR_FURNACE_ELIXIR=create("str.elixir_furnace_elixir","丹药");
	
	public static final TextComponent EMPTY=new TextComponent("");
	public static final TransMsg DEFAULT=create("defualt", "默认文字");
	
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
