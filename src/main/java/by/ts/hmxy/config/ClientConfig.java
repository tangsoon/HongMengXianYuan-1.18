package by.ts.hmxy.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
	public static final ForgeConfigSpec CONFIG;
	
	public static ForgeConfigSpec.BooleanValue isToolBarOpen;

	static {
		ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
		setupConfig(configBuilder);
		CONFIG = configBuilder.build();
	}

	private static void setupConfig(ForgeConfigSpec.Builder builder) {
		isToolBarOpen = builder.comment("是否启用鸿蒙仙缘的物品栏").define("is_tool_bar_open", true);
	}
}
