package by.ts.hmxy.fluid;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.level.material.Fluid;
import by.ts.hmxy.HmxyMod;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class HmxyFluids {
	
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, HmxyMod.MOD_ID);
	public static final RegistryObject<Fluid> PREVIOUS_LIFE_WATER = FLUIDS.register("previous_life_water",
			() -> new PreviousLifeWater.Source());
	public static final RegistryObject<Fluid> PREVIOUS_LIFE_WATER_FLOWING = FLUIDS.register("previous_life_water_flowing",
			() -> new PreviousLifeWater.Flowing());
}
