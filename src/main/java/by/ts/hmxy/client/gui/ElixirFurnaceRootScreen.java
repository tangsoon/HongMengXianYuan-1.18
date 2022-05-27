package by.ts.hmxy.client.gui;

import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.block.ElixirFurnaceRootBlock.ElixirFurnaceRootBE;
import by.ts.hmxy.menu.ElixirFurnaceRootMenu;
import by.ts.hmxy.net.SliderPacket;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ElixirFurnaceRootScreen extends BaseSreen<ElixirFurnaceRootMenu> {

	ElixirFurnaceRootBE be;

	public ElixirFurnaceRootScreen(ElixirFurnaceRootMenu menu, Inventory inv, Component name) {
		super(menu, inv, name, new ResourceLocation(HmxyMod.MOD_ID, "textures/gui/elixir_furnace_root.png"));
		this.imageWidth = 193;
		this.imageHeight = 180;
		be = this.getMenu().getBe();
	}

	protected void init() {
		super.init();
		this.addRenderableWidget(new PacketSlider(this.x + 10, this.y + 20, 100, 20, TransMsg.SLIDER_LING_QI_CONSUME,
				be.getValve() * be.getMaxLingQiConsume(), be.getValve(), SliderPacket.LING_QI_VALVE));
	}
}