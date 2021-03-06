package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerBlockEntity extends BlockEntity {
   private final BaseSpawner spawner = new BaseSpawner() {
      public void broadcastEvent(Level p_155767_, BlockPos p_155768_, int p_155769_) {
         p_155767_.blockEvent(p_155768_, Blocks.SPAWNER, p_155769_, 0);
      }

      public void setNextSpawnData(@Nullable Level p_155771_, BlockPos p_155772_, SpawnData p_155773_) {
         super.setNextSpawnData(p_155771_, p_155772_, p_155773_);
         if (p_155771_ != null) {
            BlockState blockstate = p_155771_.getBlockState(p_155772_);
            p_155771_.sendBlockUpdated(p_155772_, blockstate, blockstate, 4);
         }

      }

      @javax.annotation.Nullable
       public net.minecraft.world.level.block.entity.BlockEntity getSpawnerBlockEntity(){ return SpawnerBlockEntity.this; }
   };

   public SpawnerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(BlockEntityType.MOB_SPAWNER, pWorldPosition, pBlockState);
   }

   public void load(CompoundTag pTag) {
      super.load(pTag);
      this.spawner.load(this.level, this.worldPosition, pTag);
   }

   public CompoundTag save(CompoundTag pCompound) {
      super.save(pCompound);
      this.spawner.save(this.level, this.worldPosition, pCompound);
      return pCompound;
   }

   public static void clientTick(Level pLevel, BlockPos pPos, BlockState pState, SpawnerBlockEntity pBlockEntity) {
      pBlockEntity.spawner.clientTick(pLevel, pPos);
   }

   public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, SpawnerBlockEntity pBlockEntity) {
      pBlockEntity.spawner.serverTick((ServerLevel)pLevel, pPos);
   }

   /**
    * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
    * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
    */
   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 1, this.getUpdateTag());
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public CompoundTag getUpdateTag() {
      CompoundTag compoundtag = this.save(new CompoundTag());
      compoundtag.remove("SpawnPotentials");
      return compoundtag;
   }

   public boolean triggerEvent(int pId, int pType) {
      return this.spawner.onEventTriggered(this.level, pId) ? true : super.triggerEvent(pId, pType);
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public BaseSpawner getSpawner() {
      return this.spawner;
   }
}
