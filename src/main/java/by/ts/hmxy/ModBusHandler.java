package by.ts.hmxy;

import by.ts.hmxy.client.renderer.entity.MinbusOrbRenderer;
import by.ts.hmxy.data.HmxyBlockStatesProvider;
import by.ts.hmxy.data.HmxyBlockTagsProvider;
import by.ts.hmxy.data.HmxyLanguageProvider;
import by.ts.hmxy.data.HmxyLootTableProvider;
import by.ts.hmxy.data.HmxyRecipeProvider;
import by.ts.hmxy.entity.HmxyEntities;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class ModBusHandler {
	
	@OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
    	event.registerEntityRenderer(HmxyEntities.MINBUS_ORB.get(),MinbusOrbRenderer::new);
    	event.registerEntityRenderer(HmxyEntities.THROWN_MINBUS_BOTTLE.get(), ThrownItemRenderer::new);
    }
	
	@SubscribeEvent
    public void dataGen(GatherDataEvent event) {
    	BlockTagsProvider blockTagsProvider=new HmxyBlockTagsProvider(event.getGenerator(),event.getExistingFileHelper());
    	BlockStateProvider blockStateProvider=new HmxyBlockStatesProvider(event.getGenerator(),event.getExistingFileHelper());
    	event.getGenerator().addProvider(blockStateProvider);
        event.getGenerator().addProvider(new HmxyRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(blockTagsProvider);
        event.getGenerator().addProvider(new HmxyLootTableProvider(event.getGenerator()));
        event.getGenerator().addProvider(new HmxyLanguageProvider(event.getGenerator(),"zh_cn"));
    }
}
