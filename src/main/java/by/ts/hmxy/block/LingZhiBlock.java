package by.ts.hmxy.block;

import java.util.Random;
import by.ts.hmxy.block.blockentity.LingZhiBE;
import by.ts.hmxy.capability.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LingZhiBlock extends BushBlock implements EntityBlock {

	private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[] { Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D) };

	public LingZhiBlock(Properties pro) {
		super(pro);
	}

	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		if (pLevel.getBlockEntity(pPos) instanceof LingZhiBE be) {
			return SHAPE_BY_AGE[(int) (be.getCurrentGrowTimes() / be.getMaxGrowTimes() * SHAPE_BY_AGE.length)];
		}
		return SHAPE_BY_AGE[0];
	}

	/**
	 * 可以种植在耕土上，只有在耕土上才能生长
	 * 
	 * @param pState
	 * @param pLevel
	 * @param pPos
	 * @return
	 */
	// TODO 修改成自己的土壤
	protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return pState.is(Blocks.FARMLAND) || pState.is(Blocks.DIRT);
	}

	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
		if (pLevel.isLoaded(pPos) && pLevel.getRawBrightness(pPos, 0) >= 9) {
			if (pLevel.getBlockEntity(pPos) instanceof LingZhiBE be
					&& be.getCurrentGrowTimes() < be.getMaxGrowTimes()) {
				LevelChunk chunk = pLevel.getChunkAt(pPos);
				chunk.getCapability(Capabilities.CHUNK_INFO).ifPresent(info -> {
					float grow = info.getLingQi() * be.getGrowSpeed();
					info.setLingQi(Math.max(0, info.getLingQi() - grow));
					be.setMedicinal(be.getMedicinal() + grow);
					be.setCurrentGrowTimes(be.getCurrentGrowTimes() + 1);
					be.setChanged();
					chunk.setUnsaved(true);
				});
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new LingZhiBE(pPos, pState);
	}

	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
			BlockEntityType<T> type) {
		return null;
	}

}
