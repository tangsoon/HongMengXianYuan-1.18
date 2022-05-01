package by.ts.hmxy.data;

import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.block.HmxyBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class HmxyBlockStatesProvider extends BlockStateProvider {

	public HmxyBlockStatesProvider(DataGenerator gen, ExistingFileHelper helper) {
		super(gen, HmxyMod.MOD_ID, helper);
	}

	@Override
	protected void registerStatesAndModels() {
		this.simpleBlockAndItem(HmxyBlocks.REIKI_STONE_ORE.get());
		this.simpleBlockAndItem(HmxyBlocks.REIKI_STONE_ORE_FLICKER.get());
	}

	/**
	 * 同时生成方块的模型，BLockState和物品的模型文件。
	 * @param block
	 */
	private void simpleBlockAndItem(Block block) {
		super.simpleBlock(block, cubeAll(block));
		this.itemModels().withExistingParent(block.getRegistryName().toString(),
				block.getRegistryName().getNamespace() + ":block/"+block.getRegistryName().getPath());
	}
}
