
package by.ts.hmxy.client.hud;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

@Mod.EventBusSubscriber({ Dist.CLIENT })
public class HudOverlay {

	public static ResourceLocation hudLocation = new ResourceLocation("hmxy:textures/hud/hud.png");

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void eventHandler(RenderGameOverlayEvent.PreLayer event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.gameMode.getPlayerMode() == GameType.SURVIVAL || mc.gameMode.getPlayerMode() == GameType.CREATIVE) {
			event.setCanceled(true);
			Player player = mc.player;

			int gw = event.getWindow().getGuiScaledWidth();
			int gh = event.getWindow().getGuiScaledHeight();
			int pw = 216;// 材质的宽度
			int ph = 41;// 材质的高度
			int tw = 222;// 材质总宽度
			int th = 50;// 材质总高度
			int px = (gw - pw) / 2;// 渲染位置X
			int py = gh - ph;// 渲染位置Y
			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
					GlStateManager.DestFactor.ZERO);
			RenderSystem.setShaderColor(1, 1, 1, 1);
			RenderSystem.setShaderTexture(0, hudLocation);
			// 物品栏
			Gui.blit(event.getMatrixStack(), px, gh - ph, 0, 0, pw, ph, tw, th);
			// 经验条
			Gui.blit(event.getMatrixStack(), px + 3, py + 35, 0, 41, 200, 3, tw, th);
			// 灵力条
			// TODO 改成灵力而不是生命值
			float minbus = player.getHealth();
			int count = 1;
			while (minbus > count * 20) {
				minbus -= count * 20;
				count += 1;
			}
			for (int i = 0; i < count - 1; i++) {
				Gui.blit(event.getMatrixStack(), px + 23, py + 8, 0, 44, 200, 3, tw, th);
			}
			int minbusOffset = (int) (200.0F * (1.0F - minbus / (count * 20.0F)));
			Gui.blit(event.getMatrixStack(), px + 23, py + 8, 0, 44, 200 - minbusOffset, 3, tw - minbusOffset, th);
			// 血条
			float health = player.getHealth();
			count = 1;
			while (health > count * 20) {
				health -= count * 20;
				count += 1;
			}
			for (int i = 0; i < count - 1; i++) {
				Gui.blit(event.getMatrixStack(), px + 23, py + 3, 0, 47, 200, 3, tw, th);
			}
			int lifeOffset = (int) (200.0F * (1.0F - health / (count * 20.0F)));
			Gui.blit(event.getMatrixStack(), px + 23, py + 3, 0, 47, 200 - lifeOffset, 3, tw - lifeOffset, th);
			// 食物
			FoodData foodData = player.getFoodData();
			int foodOffset = (int) (20.0F * (1.0F - foodData.getFoodLevel() / 20.0F));
			Gui.blit(event.getMatrixStack(), px + 205, py + 3 + foodOffset, 216, foodOffset, 3, 35, tw, th);
			int saturactionOffset = (int) (20.0F * (1.0F - foodData.getSaturationLevel() / 20.0F));
			Gui.blit(event.getMatrixStack(), px + 205, py + 3 + saturactionOffset, 216, saturactionOffset, 3, 35, tw,
					th);
			// 氧气
			int air = player.getAirSupply();
			int airOffset = (int) (35.0F * (1.0F - air / 300.0F));
			Gui.blit(event.getMatrixStack(), px + 210, py + 3 + airOffset, 219, airOffset, 3, 35, tw, th);
			// 真元
			Gui.blit(event.getMatrixStack(), px + 3, py + 3, 200, 41, 18, 8, tw, th);
			RenderSystem.depthMask(true);
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			RenderSystem.disableBlend();
			RenderSystem.setShaderColor(1, 1, 1, 1);
			// 境界
			String jingjie = "境界";
			mc.font.draw(event.getMatrixStack(), jingjie, px + 2, py + 4, 0xc69636);
			// 物品栏
			List<ItemStack> itemStacks = player.getInventory().items;
			ItemRenderer render = mc.getItemRenderer();
			ItemStack itemStack;
			for (int i = 0; i < 9; i++) {
				itemStack = itemStacks.get(i);
				render.renderGuiItem(itemStack, px + 25 + 20 * i, py + 15);
				render.renderGuiItemDecorations(mc.font, itemStack, px + 25 + 20 * i, py + 15);
			}
			NonNullList<ItemStack> offhandList = player.getInventory().offhand;
			ItemStack offHand = offhandList.get(0);

			render.renderGuiItem(offHand, px + 5, py + 15);
			render.renderGuiItemDecorations(mc.font, offHand, px + 5, py + 15);

		}
	}
}
