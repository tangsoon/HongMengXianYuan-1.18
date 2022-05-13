package by.ts.hmxy.config;

import java.util.function.Consumer;

import by.ts.hmxy.HmxyMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;

public class Configs {

	public static ForgeConfigSpec.BooleanValue isToolBarOpen;
	public static final ForgeConfigSpec CLIENT_CONFIG = create(builder -> {
		isToolBarOpen = builder.comment("是否启用鸿蒙仙缘的物品栏").define("is_tool_bar_open", true);
	});
	// -------------------------------------------------------------------------------------------

	public static ForgeConfigSpec.IntValue lingZhiGrowTimesLimit;
	public static ForgeConfigSpec.IntValue lingZhiDefaultMaxGrowTimes;
	public static ForgeConfigSpec.ConfigValue<Float> lingZhiGrowSpeedLimit;
	public static ForgeConfigSpec.ConfigValue<Float> lingZhiDefaultGrowSpeed;
	public static final ForgeConfigSpec COMMON_CONFIG = create(builder -> {
		lingZhiGrowTimesLimit = builder.comment("灵植生长次数上限").defineInRange("ling_zhi_grow_times_limit", 10, 0, 20);
		lingZhiDefaultMaxGrowTimes = builder.comment("灵植默认最大生长次数（这个值应该小于等于灵植生长次数上限）")
				.defineInRange("ling_zhi_default_grow_times", 5, 0, 20);
		lingZhiGrowSpeedLimit = builder.comment("灵植生长生长速度上限").define("ling_zhi_grow_speed_limit", Float.valueOf(0.1F));
		lingZhiDefaultGrowSpeed = builder.comment("灵植默认生长速度").define("ling_zhi_default_grow_speed",
				Float.valueOf(0.01F));
	});
	// -------------------------------------------------------------------------------------------
	public static ForgeConfigSpec.ConfigValue<Float> lingMaiDiffusion;
	public static ForgeConfigSpec.ConfigValue<Float> chunkLingQiFlowRate;
	public static ForgeConfigSpec.ConfigValue<Float> chunkLingQiDisappearRate;
	public static final ForgeConfigSpec SERVER_CONFIG = create(builder -> {
		lingMaiDiffusion = builder.comment("灵脉每个随机刻能释放多少灵气").define("ling_mai_diffusion", Float.valueOf(1.0F));
		chunkLingQiFlowRate = builder.comment("每个tick区块灵气基于灵气差值向周围流动的比率").define("chunk_ling_qi_flow_rate",
				Float.valueOf(1.0F / 2400.0F));
		chunkLingQiDisappearRate = builder.comment("灵脉每个随机刻能释放多少灵气").define("ling_mai_diffusion",
				Float.valueOf(1.0F / 2400F));
	});

	public static void init() {
		FileUtils.getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve(HmxyMod.MOD_ID), HmxyMod.MOD_ID);
		ModLoadingContext context = ModLoadingContext.get();
		register(context, ModConfig.Type.CLIENT, CLIENT_CONFIG, "client_config.toml");
		register(context, ModConfig.Type.COMMON, COMMON_CONFIG, "common_config.toml");
		register(context, ModConfig.Type.SERVER, SERVER_CONFIG, "server_config.toml");
	}

	private static ForgeConfigSpec create(Consumer<ForgeConfigSpec.Builder> con) {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		con.accept(builder);
		return builder.build();
	}

	private static void register(ModLoadingContext context, ModConfig.Type type, ForgeConfigSpec config, String name) {
		context.registerConfig(type, config, HmxyMod.MOD_ID + "/" + name);
	}
}
