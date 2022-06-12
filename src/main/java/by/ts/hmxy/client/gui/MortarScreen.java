package by.ts.hmxy.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.client.gui.wigdet.PacketButton;
import by.ts.hmxy.menu.MortarMenu;
import by.ts.hmxy.net.ButtonPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MortarScreen extends BaseSreen<MortarMenu> {

	public static final ResourceLocation TEXTURER = HmxyMod.modLoc("textures/gui/mortar.png");

	public MortarScreen(MortarMenu menu, Inventory inv, Component name) {
		super(menu, inv, name);
		this.imageWidth = 193;
		this.imageHeight = 180;
	}

	protected void init() {
		super.init();
		this.addRenderableWidget(new GrindButton());
	}

	public class GrindButton extends PacketButton {
		public GrindButton() {
			super(MortarScreen.this.leftPos + 84, MortarScreen.this.topPos + 68, 24, 14, 193, 0, TEXTURER,
					ButtonPacket.MORTAR_GRIND);
		}
	}

	@Override
	protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
		this.defaultBackground(pPoseStack, pPartialTick, pMouseX, pMouseY, TEXTURER);
	}
}