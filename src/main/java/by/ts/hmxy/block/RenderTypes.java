package by.ts.hmxy.block;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

public class RenderTypes {
	public static void handle(){
		ItemBlockRenderTypes.setRenderLayer(HmxyBlocks.DENG_XIN_CAO.get(), RenderType.cutout());
	}
}
