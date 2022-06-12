package by.ts.hmxy.client.gui.wigdet;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * 进度条
 * 
 * @author tangsoon
 *
 */
public abstract class ProgressBar extends AbstractWidget implements HoveredWidget {

	private ResourceLocation texture;
	private int texBgU;
	private int texBgV;
	private int barX;
	private int barY;
	private int barU;
	private int barV;
	private int barWidth;
	private int barheight;
	private Direction dir;

	public ProgressBar(int absX, int absY, int pWidth, int pHeight, Component pMessage, ResourceLocation texture,
			int texBgU, int texBgV,int barX,int barY, int barU, int barV,int barWidth,int barHeight ,Direction dir) {
		super(absX, absY, pWidth, pHeight, pMessage);
		this.texture = texture;
		this.texBgU = texBgU;
		this.texBgV = texBgV;
		this.barU = barU;
		this.barV = barV;
		this.dir = dir;
		this.barWidth=barWidth;
		this.barheight=barHeight;
		this.barX=barX;
		this.barY=barY;
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
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, this.texture);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		this.blit(pPoseStack, this.x, this.y, this.texBgU, this.texBgV, this.width, this.height);
		switch (this.dir) {
		case UP: {
			int hOffset=(int)(this.barheight*(1-this.getValue()));
			this.blit(pPoseStack, this.barX, this.barY+hOffset, this.barU, this.barV+hOffset, this.barWidth, this.barheight-hOffset);
			//this.blit(pPoseStack, this.barX, this.barY, this.barU, this.barV, this.barWidth, this.barheight);
			break;
		}
		case DOWN: {
			this.blit(pPoseStack, this.barX, this.barY, this.barU, this.barV, this.barWidth, (int)(this.barheight*this.getValue()));
			break;
		}
		case LEFT: {
			int xOffset=(int)(this.barWidth*(1-this.getValue()));
			this.blit(pPoseStack, this.barX+xOffset, this.barY, this.barU+xOffset, this.barV, this.barWidth-xOffset, this.barheight);
			break;
		}
		case RIGHT: {
			this.blit(pPoseStack, this.barX, this.barY, this.barU, this.barV, (int) (this.barWidth * this.getValue()),
					this.barheight);
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + this.dir);
		}

	}

	public abstract float getValue();

	public ResourceLocation getTexture() {
		return texture;
	}

	public int getTexBgU() {
		return texBgU;
	}

	public int getTexBgV() {
		return texBgV;
	}

	public int getBarU() {
		return barU;
	}

	public int getBarV() {
		return barV;
	}

	public Direction getDir() {
		return dir;
	}

	public int getBarWidth() {
		return barWidth;
	}

	public int getBarheight() {
		return barheight;
	}

	public int getBarX() {
		return barX;
	}

	public int getBarY() {
		return barY;
	}

	
	
	public static enum Direction {
		UP, DOWN, LEFT, RIGHT
	}
}
