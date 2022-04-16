package com.ts.hmxy.data;

import com.ts.hmxy.HmxyMod;
import com.ts.hmxy.tags.HmxyItemTags;
import com.ts.hmxy.world.item.HmxyItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class HmxyItemTagsProvider extends ItemTagsProvider {
	public HmxyItemTagsProvider(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider,
			ExistingFileHelper existingFileHelper) {
		super(pGenerator, pBlockTagsProvider, HmxyMod.MOD_ID, existingFileHelper);
	}

	protected void addTags() {
		this.tag(HmxyItemTags.REIKI_STONE).add(HmxyItems.LOW_GRADE_REIKI_STONE.get()).add(HmxyItems.MEDIUM_GRADE_REIKI_STONE.get()).add(HmxyItems.HIGH_GRADE_REIKI_STONE.get()).add(HmxyItems.TOP_GRADE_REIKI_STONE.get());
	}
}
