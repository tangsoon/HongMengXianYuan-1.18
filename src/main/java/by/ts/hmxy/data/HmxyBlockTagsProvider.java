package by.ts.hmxy.data;

import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.block.HmxyBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class HmxyBlockTagsProvider extends BlockTagsProvider {
	public HmxyBlockTagsProvider(DataGenerator pGenerator, ExistingFileHelper existingFileHelper) {
		super(pGenerator, HmxyMod.MOD_ID, existingFileHelper);
	}

	protected void addTags() {
		this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(HmxyBlocks.REIKI_STONE_ORE.get());
	}
}
