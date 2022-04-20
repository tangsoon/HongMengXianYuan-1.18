package by.ts.hmxy.world.item;

import by.ts.hmxy.HmxyMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class HmxyItemsTest {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HmxyMod.MOD_ID);
	
	public static final RegistryObject<Item> TEST_DIMENSION=ITEMS.register("test_dimension", ()->new TestDimensionItem());
}
