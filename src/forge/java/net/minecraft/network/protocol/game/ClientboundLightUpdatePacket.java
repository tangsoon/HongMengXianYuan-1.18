package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLightUpdatePacket implements Packet<ClientGamePacketListener> {
   private final int x;
   private final int z;
   private final BitSet skyYMask;
   private final BitSet blockYMask;
   private final BitSet emptySkyYMask;
   private final BitSet emptyBlockYMask;
   private final List<byte[]> skyUpdates;
   private final List<byte[]> blockUpdates;
   private final boolean trustEdges;

   public ClientboundLightUpdatePacket(ChunkPos pChunkPos, LevelLightEngine pLightEngine, @Nullable BitSet pChangedSkySections, @Nullable BitSet pChangedBlockSections, boolean pTrustEdges) {
      this.x = pChunkPos.x;
      this.z = pChunkPos.z;
      this.trustEdges = pTrustEdges;
      this.skyYMask = new BitSet();
      this.blockYMask = new BitSet();
      this.emptySkyYMask = new BitSet();
      this.emptyBlockYMask = new BitSet();
      this.skyUpdates = Lists.newArrayList();
      this.blockUpdates = Lists.newArrayList();

      for(int i = 0; i < pLightEngine.getLightSectionCount(); ++i) {
         if (pChangedSkySections == null || pChangedSkySections.get(i)) {
            prepareSectionData(pChunkPos, pLightEngine, LightLayer.SKY, i, this.skyYMask, this.emptySkyYMask, this.skyUpdates);
         }

         if (pChangedBlockSections == null || pChangedBlockSections.get(i)) {
            prepareSectionData(pChunkPos, pLightEngine, LightLayer.BLOCK, i, this.blockYMask, this.emptyBlockYMask, this.blockUpdates);
         }
      }

   }

   private static void prepareSectionData(ChunkPos pChunkPos, LevelLightEngine pLightEngine, LightLayer pLightLayer, int pSectionIndex, BitSet pYMask, BitSet pEmptyYMask, List<byte[]> pUpdateData) {
      DataLayer datalayer = pLightEngine.getLayerListener(pLightLayer).getDataLayerData(SectionPos.of(pChunkPos, pLightEngine.getMinLightSection() + pSectionIndex));
      if (datalayer != null) {
         if (datalayer.isEmpty()) {
            pEmptyYMask.set(pSectionIndex);
         } else {
            pYMask.set(pSectionIndex);
            pUpdateData.add((byte[])datalayer.getData().clone());
         }
      }

   }

   public ClientboundLightUpdatePacket(FriendlyByteBuf pBuffer) {
      this.x = pBuffer.readVarInt();
      this.z = pBuffer.readVarInt();
      this.trustEdges = pBuffer.readBoolean();
      this.skyYMask = pBuffer.readBitSet();
      this.blockYMask = pBuffer.readBitSet();
      this.emptySkyYMask = pBuffer.readBitSet();
      this.emptyBlockYMask = pBuffer.readBitSet();
      this.skyUpdates = pBuffer.readList((p_178930_) -> {
         return p_178930_.readByteArray(2048);
      });
      this.blockUpdates = pBuffer.readList((p_178928_) -> {
         return p_178928_.readByteArray(2048);
      });
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void write(FriendlyByteBuf pBuffer) {
      pBuffer.writeVarInt(this.x);
      pBuffer.writeVarInt(this.z);
      pBuffer.writeBoolean(this.trustEdges);
      pBuffer.writeBitSet(this.skyYMask);
      pBuffer.writeBitSet(this.blockYMask);
      pBuffer.writeBitSet(this.emptySkyYMask);
      pBuffer.writeBitSet(this.emptyBlockYMask);
      pBuffer.writeCollection(this.skyUpdates, FriendlyByteBuf::writeByteArray);
      pBuffer.writeCollection(this.blockUpdates, FriendlyByteBuf::writeByteArray);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void handle(ClientGamePacketListener pHandler) {
      pHandler.handleLightUpdatePacked(this);
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public BitSet getSkyYMask() {
      return this.skyYMask;
   }

   public BitSet getEmptySkyYMask() {
      return this.emptySkyYMask;
   }

   public List<byte[]> getSkyUpdates() {
      return this.skyUpdates;
   }

   public BitSet getBlockYMask() {
      return this.blockYMask;
   }

   public BitSet getEmptyBlockYMask() {
      return this.emptyBlockYMask;
   }

   public List<byte[]> getBlockUpdates() {
      return this.blockUpdates;
   }

   public boolean getTrustEdges() {
      return this.trustEdges;
   }
}