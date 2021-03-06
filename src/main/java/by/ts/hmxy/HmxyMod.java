package by.ts.hmxy;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import by.ts.hmxy.block.HmxyBlocks;
import by.ts.hmxy.client.ClientSetupHandler;
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
//TODO bug:chunkinfo错误
@Mod("hmxy")
@EventBusSubscriber
public class HmxyMod {

	private static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "hmxy";

	public HmxyMod() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
		HmxyItems.ITEMS.register(modEventBus);
		HmxyBlocks.BLOCKS.register(modEventBus);
		HmxyFluids.FLUIDS.register(modEventBus);
		HmxyEntities.ENTITIES.register(modEventBus);
		HmxyEntities.ITEMS.register(modEventBus);
		Attrs.ATTRIBUTES.register(modEventBus);

		FileUtils.getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve(HmxyMod.MOD_ID), HmxyMod.MOD_ID);
		ModLoadingContext modLoadingContext = ModLoadingContext.get();
		LOGGER.info("开始读取配置");
		modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CONFIG,
				HmxyMod.MOD_ID + "/client_config.toml");
		modLoadingContext.registerConfig(ModConfig.Type.COMMON, CommonConfig.CONFIG,
				HmxyMod.MOD_ID + "/common_config.toml");
		modLoadingContext.registerConfig(ModConfig.Type.SERVER, CommonConfig.CONFIG,
				HmxyMod.MOD_ID + "/server_config.toml");
		LOGGER.info("读取配置结束");
		HmxyHelper.initJingJies();

		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.register(this);
		forgeBus.addListener(EventPriority.HIGH, ClientSetupHandler::new);

		new Thread(() -> {
			InputStream inStream = ClassLoader.getSystemResourceAsStream("data/hmxy/console_banner.txt");
			if (inStream != null) {
				InputStreamReader in = new InputStreamReader(inStream);
				char[] cs = new char[64];
				try {
					while ((in.read(cs)) != -1) {
						System.out.print(cs);
					}
					System.out.println();
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				HmxyMod.LOGGER.warn("未能正确加载横幅，信仰崩塌！！！");
			}
		}).run();

		Class<?> testModClass;
		String testModName = "by.ts.hmxy.HmxyModTest";
		try {
			testModClass = Class.forName(testModName);
			testModClass.getConstructor().newInstance();
			LOGGER.info("启用测试类: " + testModName);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			LOGGER.info("未启用测试类: " + testModName);
		}
	}

	private void setup(final FMLCommonSetupEvent event) {
		Messages.register();
	}

	public static ResourceLocation modLoc(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
	
	
}
