package by.ts.hmxy.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;

@OnlyIn(Dist.CLIENT)
public class GuiHelper {

	private final static int yOffset = 10;

	// Copy from the villain and do some change.
	public static void renderSelectedItemName(Gui gui, PoseStack pPoseStack) {
		Minecraft mc = Minecraft.getInstance();
		mc.getProfiler().push("selectedItemName");
		if (gui.toolHighlightTimer > 0 && !gui.lastToolHighlight.isEmpty()) {
			MutableComponent mutablecomponent = (new TextComponent("")).append(gui.lastToolHighlight.getHoverName())
					.withStyle(gui.lastToolHighlight.getRarity().color);
			if (gui.lastToolHighlight.hasCustomHoverName()) {
				mutablecomponent.withStyle(ChatFormatting.ITALIC);
			}

			Component highlightTip = gui.lastToolHighlight.getHighlightTip(mutablecomponent);
			int i = gui.getFont().width(highlightTip);
			int j = (gui.screenWidth - i) / 2;
			int k = gui.screenHeight - 59 + yOffset;
			int l = (int) ((float) gui.toolHighlightTimer * 256.0F / 10.0F);
			if (l > 255) {
				l = 255;
			}

			if (l > 0) {
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				Gui.fill(pPoseStack, j - 2, k - 2, j + i + 2, k + 9 + 2, mc.options.getBackgroundColor(0));
				Font font = net.minecraftforge.client.RenderProperties.get(gui.lastToolHighlight)
						.getFont(gui.lastToolHighlight);
				if (font == null) {
					gui.getFont().drawShadow(pPoseStack, highlightTip, (float) j, (float) k, 16777215 + (l << 24));
				} else {
					j = (gui.screenWidth - font.width(highlightTip)) / 2;
					font.drawShadow(pPoseStack, highlightTip, (float) j, (float) k, 16777215 + (l << 24));
				}
				RenderSystem.disableBlend();
			}
		}
		mc.getProfiler().pop();
	}

	//Copy from forge and do some change.
	public static void renderHealthMount(ForgeIngameGui forgeGui, int width, int height, PoseStack mStack) {
		Minecraft mc = Minecraft.getInstance();
		Player player = (Player) mc.getCameraEntity();
		Entity tmp = player.getVehicle();
		if (!(tmp instanceof LivingEntity))
			return;

		bind(ForgeIngameGui.GUI_ICONS_LOCATION);

		boolean unused = false;
		int left_align = width / 2 + 91;

		mc.getProfiler().popPush("mountHealth");
		RenderSystem.enableBlend();
		LivingEntity mount = (LivingEntity) tmp;
		int health = (int) Math.ceil((double) mount.getHealth());
		float healthMax = mount.getMaxHealth();
		int hearts = (int) (healthMax + 0.5F) / 2;

		if (hearts > 30)
			hearts = 30;

		final int MARGIN = 52;
		final int BACKGROUND = MARGIN + (unused ? 1 : 0);
		final int HALF = MARGIN + 45;
		final int FULL = MARGIN + 36;
		int yOffset=-20;
		for (int heart = 0; hearts > 0; heart += 20) {
			int top = height - forgeGui.right_height;

			int rowCount = Math.min(hearts, 10);
			hearts -= rowCount;

			for (int i = 0; i < rowCount; ++i) {
				int x = left_align - i * 8 - 9;
				forgeGui.blit(mStack, x, top+yOffset, BACKGROUND, 9, 9, 9);

				if (i * 2 + 1 + heart < health)
					forgeGui.blit(mStack, x, top+yOffset, FULL, 9, 9, 9);
				else if (i * 2 + 1 + heart == health)
					forgeGui.blit(mStack, x, top+yOffset, HALF, 9, 9, 9);
			}

			forgeGui.right_height += 10;
		}
		RenderSystem.disableBlend();
	}

	static void bind(ResourceLocation res) {
		RenderSystem.setShaderTexture(0, res);
	}
}
