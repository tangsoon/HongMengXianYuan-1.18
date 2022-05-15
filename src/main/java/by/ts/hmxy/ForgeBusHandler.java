package by.ts.hmxy;

import com.google.common.collect.ImmutableList;

import by.ts.hmxy.block.Break;
import by.ts.hmxy.block.EntityPlace;
import by.ts.hmxy.capability.Capabilities;
import by.ts.hmxy.capability.ChunkInfoProvider;
import by.ts.hmxy.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeBusHandler {
	@SubscribeEvent
	public void onSave(ChunkDataEvent.Save event) {
		ChunkAccess access = event.getChunk();
		if (access instanceof LevelChunk chunk && !chunk.getLevel().isClientSide) {
			chunk.getCapability(Capabilities.CHUNK_INFO).ifPresent(info -> {
				event.getData().put("chunkInfo", info.serializeNBT());
			});
		}
	}

	@SubscribeEvent
	public void onLoad(ChunkDataEvent.Load event) {
		ChunkAccess access = event.getChunk();
		if (access instanceof LevelChunk chunk && !chunk.getLevel().isClientSide) {
			if (chunk.getLevel() instanceof ServerLevel) {
				chunk.getCapability(Capabilities.CHUNK_INFO).ifPresent(info -> {
					info.deserializeNBT(event.getData().getCompound("chunkInfo"));
				});
			}
		}
	}

	@SubscribeEvent
	public void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<LevelChunk> event) {
		if (!event.getObject().getCapability(Capabilities.CHUNK_INFO).isPresent()
				&& !event.getObject().getLevel().isClientSide) {
			event.addCapability(new ResourceLocation(HmxyMod.MOD_ID, "chunk_info"),
					new ChunkInfoProvider(event.getObject()));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public final ImmutableList<IIngameOverlay> CANCEL_LIST = new ImmutableList.Builder<IIngameOverlay>()
			.add(ForgeIngameGui.HOTBAR_ELEMENT).add(ForgeIngameGui.ITEM_NAME_ELEMENT)
			.add(ForgeIngameGui.MOUNT_HEALTH_ELEMENT).add(ForgeIngameGui.PLAYER_HEALTH_ELEMENT)
			.add(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT).add(ForgeIngameGui.FOOD_LEVEL_ELEMENT)
			.add(ForgeIngameGui.AIR_LEVEL_ELEMENT).add(ForgeIngameGui.MOUNT_HEALTH_ELEMENT)
			.add(ForgeIngameGui.JUMP_BAR_ELEMENT).build();

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void eventHandler(RenderGameOverlayEvent.PreLayer event) {
		Minecraft mc = Minecraft.getInstance();
		if ((mc.gameMode.getPlayerMode() == GameType.SURVIVAL || mc.gameMode.getPlayerMode() == GameType.CREATIVE)
				&& Configs.isToolBarOpen.get() && CANCEL_LIST.stream().anyMatch(o -> o == event.getOverlay())) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onEntityPlace(EntityPlaceEvent event) {
		if(event.getPlacedBlock().getBlock() instanceof EntityPlace block) {
			block.onEntityPlace(event);
		}
	}

	@SubscribeEvent
	public void onBreak(BreakEvent event) {
		if(event.getState().getBlock() instanceof Break block) {
			block.onBreak(event);
		}
	}
}
