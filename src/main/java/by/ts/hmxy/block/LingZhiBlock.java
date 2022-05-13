package by.ts.hmxy.block;

import by.ts.hmxy.block.blockentity.LingZhiBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.shapes.VoxelShape;

public class LingZhiBlock extends BushBlock implements EntityBlock{

//	/** 最大生长次数 */
//	public static final IntegerProperty MAX_GROW_TIMES = IntegerProperty.create("max_grow_times", 10, 20);
//	/** 当前生长次数，每个随机刻度增1，达到最大生长次数后成熟 */
//	public static final IntegerProperty CUREENT_GROW_TIMES = IntegerProperty.create("current_grow_times", 0,20);
//	/** 决定每个随机刻药性增长量 */
//	public static final IntegerProperty GROW_SPEED = IntegerProperty.create("grow_speed", 1, 10);

	private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[] { Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D) };

	public LingZhiBlock(Properties pro) {
		super(pro);
		//this.registerDefaultState(this.stateDefinition.any().setValue(MAX_GROW_TIMES, Integer.valueOf(10)).setValue(CUREENT_GROW_TIMES, Integer.valueOf(0)).setValue(GROW_SPEED, Integer.valueOf(1)));
	}

//	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
//		pBuilder.add(MAX_GROW_TIMES,CUREENT_GROW_TIMES,GROW_SPEED);
//	}

//	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
//		return SHAPE_BY_AGE[(int) (this.getCurrentGrowTImes(pState) / (float) this.getMaxGrowTImes(pState) * 8)];
//	}

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

//	public boolean isRandomlyTicking(BlockState pState) {
//		return this.getCurrentGrowTImes(pState) >= this.getMaxGrowTImes(pState);
//	}

//	@SuppressWarnings("deprecation")
//	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
//		if (!pLevel.isAreaLoaded(pPos, 1))
//			return;
//		if (pLevel.getRawBrightness(pPos, 0) >= 9) {
//			int i = this.getCurrentGrowTImes(pState);
//			if (i < this.getMaxGrowTImes(pState)) {
//				this.setCurrentGrowTImes(pState, i + 1);
//			}
//		}
//	}

	/**
	 * TODO 在这里返回对应的ItemStack，并将灵植的属性存入其中。
	 */
	
	public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new LingZhiBE(pPos, pState);
	}
	
	
	
//	public int getMaxGrowTImes(BlockState pState) {
//		return pState.getValue(MAX_GROW_TIMES);
//	}
//
//	public void setMaxGrowTImes(BlockState pState, int value) {
//		pState.setValue(MAX_GROW_TIMES, value);
//	}
//
//	public void setCurrentGrowTImes(BlockState pState, int value) {
//		pState.setValue(CUREENT_GROW_TIMES, value);
//	}
//
//	public int getCurrentGrowTImes(BlockState pState) {
//		return pState.getValue(CUREENT_GROW_TIMES);
//	}
//
//	public void setGrowSpeed(BlockState pState, int value) {
//		pState.setValue(GROW_SPEED, value);
//	}
//
//	public int getGrowSpeed(BlockState pState) {
//		return pState.getValue(GROW_SPEED);
//	}
	
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
			BlockEntityType<T> type) {
		if(!level.isClientSide()) {
			return (lvl, pos, blockState, t) -> {
	            if (t instanceof LingZhiBE lingZhi) {
	            	lingZhi.tick(level, pos, state, lingZhi);
	            }
	        };
		}
		return null;
	}

}
