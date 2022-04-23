package by.ts.hmxy.world.item.level.material;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import by.ts.hmxy.HmxyMod;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class HmxyFluids {
	
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, HmxyMod.MOD_ID);
	public static final RegistryObject<Fluid> PREVIOUS_LIFE_WATER = FLUIDS.register("previous_life_water",
			() -> new PreviousLifeWater.Source());
	public static final RegistryObject<Fluid> PREVIOUS_LIFE_WATER_FLOWING = FLUIDS.register("previous_life_water_still",
			() -> new PreviousLifeWater.Flowing());
	
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientSideHandler {
		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {
			ItemBlockRenderTypes.setRenderLayer(PREVIOUS_LIFE_WATER.get(), renderType -> renderType == RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(PREVIOUS_LIFE_WATER_FLOWING.get(), renderType -> renderType == RenderType.translucent());
		}
	}
}
