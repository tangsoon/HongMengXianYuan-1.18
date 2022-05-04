package by.ts.hmxy.client;

import by.ts.hmxy.client.renderer.entity.MinbusOrbRenderer;
import by.ts.hmxy.entity.HmxyEntities;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityRenderersHandler {
	
    @SubscribeEvent
    public void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
    	event.registerEntityRenderer(HmxyEntities.MINBUS_ORB.get(),MinbusOrbRenderer::new);
    	event.registerEntityRenderer(HmxyEntities.THROWN_MINBUS_BOTTLE.get(), ThrownItemRenderer::new);
    }
}
