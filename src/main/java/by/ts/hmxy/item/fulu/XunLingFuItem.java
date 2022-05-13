package by.ts.hmxy.item.fulu;

import by.ts.hmxy.capability.Capabilities;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * @author tangsoon
 *
 */
public class XunLingFuItem extends FuLuItem {
	
	public XunLingFuItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		if (!pLevel.isClientSide) {
			BlockPos pos = pPlayer.blockPosition();
			int chunkX = SectionPos.blockToSectionCoord(pos.getX());
			int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
			LevelChunk chunk = pLevel.getChunk(chunkX, chunkZ);
			chunk.getCapability(Capabilities.CHUNK_INFO).ifPresent(info -> {
				if (pPlayer instanceof ServerPlayer sPlayer
						&& sPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE) {
					pPlayer.sendMessage(TransMsg.XUN_LING_FU.create(chunkX, chunkZ,info.getLingQi()), Util.NIL_UUID);
				}
			});
		}
		return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand));
	}
}
