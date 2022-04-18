package by.ts.hmxy.world.item.level.block;

import by.ts.hmxy.HmxyMod;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class HmxyBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			HmxyMod.MOD_ID);

	/** 灵石矿 */
	public static final RegistryObject<Block> REIKI_STONE_ORE = BLOCKS.register("reiki_stone_ore", () -> new OreBlock(
			BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F).sound(SoundType.DEEPSLATE),UniformInt.of(0, 2)));
}
