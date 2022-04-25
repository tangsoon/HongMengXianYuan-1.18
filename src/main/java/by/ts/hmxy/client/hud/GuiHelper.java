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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiHelper {

	private final static int yOffset = 5;

	//Copy from the villain and do some change.
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
			if (!mc.gameMode.canHurtPlayer()) {
				k += 14;
			}

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
}
