package by.ts.hmxy.client;

import by.ts.hmxy.block.HmxyBlocks;
import by.ts.hmxy.fluid.HmxyFluids;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

public class RenderTypes {
	public static void handle(){
		ItemBlockRenderTypes.setRenderLayer(HmxyBlocks.DENG_XIN_CAO.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(HmxyBlocks.ELIXIR_FURNACE_BEVEL.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(HmxyFluids.PREVIOUS_LIFE_WATER.get(), renderType -> renderType == RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(HmxyFluids.PREVIOUS_LIFE_WATER_FLOWING.get(), renderType -> renderType == RenderType.translucent());
	}
}
