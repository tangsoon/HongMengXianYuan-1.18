package by.ts.hmxy.item;

import java.util.List;
import javax.annotation.Nullable;
import by.ts.hmxy.block.LingZhiBlock;
import by.ts.hmxy.block.blockentity.LingZhiBE;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 破坏灵植方块后的凋落物，不能再被种植
 * @author tangsoon
 *
 */
public class LingZhiItem extends Item{
	
	public LingZhiBlock block;
	
	public LingZhiItem(LingZhiBlock block) {
		super(new Properties().stacksTo(64));
		this.block=block;
		this.block.setItem(this);
	}
	
	public LingZhiBE getLingZhiBE(ItemStack stack) {
		LingZhiBE be=new LingZhiBE(BlockPos.ZERO, block.defaultBlockState());
		CompoundTag beTag=stack.getTagElement("lingZhi");
		if(beTag!=null) {
			be.deserializeNBT(beTag);	
		}
		return be;
	}
	
	
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
			TooltipFlag pIsAdvanced) {
		LingZhiBE be=this.getLingZhiBE(pStack);
		LingZhiBlock.GENE_HELPER.appendHoverText(be.DNA, pLevel, pTooltipComponents, pIsAdvanced);
		pTooltipComponents.add(TransMsg.MEDICINE_BOTTLE_EFFECTIVE.create(be.getMedicinal()));
	}
	
	public ItemStack createItemStack(BlockGetter blockGetter, BlockPos pPos, BlockState pState) {
		ItemStack lingZhi = new ItemStack(this);
		LingZhiBE lingZhiBe = (LingZhiBE) blockGetter.getBlockEntity(pPos);
		lingZhi.addTagElement("lingZhi", lingZhiBe.serializeNBT());
		return lingZhi;
	}
}
