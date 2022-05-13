package by.ts.hmxy.chunk;

import by.ts.hmxy.capability.Capabilities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChunkEventHandler {
	@SubscribeEvent
	public void onSave(ChunkDataEvent.Save event) {
		ChunkAccess access = event.getChunk();
		if (access instanceof LevelChunk chunk && !chunk.getLevel().isClientSide) {
			chunk.getCapability(Capabilities.CHUNK_INFO).ifPresent(info -> {
				event.getData().put("chunkInfo", info.serializeNBT());
			});
		}
	}

	@SubscribeEvent
	public void onLoad(ChunkDataEvent.Load event) {
		ChunkAccess access = event.getChunk();
		if (access instanceof LevelChunk chunk && !chunk.getLevel().isClientSide) {
			if (chunk.getLevel() instanceof ServerLevel) {
				chunk.getCapability(Capabilities.CHUNK_INFO).ifPresent(info -> {
					info.deserializeNBT(event.getData().getCompound("chunkInfo"));
				});
			}
		}
	}
}
