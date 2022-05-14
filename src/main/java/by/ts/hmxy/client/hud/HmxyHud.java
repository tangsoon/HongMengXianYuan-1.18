package by.ts.hmxy.client.hud;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import by.ts.hmxy.config.Configs;
import by.ts.hmxy.util.HmxyHelper;
import java.util.List;
import java.util.Optional;
import com.mojang.blaze3d.platform.GlStateManager;

@Mod.EventBusSubscriber({ Dist.CLIENT })
public class HmxyHud {

	public static ResourceLocation hudLocation = new ResourceLocation("hmxy:textures/hud/hud.png");

	public static final Optional<IIngameOverlay> HOTBAR_AND_PROPERTIES_ELEMENT = Optional
			.of(OverlayRegistry.registerOverlayTop("horbar and properties", (gui, mStack, partialTicks, gw, gh) -> {
				Minecraft mc = Minecraft.getInstance();
				if ((mc.gameMode.getPlayerMode() == GameType.SURVIVAL
						|| mc.gameMode.getPlayerMode() == GameType.CREATIVE) && Configs.isToolBarOpen.get()
						&& !mc.options.hideGui) {
					Player player = mc.player;

					int pw = 216;// 材质的宽度
					int ph = 41;// 材质的高度
					int tw = 246;// 材质总宽度
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
					Gui.blit(mStack, px, gh - ph, 0, 0, pw, ph, tw, th);
					// 经验条
					Gui.blit(mStack, px + 3, py + 35, 0, 41, 200, 3, tw, th);
					// 灵力条
					float lingLi =HmxyHelper.getLingLi(player);
					int count = 1;
					while (lingLi > count * 20) {
						lingLi -= count * 20;
						count += 1;
					}
					for (int i = 0; i < count - 1; i++) {
						Gui.blit(mStack, px + 23, py + 8, 0, 44, 200, 3, tw, th);
					}
					int minbusOffset = (int) (200.0F * (1.0F - lingLi / (count * 20.0F)));
					Gui.blit(mStack, px + 23, py + 8, 0, 44, 200 - minbusOffset, 3, tw - minbusOffset, th);
					// 血条
					float health = player.getHealth();
					count = 1;
					while (health > count * 20) {
						health -= count * 20;
						count += 1;
					}
					for (int i = 0; i < count - 1; i++) {
						Gui.blit(mStack, px + 23, py + 3, 0, 47, 200, 3, tw, th);
					}
					int lifeOffset = (int) (200.0F * (1.0F - health / (count * 20.0F)));
					Gui.blit(mStack, px + 23, py + 3, 0, 47, 200 - lifeOffset, 3, tw - lifeOffset, th);
					// 食物
					FoodData foodData = player.getFoodData();
					int foodOffset = (int) (20.0F * (1.0F - foodData.getFoodLevel() / 20.0F));
					Gui.blit(mStack, px + 205, py + 3 + foodOffset, 216, foodOffset, 3, 35, tw, th);
					int saturactionOffset = (int) (20.0F * (1.0F - foodData.getSaturationLevel() / 20.0F));
					Gui.blit(mStack, px + 205, py + 3 + saturactionOffset, 216, saturactionOffset, 3, 35, tw, th);
					// 氧气
					int air = player.getAirSupply();
					int airOffset = (int) (35.0F * (1.0F - air / 300.0F));
					Gui.blit(mStack, px + 210, py + 3 + airOffset, 219, airOffset, 3, 35, tw, th);
					// 真元
					//TODO 重新画个真元的UI
					int zhenYuan=HmxyHelper.getZhenYuan(player);
					int xiaoJingJie=HmxyHelper.getXiaoJingJie(player);
					int necessaryZhenYuan=HmxyHelper.getNecessaryZhenYuan(xiaoJingJie+1);
					if(necessaryZhenYuan!=0) {
						int zhenYuanOffSet= (int) ((1.0F-(float)zhenYuan/necessaryZhenYuan)*18);
						Gui.blit(mStack, px + 3, py + 3, 200, 41, 18-zhenYuanOffSet, 8, tw, th);	
					}
					// 选择框
					int selected = player.getInventory().selected;
					Gui.blit(mStack, px + 21 + 20 * selected, py + 11, 222, 0, 24, 24, tw, th);

					// 境界
					int daJingJie=HmxyHelper.getDaJingJieByXiao(xiaoJingJie);
					String jingjie =HmxyHelper.JingJies.get(daJingJie).getZhName(); 
					mc.font.draw(mStack, jingjie, px + 3, py + 3, 0xc69636);
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
					// 副手物品
					render.renderGuiItem(offHand, px + 5, py + 15);
					render.renderGuiItemDecorations(mc.font, offHand, px + 5, py + 15);

					RenderSystem.depthMask(true);
					RenderSystem.defaultBlendFunc();
					RenderSystem.enableDepthTest();
					RenderSystem.disableBlend();
					RenderSystem.setShaderColor(1, 1, 1, 1);
				}
			}));

	public static final IIngameOverlay ITEM_NAME_ELEMENT = OverlayRegistry.registerOverlayTop("Item Name",
			(gui, mStack, partialTicks, screenWidth, screenHeight) -> {
				Minecraft mc = Minecraft.getInstance();
				if (!mc.options.hideGui&&Configs.isToolBarOpen.get()) {
					gui.setupOverlayRenderState(true, false);
					if (mc.options.heldItemTooltips && mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {
						renderSelectedItemName(gui, mStack);
					} else if (mc.player.isSpectator()) {
						gui.spectatorGui.renderTooltip(mStack);
					}
				}
			});

	public static final IIngameOverlay MOUNT_HEALTH_ELEMENT = OverlayRegistry.registerOverlayTop("Mount Health",
			(gui, mStack, partialTicks, screenWidth, screenHeight) -> {
				Minecraft mc = Minecraft.getInstance();
				if (!mc.options.hideGui && gui.shouldDrawSurvivalElements()&&Configs.isToolBarOpen.get()) {
					gui.setupOverlayRenderState(true, false);
					renderHealthMount(gui, screenWidth, screenHeight, mStack);
				}
			});

	public static final IIngameOverlay JUMP_BAR_ELEMENT = OverlayRegistry.registerOverlayTop("Jump Bar",
			(gui, mStack, partialTicks, screenWidth, screenHeight) -> {
				Minecraft mc = Minecraft.getInstance();
				if (mc.player.isRidingJumpable() && !mc.options.hideGui&&Configs.isToolBarOpen.get()) {
					gui.setupOverlayRenderState(true, false);
					int pX = screenWidth / 2 - 91;
					RenderSystem.setShaderTexture(0, ForgeIngameGui.GUI_ICONS_LOCATION);
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					RenderSystem.disableBlend();
					mc.getProfiler().push("jumpBar");
					RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
					float f = mc.player.getJumpRidingScale();
					int yOffset=-20;
					int j = (int) (f * 183.0F);
					int k = gui.screenHeight - 32 + 3;
					gui.blit(mStack, pX, k+yOffset, 0, 84, 182, 5);
					if (j > 0) {
						gui.blit(mStack, pX, k+yOffset, 0, 89, j, 5);
					}
					mc.getProfiler().pop();
					RenderSystem.enableBlend();
					mc.getProfiler().pop();
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				}
			});
	
	public static final IIngameOverlay STAMINA_ELEMENT = OverlayRegistry.registerOverlayTop("Stamina",
			(gui, mStack, partialTicks, screenWidth, screenHeight) -> {
				Minecraft mc = Minecraft.getInstance();
				if (HmxyHelper.getStamina(mc.player)<HmxyHelper.getMaxStamina(mc.player) && !mc.options.hideGui&&Configs.isToolBarOpen.get()) {
					gui.setupOverlayRenderState(true, false);
					int pX = screenWidth / 2 - 91;
					RenderSystem.setShaderTexture(0, ForgeIngameGui.GUI_ICONS_LOCATION);
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					RenderSystem.disableBlend();
					mc.getProfiler().push("stamina");
					RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
					float f=(float) (HmxyHelper.getStamina(mc.player)/HmxyHelper.getMaxStamina(mc.player));
					int yOffset=-20;
					int j = (int) (f * 183.0F);
					int k = gui.screenHeight - 32 + 3;
					gui.blit(mStack, pX, k+yOffset, 0, 84, 182, 5);
					if (j > 0) {
						gui.blit(mStack, pX, k+yOffset, 0, 89, j, 5);
					}
					mc.getProfiler().pop();
					RenderSystem.enableBlend();
					mc.getProfiler().pop();
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				}
			});
	
	public static void init() {

	}
	
	private final static int SELECTED_ITEM_NAME_OFFSET = 10;
	
	// Copy from the villain and do some change.
	private static void renderSelectedItemName(Gui gui, PoseStack pPoseStack) {
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
			int k = gui.screenHeight - 59 + SELECTED_ITEM_NAME_OFFSET;
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
	private static void renderHealthMount(ForgeIngameGui forgeGui, int width, int height, PoseStack mStack) {
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

	private static void bind(ResourceLocation res) {
		RenderSystem.setShaderTexture(0, res);
	}
}
