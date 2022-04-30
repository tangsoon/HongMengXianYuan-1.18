package by.ts.hmxy.data;

import by.ts.hmxy.HmxyMod;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class HmxyItemModelProvider extends ItemModelProvider {

	public HmxyItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, HmxyMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //withExistingParent(HmxyItems.THE_MOTAL_PORTAL.get().getRegistryName().getPath(), modLoc("block/the_mortal_portal"));
    	//this.withExistingParent(HmxyItems.REIKI_STONE_ORE.get().getRegistryName().getPath(), "minecraft:item/acacia_boat");
    }
}

