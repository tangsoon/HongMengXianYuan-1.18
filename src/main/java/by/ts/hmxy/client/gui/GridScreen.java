package by.ts.hmxy.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.client.gui.wigdet.GridWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * 一个井字布局的，可以修改尺寸并背景图片自适应的Screen.
 * 
 * @author tangsoon
 *
 * @param <T>
 */
public abstract class GridScreen<T extends AbstractContainerMenu> extends BaseSreen<T> {
	public static final ResourceLocation DEFAULT_TEXTURER = HmxyMod.modLoc(
			"textures/gui/grid_screen.png");

	protected GridWidget grideWidget;
	
	public GridScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
		this.grideWidget = createGridWidget();
	}

	@Override
	protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
		grideWidget.render(pPoseStack, pMouseX, pMouseX, pPartialTick);
	}
	
	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		drawCenteredString(matrixStack, font, title, this.imageWidth / 2, titleOffSetY, 0xffffff);
	}
	
	protected GridWidget createGridWidget() {
		return new GridWidget(DEFAULT_TEXTURER, () -> this.leftPos, () -> this.topPos, this);
	}
}