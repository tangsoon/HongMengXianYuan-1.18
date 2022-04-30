package by.ts.hmxy.chunk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import by.ts.hmxy.capability.ChunkInfoProvider;
import by.ts.hmxy.capability.HmxyChunkInfo;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ChunkEventHandler {
	private static final Logger LOGGER = LogManager.getLogger();

	@SubscribeEvent
	public static void onSave(ChunkDataEvent.Save event) {
		ChunkAccess access = event.getChunk();
		if (access instanceof LevelChunk chunk) {
			LazyOptional<HmxyChunkInfo> chunkInfo = chunk.getCapability(ChunkInfoProvider.CAPABILITY);
			event.getData().put("chunkInfo", chunkInfo.orElseGet(() -> {
				LOGGER.error("保存区块 (" + access.getPos().x + "," + access.getPos().z + ") 数据失败");
				return new HmxyChunkInfo();
			}).serializeNBT());
		}
	}
	
	@SubscribeEvent
	public static void onLoad(ChunkDataEvent.Load event) {
		ChunkAccess access = event.getChunk();
		if (access instanceof LevelChunk chunk) {
			LazyOptional<HmxyChunkInfo> chunkInfo = chunk.getCapability(ChunkInfoProvider.CAPABILITY);
			chunkInfo.orElseGet(() -> {
				LOGGER.error("保存区块 (" + access.getPos().x + "," + access.getPos().z + ") 数据失败");
				return new HmxyChunkInfo();

			}).deserializeNBT(event.getData().getCompound("chunkInfo"));
			;
		}
	}
}
