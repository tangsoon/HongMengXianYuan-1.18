package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class ClientboundLoginPacket implements Packet<ClientGamePacketListener> {
   private static final int HARDCORE_FLAG = 8;
   private final int playerId;
   /** First 8 bytes of the SHA-256 hash of the world's seed */
   private final long seed;
   private final boolean hardcore;
   private final GameType gameType;
   @Nullable
   private final GameType previousGameType;
   private final Set<ResourceKey<Level>> levels;
   private final RegistryAccess.RegistryHolder registryHolder;
   private final DimensionType dimensionType;
   private final ResourceKey<Level> dimension;
   private final int maxPlayers;
   private final int chunkRadius;
   private final boolean reducedDebugInfo;
   /** Set to false when the doImmediateRespawn gamerule is true */
   private final boolean showDeathScreen;
   private final boolean isDebug;
   private final boolean isFlat;

   public ClientboundLoginPacket(int pPlayerId, GameType pGameType, @Nullable GameType pPreviousGameType, long pSeed, boolean pHardcore, Set<ResourceKey<Level>> pLevels, RegistryAccess.RegistryHolder pRegistryHolder, DimensionType pDimensionType, ResourceKey<Level> pDimension, int pMaxPlayers, int pChunkRadius, boolean pReducedDebugInfo, boolean pShowDeathScreen, boolean pIsDebug, boolean pIsFlat) {
      this.playerId = pPlayerId;
      this.levels = pLevels;
      this.registryHolder = pRegistryHolder;
      this.dimensionType = pDimensionType;
      this.dimension = pDimension;
      this.seed = pSeed;
      this.gameType = pGameType;
      this.previousGameType = pPreviousGameType;
      this.maxPlayers = pMaxPlayers;
      this.hardcore = pHardcore;
      this.chunkRadius = pChunkRadius;
      this.reducedDebugInfo = pReducedDebugInfo;
      this.showDeathScreen = pShowDeathScreen;
      this.isDebug = pIsDebug;
      this.isFlat = pIsFlat;
   }

   public ClientboundLoginPacket(FriendlyByteBuf pBuffer) {
      this.playerId = pBuffer.readInt();
      this.hardcore = pBuffer.readBoolean();
      this.gameType = GameType.byId(pBuffer.readByte());
      this.previousGameType = GameType.byNullableId(pBuffer.readByte());
      this.levels = pBuffer.readCollection(Sets::newHashSetWithExpectedSize, (p_178965_) -> {
         return ResourceKey.create(Registry.DIMENSION_REGISTRY, p_178965_.readResourceLocation());
      });
      this.registryHolder = pBuffer.readWithCodec(RegistryAccess.RegistryHolder.NETWORK_CODEC);
      this.dimensionType = pBuffer.readWithCodec(DimensionType.CODEC).get();
      this.dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, pBuffer.readResourceLocation());
      this.seed = pBuffer.readLong();
      this.maxPlayers = pBuffer.readVarInt();
      this.chunkRadius = pBuffer.readVarInt();
      this.reducedDebugInfo = pBuffer.readBoolean();
      this.showDeathScreen = pBuffer.readBoolean();
      this.isDebug = pBuffer.readBoolean();
      this.isFlat = pBuffer.readBoolean();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void write(FriendlyByteBuf pBuffer) {
      pBuffer.writeInt(this.playerId);
      pBuffer.writeBoolean(this.hardcore);
      pBuffer.writeByte(this.gameType.getId());
      pBuffer.writeByte(GameType.getNullableId(this.previousGameType));
      pBuffer.writeCollection(this.levels, (p_178962_, p_178963_) -> {
         p_178962_.writeResourceLocation(p_178963_.location());
      });
      pBuffer.writeWithCodec(RegistryAccess.RegistryHolder.NETWORK_CODEC, this.registryHolder);
      pBuffer.writeWithCodec(DimensionType.CODEC, () -> {
         return this.dimensionType;
      });
      pBuffer.writeResourceLocation(this.dimension.location());
      pBuffer.writeLong(this.seed);
      pBuffer.writeVarInt(this.maxPlayers);
      pBuffer.writeVarInt(this.chunkRadius);
      pBuffer.writeBoolean(this.reducedDebugInfo);
      pBuffer.writeBoolean(this.showDeathScreen);
      pBuffer.writeBoolean(this.isDebug);
      pBuffer.writeBoolean(this.isFlat);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void handle(ClientGamePacketListener pHandler) {
      pHandler.handleLogin(this);
   }

   public int getPlayerId() {
      return this.playerId;
   }

   /**
    * get value
    */
   public long getSeed() {
      return this.seed;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   @Nullable
   public GameType getPreviousGameType() {
      return this.previousGameType;
   }

   public Set<ResourceKey<Level>> levels() {
      return this.levels;
   }

   public RegistryAccess registryAccess() {
      return this.registryHolder;
   }

   public DimensionType getDimensionType() {
      return this.dimensionType;
   }

   public ResourceKey<Level> getDimension() {
      return this.dimension;
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public int getChunkRadius() {
      return this.chunkRadius;
   }

   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   public boolean shouldShowDeathScreen() {
      return this.showDeathScreen;
   }

   public boolean isDebug() {
      return this.isDebug;
   }

   public boolean isFlat() {
      return this.isFlat;
   }
}