package by.ts.hmxy.client.gui.wigdet;

import java.util.function.Supplier;

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
	protected float a=1.0F;
	
	protected ResourceLocation texture; 
	
	protected Supplier<Integer> leftPos;
	protected Supplier<Integer> topPos;
	
	protected int imageWidth;
	protected int imageHeight;
	
	private GuiComponent gui;
	public GridWidget(ResourceLocation texture,Supplier<Integer> leftPos,Supplier<Integer> topPos,GuiComponent gui) {
		this.texture=texture;
		this.leftPos=leftPos;
		this.topPos=topPos;
		this.gui=gui;
		this.refreshImageSize();
	}
	@Override
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		RenderSystem.setShaderTexture(0, this.texture);
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(r, g, b, a);
		int x=this.leftPos.get();
		int y=this.topPos.get();
		
		gui.blit(pPoseStack, x, y, 0, 0, this.width1, this.height1);
		gui.blit(pPoseStack, x+this.width1, y, this.width1, 0, this.width2, height1);
		gui.blit(pPoseStack, x+this.width1+this.width2, y, this.width1+this.maxWidth2,0, this.width3, height1);

		gui.blit(pPoseStack, x, y+this.height1, 0, this.height1, this.width1, this.height2);
		gui.blit(pPoseStack, x+this.width1, y+this.height1, this.width1, this.height1, this.width2, height2);
		gui.blit(pPoseStack, x+this.width1+this.width2, y+this.height1, this.width1+this.maxWidth2,this.height1, this.width3, height2);
		
		gui.blit(pPoseStack, x, y+this.height1+this.height2, 0, this.height1+this.maxHeight2, this.width1, this.height3);
		gui.blit(pPoseStack, x+this.width1, y+this.height1+this.height2, this.width1, this.height1+this.maxHeight2, this.width2, height3);
		gui.blit(pPoseStack, x+this.width1+this.width2, y+this.height1+this.height2, this.width1+this.maxWidth2,this.height1+this.maxHeight2, this.width3, height3);
	}
	
	public GridWidget refreshImageSize(){
		this.imageWidth=this.width1+this.width2+this.width3;
		this.imageHeight=this.height1+this.height2+this.height3;
		return this;
	}
	
	public GridWidget setWidth1(int width1) {
		this.width1 = width1;
		
		return this;
	}

	public GridWidget setWidth2(int width2) {
		this.width2 = width2;
		
		return this;
	}

	public GridWidget setWidth3(int width3) {
		this.width3 = width3;
		
		return this;
	}

	public GridWidget setHeight1(int height1) {
		this.height1 = height1;
		
		return this;
	}
	public GridWidget setHeight2(int height2) {
		this.height2 = height2;
		
		return this;
	}

	public GridWidget setHeight3(int height3) {
		this.height3 = height3;
		
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
	
	public GridWidget setColor(float r,float g,float b,float a){
		this.r=r;
		this.g=g;
		this.b=b;
		this.a=a;
		return this;
	}
}
