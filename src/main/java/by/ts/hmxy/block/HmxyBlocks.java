package by.ts.hmxy.block;

import java.util.function.ToIntFunction;

import by.ts.hmxy.HmxyMod;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HmxyBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			HmxyMod.MOD_ID);
	
	/** 灵石矿 */
	public static final RegistryObject<Block> REIKI_STONE_ORE = BLOCKS.register("reiki_stone_ore",
			() -> new ReikiStoneOreBlock(
					BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)
							.sound(SoundType.DEEPSLATE).randomTicks().lightLevel(litBlockEmission(9)),
					UniformInt.of(0, 2), ReikiStoneOreBlock.Type.ORDINARY.type));
	public static final RegistryObject<Block> REIKI_STONE_ORE_FLICKER = BLOCKS.register("reiki_stone_ore_flicker",
			() -> new ReikiStoneOreBlock(
					BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)
							.sound(SoundType.DEEPSLATE).randomTicks().lightLevel(litBlockEmission(9)),
					UniformInt.of(0, 2), ReikiStoneOreBlock.Type.FLICKER.type));
	/** 凡界传送门，往生泉 */
	public static final RegistryObject<Block> PREVIOUS_LIFE_WATER = BLOCKS.register("previous_life_water",
			() -> new PreviousLifeWaterBlock());

	private static ToIntFunction<BlockState> litBlockEmission(int pLightValue) {
		return (p) -> {
			return p.getValue(BlockStateProperties.LIT) ? pLightValue : 0;
		};
	}
}
