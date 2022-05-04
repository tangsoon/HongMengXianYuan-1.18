package by.ts.hmxy.capability;

import by.ts.hmxy.HmxyMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AttachCapabilitiesHandler {
	@SubscribeEvent
	public void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<LevelChunk> event) {
		if (!event.getObject().getCapability(ChunkInfoProvider.CAPABILITY).isPresent()&&!event.getObject().getLevel().isClientSide) {
			event.addCapability(new ResourceLocation(HmxyMod.MOD_ID, "chunk_info"), new ChunkInfoProvider(event.getObject()));
		}
	}
}
