package by.ts.hmxy.client;

import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.client.hud.HmxyHud;
import by.ts.hmxy.client.renderer.entity.MinbusOrbRenderer;
import by.ts.hmxy.entity.HmxyEntities;
import by.ts.hmxy.key.KeyBindings;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = HmxyMod.MOD_ID, value =Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetupHandler {

	public ClientSetupHandler(FMLClientSetupEvent event) {
		IEventBus bus=MinecraftForge.EVENT_BUS;
		bus.addListener(t->HmxyHud.init());
		bus.addListener(KeyInputHandler::onKeyInput);
		KeyBindings.init();
	}
	
    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
    	event.registerEntityRenderer(HmxyEntities.MINBUS_ORB.get(),MinbusOrbRenderer::new);
    	event.registerEntityRenderer(HmxyEntities.THROWN_MINBUS_BOTTLE.get(), ThrownItemRenderer::new);
    }
}
