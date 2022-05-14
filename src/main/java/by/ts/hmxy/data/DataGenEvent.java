package by.ts.hmxy.data;

import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class DataGenEvent {
    @SubscribeEvent
    public void dataGen(GatherDataEvent event) {
    	BlockTagsProvider blockTagsProvider=new HmxyBlockTagsProvider(event.getGenerator(),event.getExistingFileHelper());
    	BlockStateProvider blockStateProvider=new HmxyBlockStatesProvider(event.getGenerator(),event.getExistingFileHelper());
    	event.getGenerator().addProvider(blockStateProvider);
        event.getGenerator().addProvider(new HmxyRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(blockTagsProvider);
        //event.getGenerator().addProvider(new HmxyItemTagsProvider(event.getGenerator(), blockTagsProvider, event.getExistingFileHelper()));
        event.getGenerator().addProvider(new HmxyLootTableProvider(event.getGenerator()));
        event.getGenerator().addProvider(new HmxyLanguageProvider(event.getGenerator(),"zh_cn"));
        event.getGenerator().addProvider(new HmxyItemModelProvider(event.getGenerator(), event.getExistingFileHelper()));
    }
}