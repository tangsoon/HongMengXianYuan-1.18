package by.ts.hmxy.item;

import java.util.List;
import javax.annotation.Nullable;
import by.ts.hmxy.block.LingZhiBlock;
import by.ts.hmxy.block.blockentity.LingZhiBE;
import by.ts.hmxy.item.gene.DNA;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 灵植种子
 * 
 * @author tangsoon
 *
 */
public class SeedItem extends Item {
	public SeedItem(Properties pProperties) {
		super(pProperties);
	}

	public DNA getDna(ItemStack stack) {
		CompoundTag tag = stack.getTagElement("dna");
		DNA dna = new DNA(LingZhiBlock.GENE_HELPER.getGeneTypes());
		if (tag != null) {
			dna.deserializeNBT(tag);
		} else {
			this.setDna(stack, dna);
		}
		return dna;
	}

	public InteractionResult useOn(UseOnContext pContext) {
		InteractionResult interactionresult = InteractionResult.PASS;
		BlockPlaceContext bContext = new BlockPlaceContext(pContext);
		if (bContext.canPlace()) {
			ItemStack stack = pContext.getItemInHand();
			LingZhiBlock block = this.getLingZhi(stack);
			Level level = bContext.getLevel();
			BlockPos pos = bContext.getClickedPos();
			level.setBlock(pos, block.defaultBlockState(), 11);
			if (level.getBlockEntity(pos) instanceof LingZhiBE be) {
				CompoundTag dna = stack.getTagElement("dna");
				if (dna != null) {
					be.DNA.deserializeNBT(dna);
					stack.shrink(1);
					be.setChanged();
					interactionresult = InteractionResult.SUCCESS;
				}
			}
		}
		return interactionresult;
	}

	public void setDna(ItemStack stack, DNA dna) {
		stack.addTagElement("dna", dna.serializeNBT());
	}

	public LingZhiBlock getLingZhi(ItemStack stack) {
		return (LingZhiBlock) ForgeRegistries.BLOCKS
				.getValue(new ResourceLocation(((StringTag) stack.getTag().get("lingZhi")).getAsString()));
	}

	public void setLingZhi(ItemStack stack, LingZhiBlock lingZhi) {
		stack.addTagElement("lingZhi", StringTag.valueOf(lingZhi.getRegistryName().toString()));
	}

	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
			TooltipFlag pIsAdvanced) {
		LingZhiBlock.GENE_HELPER.appendHoverText(this.getDna(pStack), pLevel, pTooltipComponents, pIsAdvanced);
	}
}
