package by.ts.hmxy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 方块实现这个类能快捷地创造一个ItemStack
 * @author tangsoon
 *
 */
public interface ItemStackCreator {
	ItemStack createItemStack(BlockGetter blockGetter, BlockPos pPos, BlockState pState);
}
