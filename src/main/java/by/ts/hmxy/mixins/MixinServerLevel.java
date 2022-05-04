package by.ts.hmxy.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import by.ts.hmxy.capability.ChunkInfoProvider;
import by.ts.hmxy.capability.HmxyChunkInfo;
import by.ts.hmxy.config.Configs;
import by.ts.hmxy.util.BiSupplier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel {

	@Inject(method = "tickChunk", at = @At("HEAD"))
	protected void onTick(LevelChunk chunk, int pRandomTickSpeed, CallbackInfo ci) {

		//TODO 灵气无法向周围传播
		chunk.getCapability(ChunkInfoProvider.CAPABILITY).ifPresent(info -> {
			float currentLingQi = info.getLingQi() * (1.0F - Configs.chunkLingQiDisappearRate.get());
			info.setLingQi(currentLingQi);
			ChunkPos chunkPos = chunk.getPos();
			BiSupplier<LevelChunk, HmxyChunkInfo> bi = getChunkInfoWithLowLingQi(chunk, info, chunkPos.x + 1,
					chunkPos.z);
			bi = getChunkInfoWithLowLingQi(bi.a, bi.b, chunkPos.x - 1, chunkPos.z);
			bi = getChunkInfoWithLowLingQi(bi.a, bi.b, chunkPos.x, chunkPos.z + 1);
			bi = getChunkInfoWithLowLingQi(bi.a, bi.b, chunkPos.x, chunkPos.z - 1);
			float diff = (info.getLingQi() - bi.b.getLingQi()) * Configs.chunkLingQiFlowRate.get();
			if (diff > 0) {
				info.setLingQi(info.getLingQi() - diff);
				bi.b.setLingQi(bi.b.getLingQi() + diff);
				chunk.setUnsaved(true);
				bi.a.setUnsaved(true);
			}
		});
	}

	/**
	 * 
	 * @param chunk1
	 * @param info1
	 * @param chunkX
	 * @param chunkZ
	 * @return 获取灵气较小的区块
	 */
	private static BiSupplier<LevelChunk, HmxyChunkInfo> getChunkInfoWithLowLingQi(LevelChunk chunk1,
			HmxyChunkInfo info1, int chunkX, int chunkZ) {
		Level level = chunk1.getLevel();
		BiSupplier<LevelChunk, HmxyChunkInfo> bi = new BiSupplier<LevelChunk, HmxyChunkInfo>(chunk1, info1);
		if (level.hasChunk(chunkX, chunkZ)) {
			LevelChunk chunk2 = level.getChunk(chunkX, chunkZ);
			chunk2.getCapability(ChunkInfoProvider.CAPABILITY).ifPresent(info2 -> {
				if (info2.getLingQi() < info1.getLingQi()) {
					bi.a = chunk2;
					bi.b = info2;
					System.out.println(chunkX+" "+chunkZ+" "+info2.getLingQi());
				}
			});
		}
		return bi;
	}
}
