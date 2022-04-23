package by.ts.hmxy.world.dimension;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableSet;
import by.ts.hmxy.world.item.level.block.HmxyBlocks;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TheMortalTeleporter implements ITeleporter {
	public static PoiType poi = null;

	@SubscribeEvent
	public static void registerPointOfInterest(RegistryEvent.Register<PoiType> event) {
		poi = new PoiType("the_mortal_portal",
				Sets.newHashSet(ImmutableSet.copyOf(HmxyBlocks.PREVIOUS_LIFE_WATER.get().getStateDefinition().getPossibleStates())), 0, 1)
						.setRegistryName("the_mortal_portal");
		ForgeRegistries.POI_TYPES.register(poi);
	}

	public TheMortalTeleporter() {
	}
}
