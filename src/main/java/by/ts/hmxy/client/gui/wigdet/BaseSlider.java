package by.ts.hmxy.client.gui.wigdet;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import by.ts.hmxy.util.HmxyHelper;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.resources.ResourceLocation;

/**
 * this will atomaticly send packet to server when player modify the value of
 * slider
 * 
 * @author tangsoon
 */

//TODO bug:can not drag
public abstract class BaseSlider extends AbstractSliderButton implements HoverWidget{

	protected ResourceLocation texture;
	protected double valueMulti;
	protected TransMsg msg;

	public BaseSlider(int pX, int pY, int pWidth, int pHeight, TransMsg msg, double pValue, double valueMulti,
			ResourceLocation texture) {
		super(pX, pY, pWidth, pHeight, msg.get(valueMulti * pValue), pValue);
		this.texture = texture;
		this.valueMulti = valueMulti;
		this.msg = msg;
		updateMessage();
	}

	@Override
	protected void updateMessage() {
		float a = ((int) (this.value * valueMulti * 10)) / 10F;
		this.setMessage(msg.get(a));
	}

	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		if (this.visible) {
			this.isHovered = pMouseX >= this.x && pMouseY >= this.y && pMouseX < this.x + this.width
					&& pMouseY < this.y + this.height;
			Minecraft minecraft = Minecraft.getInstance();
			Font font = minecraft.font;
			RenderSystem.setShaderTexture(0, this.texture);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
			RenderSystem.enableBlend();

			this.blit(pPoseStack, this.x + (int) (this.value * (double) (this.width - 6)), this.y - 3, 98, 242, 6, 14);
			if (isHoveredOrFocused()) {
				this.blit(pPoseStack, this.x + (int) (this.value * (double) (this.width - 6)), this.y - 3, 98 + 6, 242,
						6, 14);
			}

			HmxyHelper.drawCenterString(pPoseStack, pMouseX, pMouseY, pPartialTick, 0.5F, this.x, this.y,
					this.getWidth(), this.getHeight(), font, getMessage(), 0xffffffff, true);
		}
	}
}
