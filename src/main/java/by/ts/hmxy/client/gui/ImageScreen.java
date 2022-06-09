package by.ts.hmxy.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import by.ts.hmxy.HmxyMod;
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
	
	
	
	protected int width1=11;
	protected int width2=170;
	protected int width3=11;
	protected int height1=10;
	protected int height2=174;
	protected int height3=2;
	
	protected int maxWidth2=233;
	protected int maxHeight2=244;
	
	protected float r=1.0F;
	protected float g=1.0F;
	protected float b=1.0F;
	
	public ImageScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle, DEFAULT_TEXTURER);
		refreshImageSize();
	}
	
	private void refreshImageSize(){
		this.imageWidth=this.width1+this.width2+this.width3;
		this.imageHeight=this.height1+this.height2+this.height3;
	}
	
	public ImageScreen<T> setWidth1(int width1) {
		this.width1 = width1;
		refreshImageSize();
		return this;
	}

	public ImageScreen<T> setWidth2(int width2) {
		this.width2 = width2;
		refreshImageSize();
		return this;
	}

	public ImageScreen<T> setWidth3(int width3) {
		this.width3 = width3;
		refreshImageSize();
		return this;
	}

	public ImageScreen<T> setHeight1(int height1) {
		this.height1 = height1;
		refreshImageSize();
		return this;
	}
	public ImageScreen<T> setHeight2(int height2) {
		this.height2 = height2;
		refreshImageSize();
		return this;
	}

	public ImageScreen<T> setHeight3(int height3) {
		this.height3 = height3;
		refreshImageSize();
		return this;
	}
	
	public ImageScreen<T> setMaxWidth2(int maxWidth2) {
		this.maxWidth2 = maxWidth2;
		return this;
	}

	public ImageScreen<T> setMaxHeight2(int maxHeight2) {
		this.maxHeight2 = maxHeight2;
		return this;
	}

	public ImageScreen<T> setTexture(ResourceLocation texture){
		this.texture=texture;
		refreshImageSize();
		return this;
	}
	
	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		//字体在这里渲染
	}
	
	public void customRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.setShaderTexture(0, this.texture);
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(r, g, b, 1.0F);
		
		blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.width1, this.height1);
		blit(matrixStack, this.leftPos+this.width1, this.topPos, this.width1, 0, this.width2, height1);
		blit(matrixStack, this.leftPos+this.width1+this.width2, this.topPos, this.width1+this.maxWidth2,0, this.width3, height1);

		blit(matrixStack, this.leftPos, this.topPos+this.height1, 0, this.height1, this.width1, this.height2);
		blit(matrixStack, this.leftPos+this.width1, this.topPos+this.height1, this.width1, this.height1, this.width2, height2);
		blit(matrixStack, this.leftPos+this.width1+this.width2, this.topPos+this.height1, this.width1+this.maxWidth2,this.height1, this.width3, height2);
		
		blit(matrixStack, this.leftPos, this.topPos+this.height1+this.height2, 0, this.height1+this.maxHeight2, this.width1, this.height3);
		blit(matrixStack, this.leftPos+this.width1, this.topPos+this.height1+this.height2, this.width1, this.height1+this.maxHeight2, this.width2, height3);
		blit(matrixStack, this.leftPos+this.width1+this.width2, this.topPos+this.height1+this.height2, this.width1+this.maxWidth2,this.height1+this.maxHeight2, this.width3, height3);
	}
}
