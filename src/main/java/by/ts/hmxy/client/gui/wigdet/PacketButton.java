package by.ts.hmxy.client.gui.wigdet;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import by.ts.hmxy.net.ButtonPacket;
import by.ts.hmxy.net.Messages;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This button will send packet to server when player click it
 * 
 * @author tangsoon
 *
 */
@OnlyIn(Dist.CLIENT)
public class PacketButton extends ImageButton {
	public PacketButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex,
			ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, ButtonPacket.Handler handler) {
		super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pTextureWidth,
				pTextureHeight, b -> {
					Messages.sendToServer(new ButtonPacket(handler));
				}, TextComponent.EMPTY);
	}

	public boolean isClicking = false;

	public PacketButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart,
			ResourceLocation pResourceLocation, ButtonPacket.Handler handler) {
		this(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pHeight, pResourceLocation, 256, 256, handler);
	}

	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, this.resourceLocation);
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		if (isClicking) {
			blit(pPoseStack, this.x, this.y, (float) this.xTexStart, (float) (this.yTexStart + this.yDiffTex * 2),
					this.width, this.height, this.textureWidth, this.textureHeight);
		} else {
			blit(pPoseStack, this.x, this.y, (float) this.xTexStart, (float) (this.yTexStart), this.width, this.height,
					this.textureWidth, this.textureHeight);
			if (this.isHovered) {
				blit(pPoseStack, this.x, this.y, (float) this.xTexStart, (float) (this.yTexStart + this.yDiffTex),
						this.width, this.height, this.textureWidth, this.textureHeight);
			}
		}

		if (this.isHovered) {
			this.renderToolTip(pPoseStack, pMouseX, pMouseY);
		}
		// TODO 渲染按钮字体
//		Minecraft mc= Minecraft.getInstance();
//		int swidth= mc.font.width("你好");
//		int shight=mc.font.lineHeight;
//		GuiComponent.drawString(pPoseStack, mc.font, "你好", (this.width-swidth)/2+this.x, this.y+(this.height-shight)/2, 0xffffff);
	}

	public void onClick(double pMouseX, double pMouseY) {
		super.onClick(pMouseX, pMouseY);
		this.isClicking = true;
	}

	public void onRelease(double pMouseX, double pMouseY) {
		super.onRelease(pMouseX, pMouseY);
		this.isClicking = false;
	}
}
