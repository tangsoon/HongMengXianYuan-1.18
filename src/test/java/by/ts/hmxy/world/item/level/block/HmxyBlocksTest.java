package by.ts.hmxy.world.item.level.block;

import by.ts.hmxy.HmxyMod;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class HmxyBlocksTest {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, HmxyMod.MOD_ID);
	public static final RegistryObject<Block> TEST_DIMENSION_PORTAL=BLOCKS.register("test_dimension_portal", ()->new TestDimensionPortalBlock());
}
