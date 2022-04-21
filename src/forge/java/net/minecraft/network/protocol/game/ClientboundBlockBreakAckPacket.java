package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientboundBlockBreakAckPacket implements Packet<ClientGamePacketListener> {
   /** Unused (probably related to the unused parameter in the constructor) */
   private static final Logger LOGGER = LogManager.getLogger();
   private final BlockPos pos;
   private final BlockState state;
   private final ServerboundPlayerActionPacket.Action action;
   private final boolean allGood;

   public ClientboundBlockBreakAckPacket(BlockPos pPos, BlockState pState, ServerboundPlayerActionPacket.Action pAction, boolean pAllGood, String pReason) {
      this.pos = pPos.immutable();
      this.state = pState;
      this.action = pAction;
      this.allGood = pAllGood;
   }

   public ClientboundBlockBreakAckPacket(FriendlyByteBuf pBuffer) {
      this.pos = pBuffer.readBlockPos();
      this.state = Block.BLOCK_STATE_REGISTRY.byId(pBuffer.readVarInt());
      this.action = pBuffer.readEnum(ServerboundPlayerActionPacket.Action.class);
      this.allGood = pBuffer.readBoolean();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void write(FriendlyByteBuf pBuffer) {
      pBuffer.writeBlockPos(this.pos);
      pBuffer.writeVarInt(Block.getId(this.state));
      pBuffer.writeEnum(this.action);
      pBuffer.writeBoolean(this.allGood);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void handle(ClientGamePacketListener pHandler) {
      pHandler.handleBlockBreakAck(this);
   }

   public BlockState getState() {
      return this.state;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public boolean allGood() {
      return this.allGood;
   }

   public ServerboundPlayerActionPacket.Action action() {
      return this.action;
   }
}