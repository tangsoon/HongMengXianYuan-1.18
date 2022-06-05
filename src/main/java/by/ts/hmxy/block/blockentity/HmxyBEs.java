package by.ts.hmxy.block.blockentity;

import java.util.function.Supplier;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.block.ElixirFurnaceBlock;
import by.ts.hmxy.block.ElixirFurnaceRootBlock;
import by.ts.hmxy.block.HmxyBlocks;
import by.ts.hmxy.block.LingZhiBlock.LingZhiBE;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HmxyBEs {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITIES, HmxyMod.MOD_ID);

	public static final RegistryObject<BlockEntityType<LingZhiBE>> LING_ZHI = register("ling_zhi",
			() -> BlockEntityType.Builder.of(LingZhiBE::new, HmxyBlocks.DENG_XIN_CAO.get())
					.build(Util.fetchChoiceType(References.BLOCK_ENTITY, "ling_zhi")));

	public static final RegistryObject<BlockEntityType<ElixirFurnaceRootBlock.ElixirFurnaceRootBE>> ELIXIR_FURNACE_ROOT = register(
			"elixir_furnace_root",
			() -> BlockEntityType.Builder
					.of(ElixirFurnaceRootBlock.ElixirFurnaceRootBE::new, HmxyBlocks.ELIXIR_FURNACE_ROOT.get())
					.build(Util.fetchChoiceType(References.BLOCK_ENTITY, "elixir_furnace_root")));

	public static final RegistryObject<BlockEntityType<ElixirFurnaceBlock.ElixirFurnaceBE>> ELIXIR_FURNACE = register(
			"elixir_furnace",
			()->BlockEntityType.Builder.of(ElixirFurnaceBlock.ElixirFurnaceBE::new, HmxyBlocks.ELIXIR_FURNACE.get())
					.build(Util.fetchChoiceType(References.BLOCK_ENTITY, "elixir_furnace")));

	public static final RegistryObject<BlockEntityType<GeneratorBE>> GENERATOR=register("generator", ()->BlockEntityType.Builder.of(GeneratorBE::new, HmxyBlocks.ELIXIR_FURNACE.get())
			.build(Util.fetchChoiceType(References.BLOCK_ENTITY, "elixir_furnace")));
	// 在其它泛型基础上定义泛型参数时，应该有满足其要求的约束
	private static <I extends BlockEntity> RegistryObject<BlockEntityType<I>> register(final String name,
			Supplier<BlockEntityType<I>> s) {
		return BLOCK_ENTITIES.register(name, s);
	}
}
