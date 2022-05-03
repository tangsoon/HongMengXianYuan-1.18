package by.ts.hmxy.block;

import java.util.Random;
import by.ts.hmxy.capability.ChunkInfoProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * 灵脉
 * @author tangsoon
 *
 */
public class LingMaiBlock extends Block {

	public LingMaiBlock(Properties pro) {
		super(pro);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
		super.randomTick(pState, pLevel, pPos, pRandom);
		LevelChunk chunk = pLevel.getChunkAt(pPos);
		if (chunk.getLevel() instanceof ServerLevel) {
			chunk.getCapability(ChunkInfoProvider.CAPABILITY).ifPresent(info -> {
				info.setLingQi(info.getLingQi() + 1);
				chunk.setUnsaved(true);
			});	
		}
	}
}
