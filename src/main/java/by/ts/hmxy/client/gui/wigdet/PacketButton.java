package by.ts.hmxy.client.gui.wigdet;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import by.ts.hmxy.net.ButtonPacket;
import by.ts.hmxy.net.Messages;
import by.ts.hmxy.util.HmxyHelper;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
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
public class PacketButton extends ImageButton implements HoveredWidget {

	private Supplier<List<Component>> componentSup;

	public boolean isClicking = false;

	/**
	 * 默认Message为Empty
	 * @param pX
	 * @param pY
	 * @param pWidth
	 * @param pHeight
	 * @param pXTexStart
	 * @param pYTexStart
	 * @param pResourceLocation
	 * @param handler
	 */
	public PacketButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart,
			ResourceLocation pResourceLocation, ButtonPacket.Handler handler) {
		this(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pHeight, pResourceLocation, 256, 256, handler,
				() -> Arrays.asList(TransMsg.EMPTY), TransMsg.EMPTY);
	}

	public PacketButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart,
			ResourceLocation pResourceLocation, ButtonPacket.Handler handler, Supplier<List<Component>> componentSup,
			Component message) {
		this(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pHeight, pResourceLocation, 256, 256, handler,
				componentSup, message);
	}

	public PacketButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex,
			ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, ButtonPacket.Handler handler,
			Supplier<List<Component>> componentSup, Component message) {
		super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pTextureWidth,
				pTextureHeight, b -> {
					Messages.sendToServer(new ButtonPacket(handler));
				}, message);
		this.componentSup = componentSup;
	}

	public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		//RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, this.resourceLocation);
		RenderSystem.enableBlend();
		Minecraft mc = Minecraft.getInstance();
		int offsetY=this.isClicking?1:0;
		if (isClicking) {
			
			blit(pPoseStack, this.x, this.y, (float) this.xTexStart, (float) (this.yTexStart + this.yDiffTex * 2),
					this.width, this.height, this.textureWidth, this.textureHeight);
			
			HmxyHelper.drawCenterString(pPoseStack, pMouseX, pMouseY, pPartialTick, 0.7F, this.x, this.y+offsetY, this.width,
					this.height, mc.font, getMessage(), 0xa88467, false);
		} else {
			blit(pPoseStack, this.x, this.y, (float) this.xTexStart, (float) (this.yTexStart), this.width, this.height,
					this.textureWidth, this.textureHeight);
			if (this.isHovered) {
				HmxyHelper.drawCenterString(pPoseStack, pMouseX, pMouseY, pPartialTick, 0.7F, this.x, this.y+offsetY, this.width,
						this.height, mc.font, getMessage(), 0xa88467, false);
				RenderSystem.setShaderTexture(0, this.resourceLocation);
				RenderSystem.enableBlend();
				blit(pPoseStack, this.x, this.y, (float) this.xTexStart, (float) (this.yTexStart + this.yDiffTex),
						this.width, this.height, this.textureWidth, this.textureHeight);
			}
			else {
				HmxyHelper.drawCenterString(pPoseStack, pMouseX, pMouseY, pPartialTick, 0.7F, this.x, this.y+offsetY, this.width,
						this.height, mc.font, getMessage(), 0xa88467, false);
			}
		}
		
		
	}

	public void onClick(double pMouseX, double pMouseY) {
		super.onClick(pMouseX, pMouseY);
		this.isClicking = true;
	}

	public void onRelease(double pMouseX, double pMouseY) {
		super.onRelease(pMouseX, pMouseY);
		this.isClicking = false;
	}

	@Override
	public List<Component> getTips() {
			return componentSup.get();	
	}
}
