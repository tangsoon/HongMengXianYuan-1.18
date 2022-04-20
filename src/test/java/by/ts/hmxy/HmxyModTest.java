package by.ts.hmxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod("hmxy")
@EventBusSubscriber
public class HmxyModTest {
	public HmxyModTest() {
		System.out.println("啦啦啦啦啦啦啦啦啦啦啦啦啦啦啦啦啦啦啦："+this.getClass().getClassLoader().toString());
	}
}
