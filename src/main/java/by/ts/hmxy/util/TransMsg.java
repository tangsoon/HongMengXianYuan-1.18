package by.ts.hmxy.util;

import net.minecraft.network.chat.TranslatableComponent;

public class TransMsg {
	private final String key;
	public TransMsg(String key) {
		this.key=key;
	}
	
 	public TranslatableComponent create(Object... args) {
 		return new TranslatableComponent(key,args);
 	}

	public String getKey() {
		return key;
	}
}
