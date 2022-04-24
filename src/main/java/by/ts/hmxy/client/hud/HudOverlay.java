
package by.ts.hmxy.client.hud;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;

@Mod.EventBusSubscriber({ Dist.CLIENT })
public class HudOverlay {
	
	public static ResourceLocation hudLocation=new ResourceLocation("hmxy:textures/hud/hud.png");
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGameOverlayEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.gameMode.getPlayerMode() == GameType.SURVIVAL||mc.gameMode.getPlayerMode() == GameType.CREATIVE) {
			event.setCanceled(true);
			int gw = event.getWindow().getGuiScaledWidth();
			int gh = event.getWindow().getGuiScaledHeight();
			int pw = 216;//材质的宽度
			int ph = 41;//材质的高度
			int tw=222;
			int th=50;
			int px=(gw - pw) / 2;
			int py=gh - ph;
			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
					GlStateManager.DestFactor.ZERO);
			RenderSystem.setShaderColor(1, 1, 1, 1);
			RenderSystem.setShaderTexture(0,hudLocation);
			//物品栏
			Gui.blit(event.getMatrixStack(), px, gh - ph, 0, 0, pw, ph, tw, th);
			//经验条
			Gui.blit(event.getMatrixStack(), px+3, py+35, 0, 41, 200, 3, tw, th);
			//灵力
			Gui.blit(event.getMatrixStack(), px+23, py+8, 0, 44, 200, 3, tw, th);
			RenderSystem.depthMask(true);
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			RenderSystem.disableBlend();
			RenderSystem.setShaderColor(1, 1, 1, 1);
		}
	}
}
