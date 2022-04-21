
package by.ts.hmxy.world.level.biome;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.ArrayList;
import by.ts.hmxy.HmxyMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class HmxyModBiomes {
	private static final List<Biome> REGISTRY = new ArrayList<>();
	public static Biome TEST_BIOME = register("test_biome", TestBiomeBiome.createBiome());

	private static Biome register(String registryname, Biome biome) {
		REGISTRY.add(biome.setRegistryName(new ResourceLocation(HmxyMod.MOD_ID, registryname)));
		return biome;
	}

	@SubscribeEvent
	public static void registerBiomes(RegistryEvent.Register<Biome> event) {
		event.getRegistry().registerAll(REGISTRY.toArray(new Biome[0]));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			TestBiomeBiome.init();
		});
	}
}
