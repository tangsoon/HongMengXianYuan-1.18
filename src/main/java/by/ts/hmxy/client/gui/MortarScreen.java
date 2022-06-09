package by.ts.hmxy.client.gui;

import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.client.gui.wigdet.PacketButton;
import by.ts.hmxy.menu.MortarMenu;
import by.ts.hmxy.net.ButtonPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MortarScreen extends BaseSreen<MortarMenu> {

	public MortarScreen(MortarMenu menu, Inventory inv, Component name) {
		super(menu, inv, name, new ResourceLocation(HmxyMod.MOD_ID, "textures/gui/mortar.png"));
		this.imageWidth = 193;
		this.imageHeight = 180;
	}

	protected void init() {
		super.init();
		this.addRenderableWidget(new GrindButton());
	}

	public class GrindButton extends PacketButton {
		public GrindButton() {
			super(MortarScreen.this.leftPos + 84, MortarScreen.this.topPos + 68, 24, 14, 193, 0, MortarScreen.this.texture,
					ButtonPacket.MORTAR_GRIND);
		}
	}
}