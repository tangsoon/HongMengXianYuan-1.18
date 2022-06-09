package by.ts.hmxy.client.gui.wigdet;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.resources.ResourceLocation;

/**
 * 一个井字布局的组件
 * @author tangsoon
 *
 */
public class GridWidget implements Widget{

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
	
	protected ResourceLocation texture; 
	
	protected int leftPos;
	protected int topPos;
	
	protected int imageWidth;
	protected int imageHeight;
	
	private GuiComponent gui;
	public GridWidget(ResourceLocation texture,int x,int y,GuiComponent gui) {
		this.leftPos=x;
		this.topPos=y;
		this.gui=gui;
		this.refreshImageSize();
	}
	@Override
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		RenderSystem.setShaderTexture(0, this.texture);
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(r, g, b, 1.0F);
		
		gui.blit(pPoseStack, this.leftPos, this.topPos, 0, 0, this.width1, this.height1);
		gui.blit(pPoseStack, this.leftPos+this.width1, this.topPos, this.width1, 0, this.width2, height1);
		gui.blit(pPoseStack, this.leftPos+this.width1+this.width2, this.topPos, this.width1+this.maxWidth2,0, this.width3, height1);

		gui.blit(pPoseStack, this.leftPos, this.topPos+this.height1, 0, this.height1, this.width1, this.height2);
		gui.blit(pPoseStack, this.leftPos+this.width1, this.topPos+this.height1, this.width1, this.height1, this.width2, height2);
		gui.blit(pPoseStack, this.leftPos+this.width1+this.width2, this.topPos+this.height1, this.width1+this.maxWidth2,this.height1, this.width3, height2);
		
		gui.blit(pPoseStack, this.leftPos, this.topPos+this.height1+this.height2, 0, this.height1+this.maxHeight2, this.width1, this.height3);
		gui.blit(pPoseStack, this.leftPos+this.width1, this.topPos+this.height1+this.height2, this.width1, this.height1+this.maxHeight2, this.width2, height3);
		gui.blit(pPoseStack, this.leftPos+this.width1+this.width2, this.topPos+this.height1+this.height2, this.width1+this.maxWidth2,this.height1+this.maxHeight2, this.width3, height3);
	}
	
	public GridWidget refreshImageSize(){
		this.imageWidth=this.width1+this.width2+this.width3;
		this.imageHeight=this.height1+this.height2+this.height3;
		return this;
	}
	
	public GridWidget setWidth1(int width1) {
		this.width1 = width1;
		refreshImageSize();
		return this;
	}

	public GridWidget setWidth2(int width2) {
		this.width2 = width2;
		refreshImageSize();
		return this;
	}

	public GridWidget setWidth3(int width3) {
		this.width3 = width3;
		refreshImageSize();
		return this;
	}

	public GridWidget setHeight1(int height1) {
		this.height1 = height1;
		refreshImageSize();
		return this;
	}
	public GridWidget setHeight2(int height2) {
		this.height2 = height2;
		refreshImageSize();
		return this;
	}

	public GridWidget setHeight3(int height3) {
		this.height3 = height3;
		refreshImageSize();
		return this;
	}
	
	public GridWidget setMaxWidth2(int maxWidth2) {
		this.maxWidth2 = maxWidth2;
		return this;
	}

	public GridWidget setMaxHeight2(int maxHeight2) {
		this.maxHeight2 = maxHeight2;
		return this;
	}

	public GridWidget setTexture(ResourceLocation texture){
		this.texture=texture;
		refreshImageSize();
		return this;
	}
}
