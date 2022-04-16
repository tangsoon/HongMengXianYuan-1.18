package com.ts.hmxy.data;

import com.ts.hmxy.HmxyMod;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class HmxyBlockTagsProvider extends BlockTagsProvider {

	public HmxyBlockTagsProvider(DataGenerator pGenerator, String modId, ExistingFileHelper existingFileHelper) {
		super(pGenerator, HmxyMod.MOD_ID,existingFileHelper);
	}
	
	 protected void addTags() {
		 
	 }
	   
}
