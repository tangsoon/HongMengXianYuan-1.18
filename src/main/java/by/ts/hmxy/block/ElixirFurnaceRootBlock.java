package by.ts.hmxy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 炼丹炉基，用于放置燃料
 * 
 * @author tangsoon
 *
 */
public class ElixirFurnaceRootBlock extends WaterloggedBlockBase{

	protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D);
	
	public ElixirFurnaceRootBlock(Material m) {
		super(Properties.of(m, m.getColor()).strength(2.0F));
	}

	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return SHAPE;
	}

}
