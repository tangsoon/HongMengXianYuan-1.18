package by.ts.hmxy.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import by.ts.hmxy.client.gui.wigdet.HoverWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public abstract class BaseSreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

	public ResourceLocation texture;
	public int titleOffSetY = 5;

	public BaseSreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation texture) {
		super(pMenu, pPlayerInventory, pTitle);
		this.texture = texture;
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.customRender(matrixStack, mouseX, mouseY, partialTicks);
		this.renderWidgetTip(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
	}

	public void customRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {

	}

	public void renderWidgetTip(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		for (Widget w : this.renderables) {
			if (w instanceof AbstractWidget aw&&aw.isHoveredOrFocused()&&w instanceof HoverWidget hw) {
				var components = hw.getTips();
				if (components!=null&&components.size() > 0) {
					this.renderComponentTooltip(matrixStack, components, mouseX, mouseY);
				}
			}
		}
	}

	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		drawCenteredString(matrixStack, font, title, this.imageWidth / 2, titleOffSetY, 0xffffff);
	}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
//		RenderSystem.setShaderTexture(0, texture);
//		this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageWidth);
	}

	public Slot getHoverdSlot() {
		return this.hoveredSlot;
	}
}
