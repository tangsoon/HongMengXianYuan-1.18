package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;

public class ServerboundClientInformationPacket implements Packet<ServerGamePacketListener> {
   public static final int MAX_LANGUAGE_LENGTH = 16;
   private final String language;
   private final int viewDistance;
   private final ChatVisiblity chatVisibility;
   private final boolean chatColors;
   private final int modelCustomisation;
   private final HumanoidArm mainHand;
   private final boolean textFilteringEnabled;

   public ServerboundClientInformationPacket(String pLanguage, int pViewDistance, ChatVisiblity pChatVisiblity, boolean pChatColors, int pModelCustomisation, HumanoidArm pMainHand, boolean pTextFilteringEnabled) {
      this.language = pLanguage;
      this.viewDistance = pViewDistance;
      this.chatVisibility = pChatVisiblity;
      this.chatColors = pChatColors;
      this.modelCustomisation = pModelCustomisation;
      this.mainHand = pMainHand;
      this.textFilteringEnabled = pTextFilteringEnabled;
   }

   public ServerboundClientInformationPacket(FriendlyByteBuf pBuffer) {
      this.language = pBuffer.readUtf(16);
      this.viewDistance = pBuffer.readByte();
      this.chatVisibility = pBuffer.readEnum(ChatVisiblity.class);
      this.chatColors = pBuffer.readBoolean();
      this.modelCustomisation = pBuffer.readUnsignedByte();
      this.mainHand = pBuffer.readEnum(HumanoidArm.class);
      this.textFilteringEnabled = pBuffer.readBoolean();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void write(FriendlyByteBuf pBuffer) {
      pBuffer.writeUtf(this.language);
      pBuffer.writeByte(this.viewDistance);
      pBuffer.writeEnum(this.chatVisibility);
      pBuffer.writeBoolean(this.chatColors);
      pBuffer.writeByte(this.modelCustomisation);
      pBuffer.writeEnum(this.mainHand);
      pBuffer.writeBoolean(this.textFilteringEnabled);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void handle(ServerGamePacketListener pHandler) {
      pHandler.handleClientInformation(this);
   }

   public String getLanguage() {
      return this.language;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public ChatVisiblity getChatVisibility() {
      return this.chatVisibility;
   }

   public boolean getChatColors() {
      return this.chatColors;
   }

   public int getModelCustomisation() {
      return this.modelCustomisation;
   }

   public HumanoidArm getMainHand() {
      return this.mainHand;
   }

   public boolean isTextFilteringEnabled() {
      return this.textFilteringEnabled;
   }
}