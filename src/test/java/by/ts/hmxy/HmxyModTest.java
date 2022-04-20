package by.ts.hmxy;

import by.ts.hmxy.world.item.HmxyItemsTest;
import by.ts.hmxy.world.item.level.block.HmxyBlocksTest;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**在测试文件夹中覆盖这个类，用于测试*/
public class HmxyModTest {
	public HmxyModTest() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		HmxyItemsTest.ITEMS.register(modEventBus);
		HmxyBlocksTest.BLOCKS.register(modEventBus);
	}
}
