package by.ts.hmxy.client.hud;

import com.google.common.collect.ImmutableList;
import by.ts.hmxy.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderOverlayHander {
	public final ImmutableList<IIngameOverlay> CANCEL_LIST = new ImmutableList.Builder<IIngameOverlay>()
			.add(ForgeIngameGui.HOTBAR_ELEMENT).add(ForgeIngameGui.ITEM_NAME_ELEMENT)
			.add(ForgeIngameGui.MOUNT_HEALTH_ELEMENT).add(ForgeIngameGui.PLAYER_HEALTH_ELEMENT)
			.add(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT).add(ForgeIngameGui.FOOD_LEVEL_ELEMENT)
			.add(ForgeIngameGui.AIR_LEVEL_ELEMENT).add(ForgeIngameGui.MOUNT_HEALTH_ELEMENT)
			.add(ForgeIngameGui.JUMP_BAR_ELEMENT).build();

	@SubscribeEvent
	public void eventHandler(RenderGameOverlayEvent.PreLayer event) {
		Minecraft mc = Minecraft.getInstance();
		if ((mc.gameMode.getPlayerMode() == GameType.SURVIVAL || mc.gameMode.getPlayerMode() == GameType.CREATIVE)
				&&Configs.isToolBarOpen.get() && CANCEL_LIST.stream().anyMatch(o -> o == event.getOverlay())) {
			event.setCanceled(true);
		}
	}
}
