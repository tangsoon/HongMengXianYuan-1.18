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
	
	public static final ForgeConfigSpec SERVER_CONFIG = create(builder -> {
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
