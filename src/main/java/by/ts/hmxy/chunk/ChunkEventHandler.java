package by.ts.hmxy.chunk;

import by.ts.hmxy.capability.ChunkInfoProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ChunkEventHandler {
	@SubscribeEvent
	public static void onSave(ChunkDataEvent.Save event) {
		ChunkAccess access = event.getChunk();
		if (access instanceof LevelChunk chunk && !chunk.getLevel().isClientSide) {
			chunk.getCapability(ChunkInfoProvider.CAPABILITY).ifPresent(info -> {
				event.getData().put("chunkInfo", info.serializeNBT());
			});
		}
	}

	@SubscribeEvent
	public static void onLoad(ChunkDataEvent.Load event) {
		ChunkAccess access = event.getChunk();
		if (access instanceof LevelChunk chunk && !chunk.getLevel().isClientSide) {
			if (chunk.getLevel() instanceof ServerLevel) {
				chunk.getCapability(ChunkInfoProvider.CAPABILITY).ifPresent(info -> {
					info.deserializeNBT(event.getData().getCompound("chunkInfo"));
				});
			}
		}
	}
}
