package by.ts.hmxy;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import by.ts.hmxy.block.HmxyBlocks;
import by.ts.hmxy.block.RenderTypes;
import by.ts.hmxy.block.blockentity.HmxyBEs;
import by.ts.hmxy.client.gui.Screens;
import by.ts.hmxy.client.hud.HmxyHud;
import by.ts.hmxy.client.key.KeyBindings;
import by.ts.hmxy.config.Configs;
import by.ts.hmxy.entity.HmxyEntities;
import by.ts.hmxy.feature.Features;
import by.ts.hmxy.fluid.HmxyFluids;
import by.ts.hmxy.item.Grade;
import by.ts.hmxy.item.HmxyItems;
import by.ts.hmxy.menu.MenuTypes;
import by.ts.hmxy.net.Messages;
import by.ts.hmxy.registry.HmxyRegistries;
import by.ts.hmxy.util.Attrs;
import by.ts.hmxy.util.ConsoleBanner;
import by.ts.hmxy.util.HmxyHelper;
import by.ts.hmxy.util.PlantTypes;
import by.ts.hmxy.util.TransMsg;

//TODO 圆林的亭子没有对称。
//TODO 硬山建筑不加载？
//TODO 灵石矿生成

@Mod(HmxyMod.MOD_ID)
@EventBusSubscriber
public class HmxyMod {
	
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "hmxy";

	public HmxyMod() {
		Configs.init();
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		HmxyItems.ITEMS.register(modEventBus);
		HmxyBlocks.BLOCKS.register(modEventBus);
		HmxyFluids.FLUIDS.register(modEventBus);
		HmxyEntities.ENTITIES.register(modEventBus);
		HmxyEntities.ITEMS.register(modEventBus);
		MenuTypes.MENU_TYPES.register(modEventBus);
		Attrs.ATTRIBUTES.register(modEventBus);
		HmxyBEs.BLOCK_ENTITIES.register(modEventBus);
		modEventBus.register(new ModBusHandler());
		modEventBus.register(new HmxyRegistries());
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::clientSetUp);
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.register(new ForgeBusHandler());
		HmxyHelper.initJingJies();
		Grade.init();
		TransMsg.init();
	}
	
	/**这个在数据生成的时候不会执行*/
	private void setup(final FMLCommonSetupEvent event) {
		Messages.register();
		ConsoleBanner.banner();
		Features.init(event);
		PlantTypes.init();
	}

	public void clientSetUp(FMLClientSetupEvent event) {
		HmxyHud.init();
		KeyBindings.init();
		Screens.init(event);
		RenderTypes.handle();
	}

	public static ResourceLocation modLoc(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
}
