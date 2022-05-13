package by.ts.hmxy.block.blockentity;

import java.util.function.Supplier;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.block.HmxyBlocks;
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

	public static final RegistryObject<BlockEntityType<LingZhiBE>> LING_ZHI	= register("ling_zhi",
			() -> BlockEntityType.Builder.of(LingZhiBE::new, HmxyBlocks.DENG_XIN_CAO.get())
					.build(Util.fetchChoiceType(References.BLOCK_ENTITY, "ling_zhi")));

	// 在其它泛型基础上定义泛型参数时，应该有满足其要求的约束
	private static <I extends BlockEntity> RegistryObject<BlockEntityType<I>> register(final String name,
			Supplier<BlockEntityType<I>> s) {
		return BLOCK_ENTITIES.register(name, s);
	}
}