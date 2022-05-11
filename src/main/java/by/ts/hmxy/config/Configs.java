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
	
	public static final ForgeConfigSpec COMMON_CONFIG = create(builder -> {
		
	});
	
	public static ForgeConfigSpec.ConfigValue<Float> lingMaiDiffusion;
	public static ForgeConfigSpec.ConfigValue<Float> chunkLingQiFlowRate;
	public static ForgeConfigSpec.ConfigValue<Float> chunkLingQiDisappearRate;
	public static final ForgeConfigSpec SERVER_CONFIG = create(builder -> {
		lingMaiDiffusion= builder.comment("灵脉每个随机刻能释放多少灵气").define("ling_mai_diffusion",Float.valueOf(1.0F));
		chunkLingQiFlowRate= builder.comment("每个tick区块灵气基于灵气差值向周围流动的比率").define("chunk_ling_qi_flow_rate",Float.valueOf(1.0F/2400.0F));
		chunkLingQiDisappearRate= builder.comment("灵脉每个随机刻能释放多少灵气").define("ling_mai_diffusion",Float.valueOf(1.0F/2400F));
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
