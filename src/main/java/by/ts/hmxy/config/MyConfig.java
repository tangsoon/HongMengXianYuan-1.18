package by.ts.hmxy.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MyConfig {
	public static final ForgeConfigSpec GENERAL_SPEC;
	
	public static ForgeConfigSpec.BooleanValue isToolBarOpen;
	
	static {
	    ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
	    setupConfig(configBuilder);
	    GENERAL_SPEC = configBuilder.build();
	}
	
	private static void setupConfig(ForgeConfigSpec.Builder builder) { 
		isToolBarOpen = builder.define("is_tool_bar_open", true);
	}
}

