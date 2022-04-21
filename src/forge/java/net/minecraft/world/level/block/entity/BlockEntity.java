package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockEntity extends net.minecraftforge.common.capabilities.CapabilityProvider<BlockEntity> implements net.minecraftforge.common.extensions.IForgeBlockEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private final BlockEntityType<?> type;
   @Nullable
   protected Level level;
   protected final BlockPos worldPosition;
   protected boolean remove;
   private BlockState blockState;
   private CompoundTag customTileData;

   public BlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
      super(BlockEntity.class);
      this.type = pType;
      this.worldPosition = pWorldPosition.immutable();
      this.blockState = pBlockState;
      this.gatherCapabilities();
   }

   @Nullable
   public Level getLevel() {
      return this.level;
   }

   public void setLevel(Level pLevel) {
      this.level = pLevel;
   }

   /**
    * @return whether this BlockEntity's level has been set
    */
   public boolean hasLevel() {
      return this.level != null;
   }

   public void load(CompoundTag pTag) {
      if (pTag.contains("ForgeData")) this.customTileData = pTag.getCompound("ForgeData");
      if (getCapabilities() != null && pTag.contains("ForgeCaps")) deserializeCaps(pTag.getCompound("ForgeCaps"));
   }

   public CompoundTag save(CompoundTag pTag) {
      return this.saveMetadata(pTag);
   }

   private CompoundTag saveMetadata(CompoundTag pTag) {
      ResourceLocation resourcelocation = BlockEntityType.getKey(this.getType());
      if (resourcelocation == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         pTag.putString("id", resourcelocation.toString());
         pTag.putInt("x", this.worldPosition.getX());
         pTag.putInt("y", this.worldPosition.getY());
         pTag.putInt("z", this.worldPosition.getZ());
         if (this.customTileData != null) pTag.put("ForgeData", this.customTileData.copy());
         if (getCapabilities() != null) pTag.put("ForgeCaps", serializeCaps());
         return pTag;
      }
   }

   @Nullable
   public static BlockEntity loadStatic(BlockPos pPos, BlockState pState, CompoundTag pTag) {
      String s = pTag.getString("id");
      ResourceLocation resourcelocation = ResourceLocation.tryParse(s);
      if (resourcelocation == null) {
         LOGGER.error("Block entity has invalid type: {}", (Object)s);
         return null;
      } else {
         return Registry.BLOCK_ENTITY_TYPE.getOptional(resourcelocation).map((p_155240_) -> {
            try {
               return p_155240_.create(pPos, pState);
            } catch (Throwable throwable) {
               LOGGER.error("Failed to create block entity {}", s, throwable);
               return null;
            }
         }).map((p_155249_) -> {
            try {
               p_155249_.load(pTag);
               return p_155249_;
            } catch (Throwable throwable) {
               LOGGER.error("Failed to load data for block entity {}", s, throwable);
               return null;
            }
         }).orElseGet(() -> {
            LOGGER.warn("Skipping BlockEntity with id {}", (Object)s);
            return null;
         });
      }
   }

   /**
    * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
    * hasn't changed and skip it.
    */
   public void setChanged() {
      if (this.level != null) {
         setChanged(this.level, this.worldPosition, this.blockState);
      }

   }

   protected static void setChanged(Level pLevel, BlockPos pPos, BlockState pState) {
      pLevel.blockEntityChanged(pPos);
      if (!pState.isAir()) {
         pLevel.updateNeighbourForOutputSignal(pPos, pState.getBlock());
      }

   }

   public BlockPos getBlockPos() {
      return this.worldPosition;
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   /**
    * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
    * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
    */
   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return null;
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public CompoundTag getUpdateTag() {
      return this.saveMetadata(new CompoundTag());
   }

   public boolean isRemoved() {
      return this.remove;
   }

   /**
    * Marks this {@code BlockEntity} as no longer valid (removed from the level).
    */
   public void setRemoved() {
      this.remove = true;
      this.invalidateCaps();
      requestModelDataUpdate();
   }

   @Override
   public void onChunkUnloaded() {
      this.invalidateCaps();
   }

   /**
    * Marks this {@code BlockEntity} as valid again (no longer removed from the level).
    */
   public void clearRemoved() {
      this.remove = false;
   }

   public boolean triggerEvent(int pId, int pType) {
      return false;
   }

   public void fillCrashReportCategory(CrashReportCategory pReportCategory) {
      pReportCategory.setDetail("Name", () -> {
         return Registry.BLOCK_ENTITY_TYPE.getKey(this.getType()) + " // " + this.getClass().getCanonicalName();
      });
      if (this.level != null) {
         CrashReportCategory.populateBlockDetails(pReportCategory, this.level, this.worldPosition, this.getBlockState());
         CrashReportCategory.populateBlockDetails(pReportCategory, this.level, this.worldPosition, this.level.getBlockState(this.worldPosition));
      }
   }

   public boolean onlyOpCanSetNbt() {
      return false;
   }

   public BlockEntityType<?> getType() {
      return this.type;
   }

   @Override
   public CompoundTag getTileData() {
      if (this.customTileData == null)
         this.customTileData = new CompoundTag();
      return this.customTileData;
   }

   @Deprecated
   public void setBlockState(BlockState pBlockState) {
      this.blockState = pBlockState;
   }
}
