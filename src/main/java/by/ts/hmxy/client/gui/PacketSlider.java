package by.ts.hmxy.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import by.ts.hmxy.net.Messages;
import by.ts.hmxy.net.SliderPacket;
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
public class PacketSlider extends AbstractSliderButton {

	private ResourceLocation texture;
	private SliderPacket.Handler handler;
	private double valueMulti;
	protected TransMsg msg;

	public PacketSlider(int pX, int pY, int pWidth, int pHeight, TransMsg msg, double pValue, double valueMulti,
			SliderPacket.Handler handler, ResourceLocation texture) {
		super(pX, pY, pWidth, pHeight, msg.create(valueMulti * pValue), pValue);
		this.texture = texture;
		this.valueMulti = valueMulti;
		this.handler = handler;
		this.msg = msg;
		updateMessage();
	}

	@Override
	protected void updateMessage() {
		float a = ((int) (this.value * valueMulti * 10)) / 10F;
		this.setMessage(msg.create(a));
	}

	@Override
	protected void applyValue() {
		Messages.sendToServer(new SliderPacket(handler, value));
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
			      			      
			      this.blit(pPoseStack, this.x + (int)(this.value * (double)(this.width-6)), this.y-3, 98, 242, 6, 14);
			      if(isHoveredOrFocused()) {
			    	  this.blit(pPoseStack, this.x + (int)(this.value * (double)(this.width - 6)), this.y-3, 98+6, 242, 6, 14);
			      }
			      
			      pPoseStack.pushPose();
			      int textX=this.x+Math.abs(this.width-(int)(font.width(this.getMessage().getString())*0.5))/2;
			      int textY=this.y+Math.abs((this.height-(int)(font.lineHeight*0.5))/2);
			      pPoseStack.translate(textX, textY, 0);
			      pPoseStack.scale(0.5F, 0.5F, 0F);
			      pPoseStack.translate(-textX, -textY, 0);
			      font.draw(pPoseStack, getMessage(), textX, textY, 0xffffffff);
			      font.drawShadow(pPoseStack, getMessage(), textX, textY, 0xffffffff);
			      pPoseStack.popPose();
		}
	}
}
