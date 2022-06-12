package by.ts.hmxy.client.gui.wigdet;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import by.ts.hmxy.util.HmxyColor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * 一个简单的组件
 * 
 * @author tangsoon
 *
 */
public abstract class HoveredWidgetImp extends AbstractWidget implements HoveredWidget {

	protected ResourceLocation texture;
	protected int texBgU;
	protected int texBgV;
	protected HmxyColor color=new HmxyColor(0xffffff);

	public HoveredWidgetImp(int absX, int absY, int pWidth, int pHeight, Component pMessage, ResourceLocation texture,
			int texBgU, int texBgV) {
		super(absX, absY, pWidth, pHeight, pMessage);
		this.texture = texture;
		this.texBgU = texBgU;
		this.texBgV = texBgV;
	}

	@Override
	public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
		pNarrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
		if (this.active) {
			if (this.isFocused()) {
				pNarrationElementOutput.add(NarratedElementType.USAGE,
						new TranslatableComponent("narration.checkbox.usage.focused"));
			} else {
				pNarrationElementOutput.add(NarratedElementType.USAGE,
						new TranslatableComponent("narration.checkbox.usage.hovered"));
			}
		}
	}

	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
//		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, this.texture);
		RenderSystem.setShaderColor(color.getR(), color.getG(), color.getB(), color.getA());
		RenderSystem.enableBlend();
//		RenderSystem.defaultBlendFunc();
//		RenderSystem.enableDepthTest();
		this.blit(pPoseStack, this.x, this.y, this.texBgU, this.texBgV, this.width, this.height);
	}

	public ResourceLocation getTexture() {
		return texture;
	}

	public int getTexBgU() {
		return texBgU;
	}

	public int getTexBgV() {
		return texBgV;
	}

	public HmxyColor getColor() {
		return color;
	}

	public HoveredWidgetImp setColor(int rgb) {
		this.color = new HmxyColor(rgb);
		return this;
	}
	
	public HoveredWidgetImp setColor(int rgb,int a) {
		this.color = new HmxyColor(rgb,a);
		return this;
	}
}
