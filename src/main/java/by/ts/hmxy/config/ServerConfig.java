package by.ts.hmxy.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
	public static final ForgeConfigSpec CONFIG;
	
	public static ForgeConfigSpec.ConfigValue<Float> lingLiConsumPerTickWhenSprinting;
	
	static {
		ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
		setupConfig(configBuilder);
		CONFIG = configBuilder.build();
	}

	private static void setupConfig(ForgeConfigSpec.Builder builder) {
		lingLiConsumPerTickWhenSprinting=builder.define("ling_li_consume_per_tick_when_sprinting", Float.valueOf(0.05F));
	}
}
