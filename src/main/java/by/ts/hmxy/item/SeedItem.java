package by.ts.hmxy.item;

import by.ts.hmxy.block.LingZhiBlock;
import by.ts.hmxy.item.gene.DNA;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 灵植
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
}
