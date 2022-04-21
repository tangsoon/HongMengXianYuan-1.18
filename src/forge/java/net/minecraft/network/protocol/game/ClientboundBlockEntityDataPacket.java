package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundBlockEntityDataPacket implements Packet<ClientGamePacketListener> {
   public static final int TYPE_MOB_SPAWNER = 1;
   public static final int TYPE_ADV_COMMAND = 2;
   public static final int TYPE_BEACON = 3;
   public static final int TYPE_SKULL = 4;
   public static final int TYPE_CONDUIT = 5;
   public static final int TYPE_BANNER = 6;
   public static final int TYPE_STRUCT_COMMAND = 7;
   public static final int TYPE_END_GATEWAY = 8;
   public static final int TYPE_SIGN = 9;
   public static final int TYPE_BED = 11;
   public static final int TYPE_JIGSAW = 12;
   public static final int TYPE_CAMPFIRE = 13;
   public static final int TYPE_BEEHIVE = 14;
   private final BlockPos pos;
   /** Used only for vanilla tile entities */
   private final int type;
   private final CompoundTag tag;

   public ClientboundBlockEntityDataPacket(BlockPos pPos, int pType, CompoundTag pTag) {
      this.pos = pPos;
      this.type = pType;
      this.tag = pTag;
   }

   public ClientboundBlockEntityDataPacket(FriendlyByteBuf pBuffer) {
      this.pos = pBuffer.readBlockPos();
      this.type = pBuffer.readUnsignedByte();
      this.tag = pBuffer.readNbt();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void write(FriendlyByteBuf pBuffer) {
      pBuffer.writeBlockPos(this.pos);
      pBuffer.writeByte((byte)this.type);
      pBuffer.writeNbt(this.tag);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void handle(ClientGamePacketListener pHandler) {
      pHandler.handleBlockEntityData(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getType() {
      return this.type;
   }

   public CompoundTag getTag() {
      return this.tag;
   }
}