package by.ts.hmxy.data;

import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.world.item.level.block.HmxyBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class HmxyBlockStatesProvider extends BlockStateProvider {

    public HmxyBlockStatesProvider(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, HmxyMod.MOD_ID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(HmxyBlocks.THE_MORTAL_PORTAL.get());
    }
}


