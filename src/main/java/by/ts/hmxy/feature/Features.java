package by.ts.hmxy.feature;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class Features {
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(LingMaiFeature::registerFeatures);
	}
}
