package by.ts.hmxy.block;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import by.ts.hmxy.HmxyMod;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
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
	public static final RegistryObject<Block> REIKI_STONE_ORE = register("reiki_stone_ore",
			() -> new ReikiStoneOreBlock(
					Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F)
							.sound(SoundType.DEEPSLATE).randomTicks().lightLevel(litBlockEmission(9)),
					UniformInt.of(0, 2), ReikiStoneOreBlock.Type.ORDINARY.type));

	/** 闪烁的灵石矿 */
	public static final RegistryObject<Block> REIKI_STONE_ORE_FLICKER = register("reiki_stone_ore_flicker",
			() -> new ReikiStoneOreBlock(Properties.copy(REIKI_STONE_ORE.get()), UniformInt.of(0, 2),
					ReikiStoneOreBlock.Type.FLICKER.type));

	/** 往生泉 */
	public static final RegistryObject<Block> PREVIOUS_LIFE_WATER = register("previous_life_water",
			() -> new PreviousLifeWaterBlock.Source());
	/**流动的往生泉*/
	public static final RegistryObject<Block> PREVIOUS_LIFE_WATER_FLOWING = register("previous_life_water_flowing",
			() -> new PreviousLifeWaterBlock.Flowing());

	/** 灵脉 */
	public static final RegistryObject<Block> LING_MAI = register("ling_mai",
			() -> new LingMaiBlock(Properties.copy(Blocks.STONE).noDrops().randomTicks()));

	private static ToIntFunction<BlockState> litBlockEmission(int pLightValue) {
		return (p) -> {
			return p.getValue(BlockStateProperties.LIT) ? pLightValue : 0;
		};
	}
	
	/**灯心草*/
	public static final RegistryObject<Block> DENG_XIN_CAO = register("deng_xin_cao",
			() -> new LingZhiBlock(Properties.copy(Blocks.WHEAT)));

	// 注意Supplier和直接new个的区别，Supplier只有调用的时候才创建对象，而new是直接创建对象
	private static RegistryObject<Block> register(String name, Supplier<Block> s) {
		return BLOCKS.register(name, s);
	}
}
