package by.ts.hmxy.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class BaseSreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

	public ResourceLocation texture;
	public int x;
	public int y;
	public int titleOffSetY=5;
	
	public BaseSreen(T pMenu, Inventory pPlayerInventory, Component pTitle,ResourceLocation texture) {
		super(pMenu, pPlayerInventory, pTitle);
		this.texture=texture;
	}

	protected void init() {
		super.init();
		x = (this.width - this.imageWidth) / 2;
		y = (this.height - this.imageHeight) / 2;
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.customRender(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
	}
	
	public void customRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		
	}

	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		drawCenteredString(matrixStack, font, title, this.imageWidth/2, titleOffSetY, 0xffffff);
	}
	
	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, texture);
		this.blit(matrixStack, x, y, 0, 0, this.imageWidth, this.imageWidth);
	}
}
