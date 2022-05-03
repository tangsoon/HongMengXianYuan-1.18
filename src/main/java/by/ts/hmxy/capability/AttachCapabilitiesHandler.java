package by.ts.hmxy.capability;

import by.ts.hmxy.HmxyMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class AttachCapabilitiesHandler {
	@SubscribeEvent
	public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<LevelChunk> event) {
		if (!event.getObject().getCapability(ChunkInfoProvider.CAPABILITY).isPresent()) {
			event.addCapability(new ResourceLocation(HmxyMod.MOD_ID, "chunk_info"), new ChunkInfoProvider(event.getObject()));
		}
	}
	//TODO 疑似无用代码
	@SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(HmxyChunkInfo.class);
    }
}
