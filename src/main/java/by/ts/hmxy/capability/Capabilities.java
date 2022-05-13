package by.ts.hmxy.capability;

import by.ts.hmxy.HmxyMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Capabilities {
	
	public static final Capability<HmxyChunkInfo> CHUNK_INFO = CapabilityManager.get(new CapabilityToken<>() {
	});
	
	@SubscribeEvent
	public void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<LevelChunk> event) {
		if (!event.getObject().getCapability(CHUNK_INFO).isPresent()&&!event.getObject().getLevel().isClientSide) {
			event.addCapability(new ResourceLocation(HmxyMod.MOD_ID, "chunk_info"), new ChunkInfoProvider(event.getObject()));
		}
	}
}
