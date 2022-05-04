package by.ts.hmxy;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import by.ts.hmxy.block.HmxyBlocks;
import by.ts.hmxy.capability.AttachCapabilitiesHandler;
import by.ts.hmxy.chunk.ChunkEventHandler;
import by.ts.hmxy.client.EntityRenderersHandler;
import by.ts.hmxy.client.hud.HmxyHud;
import by.ts.hmxy.client.hud.RenderOverlayHander;
import by.ts.hmxy.client.key.KeyBindings;
import by.ts.hmxy.config.ClientConfig;
import by.ts.hmxy.config.CommonConfig;
import by.ts.hmxy.entity.HmxyEntities;
import by.ts.hmxy.fluid.HmxyFluids;
import by.ts.hmxy.item.HmxyItems;
import by.ts.hmxy.network.Messages;
import by.ts.hmxy.util.Attrs;
import by.ts.hmxy.util.HmxyHelper;

//TODO 圆林的亭子没有对称。
//TODO 硬山建筑不加载？
//TODO tesr；
//TODO bug: 耐力不会消耗消耗
//TODO 绘画模组的图标，背景图标
//TODO 灵石矿生成
//TODO 灵气的流动
//TODO 灵符
//TODO 灵植方块
@Mod("hmxy")
@EventBusSubscriber
public class HmxyMod {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "hmxy";

	public HmxyMod() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		HmxyItems.ITEMS.register(modEventBus);
		HmxyBlocks.BLOCKS.register(modEventBus);
		HmxyFluids.FLUIDS.register(modEventBus);
		HmxyEntities.ENTITIES.register(modEventBus);
		HmxyEntities.ITEMS.register(modEventBus);
		Attrs.ATTRIBUTES.register(modEventBus);
		modEventBus.register(new EntityRenderersHandler());
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::clientSetUp);
		
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.register(this);
		forgeBus.register(new ChunkEventHandler());
		forgeBus.register(new AttachCapabilitiesHandler());
		forgeBus.register(new RenderOverlayHander());
	}

	private void setup(final FMLCommonSetupEvent event) {
		FileUtils.getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve(HmxyMod.MOD_ID), HmxyMod.MOD_ID);
		ModLoadingContext modLoadingContext = ModLoadingContext.get();
		modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CONFIG,
				HmxyMod.MOD_ID + "/client_config.toml");
		modLoadingContext.registerConfig(ModConfig.Type.COMMON, CommonConfig.CONFIG,
				HmxyMod.MOD_ID + "/common_config.toml");
		modLoadingContext.registerConfig(ModConfig.Type.SERVER, CommonConfig.CONFIG,
				HmxyMod.MOD_ID + "/server_config.toml");
		HmxyHelper.initJingJies();
		Messages.register();
		ConsoleBanner.banner();
	}
	
	public void clientSetUp(FMLClientSetupEvent event) {
		HmxyHud.init();
		KeyBindings.init();
	}

	public static ResourceLocation modLoc(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
	
	public static IEventBus forgeBus() {
		return MinecraftForge.EVENT_BUS;
	}
	
	public static IEventBus modBus() {
		return FMLJavaModLoadingContext.get().getModEventBus();
	}
}
