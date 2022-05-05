package by.ts.hmxy.util;

import by.ts.hmxy.data.HmxyLanguageProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.data.loading.DatagenModLoader;

public class TransMsg {
	
	public static final TransMsg XUN_LING_FU = create("msg.xun_ling_fu","区块 (%d,%d) 灵气: %.2f");

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
	
	public static void init() {
		
	}
}
