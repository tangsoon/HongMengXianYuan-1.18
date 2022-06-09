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
 * @author tangsoon
 *
 * @param <T>
 */
public class ImageScreen<T extends AbstractContainerMenu> extends BaseSreen<T>{
	public static final ResourceLocation DEFAULT_TEXTURER=new ResourceLocation(HmxyMod.MOD_ID,"textures/gui/default_texture.png"); 
	
	protected GridWidget grideWidget;
	
	public ImageScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle, DEFAULT_TEXTURER);
		this.grideWidget=new GridWidget(DEFAULT_TEXTURER,()->this.leftPos, ()->this.topPos, this);
	}
	
	public void customRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		grideWidget.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}
