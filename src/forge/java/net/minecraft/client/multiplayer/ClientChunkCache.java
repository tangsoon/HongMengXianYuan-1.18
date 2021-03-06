package net.minecraft.client.multiplayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientChunkCache extends ChunkSource {
   static final Logger LOGGER = LogManager.getLogger();
   private final LevelChunk emptyChunk;
   private final LevelLightEngine lightEngine;
   volatile ClientChunkCache.Storage storage;
   final ClientLevel level;

   public ClientChunkCache(ClientLevel pLevel, int pViewDistance) {
      this.level = pLevel;
      this.emptyChunk = new EmptyLevelChunk(pLevel, new ChunkPos(0, 0));
      this.lightEngine = new LevelLightEngine(this, true, pLevel.dimensionType().hasSkyLight());
      this.storage = new ClientChunkCache.Storage(calculateStorageRange(pViewDistance));
   }

   public LevelLightEngine getLightEngine() {
      return this.lightEngine;
   }

   private static boolean isValidChunk(@Nullable LevelChunk pChunk, int pX, int pZ) {
      if (pChunk == null) {
         return false;
      } else {
         ChunkPos chunkpos = pChunk.getPos();
         return chunkpos.x == pX && chunkpos.z == pZ;
      }
   }

   /**
    * Unload chunk from ChunkProviderClient's hashmap. Called in response to a Packet50PreChunk with its mode field set
    * to false
    */
   public void drop(int pX, int pZ) {
      if (this.storage.inRange(pX, pZ)) {
         int i = this.storage.getIndex(pX, pZ);
         LevelChunk levelchunk = this.storage.getChunk(i);
         if (isValidChunk(levelchunk, pX, pZ)) {
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Unload(levelchunk));
            this.storage.replace(i, levelchunk, (LevelChunk)null);
         }

      }
   }

   /**
    * Gets the chunk at the provided position, if it exists.
    * Note: This method <strong>can deadlock</strong> when called from within an existing chunk load, as it will be
    * stuck waiting for the current chunk to load!
    * @param pLoad If this should force a chunk load. When {@code false}, this will return null if the chunk is not
    * loaded.
    */
   @Nullable
   public LevelChunk getChunk(int pChunkX, int pChunkZ, ChunkStatus pRequiredStatus, boolean pLoad) {
      if (this.storage.inRange(pChunkX, pChunkZ)) {
         LevelChunk levelchunk = this.storage.getChunk(this.storage.getIndex(pChunkX, pChunkZ));
         if (isValidChunk(levelchunk, pChunkX, pChunkZ)) {
            return levelchunk;
         }
      }

      return pLoad ? this.emptyChunk : null;
   }

   public BlockGetter getLevel() {
      return this.level;
   }

   @Nullable
   public LevelChunk replaceWithPacketData(int p_171616_, int p_171617_, ChunkBiomeContainer p_171618_, FriendlyByteBuf p_171619_, CompoundTag p_171620_, BitSet p_171621_) {
      if (!this.storage.inRange(p_171616_, p_171617_)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", p_171616_, p_171617_);
         return null;
      } else {
         int i = this.storage.getIndex(p_171616_, p_171617_);
         LevelChunk levelchunk = this.storage.chunks.get(i);
         ChunkPos chunkpos = new ChunkPos(p_171616_, p_171617_);
         if (!isValidChunk(levelchunk, p_171616_, p_171617_)) {
            levelchunk = new LevelChunk(this.level, chunkpos, p_171618_);
            levelchunk.replaceWithPacketData(p_171618_, p_171619_, p_171620_, p_171621_);
            this.storage.replace(i, levelchunk);
         } else {
            levelchunk.replaceWithPacketData(p_171618_, p_171619_, p_171620_, p_171621_);
         }

         LevelChunkSection[] alevelchunksection = levelchunk.getSections();
         LevelLightEngine levellightengine = this.getLightEngine();
         levellightengine.enableLightSources(chunkpos, true);

         for(int j = 0; j < alevelchunksection.length; ++j) {
            LevelChunkSection levelchunksection = alevelchunksection[j];
            int k = this.level.getSectionYFromSectionIndex(j);
            levellightengine.updateSectionStatus(SectionPos.of(p_171616_, k, p_171617_), LevelChunkSection.isEmpty(levelchunksection));
         }

         this.level.onChunkLoaded(chunkpos);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(levelchunk));
         return levelchunk;
      }
   }

   public void tick(BooleanSupplier pHasTimeLeft) {
   }

   public void updateViewCenter(int pX, int pZ) {
      this.storage.viewCenterX = pX;
      this.storage.viewCenterZ = pZ;
   }

   public void updateViewRadius(int pViewDistance) {
      int i = this.storage.chunkRadius;
      int j = calculateStorageRange(pViewDistance);
      if (i != j) {
         ClientChunkCache.Storage clientchunkcache$storage = new ClientChunkCache.Storage(j);
         clientchunkcache$storage.viewCenterX = this.storage.viewCenterX;
         clientchunkcache$storage.viewCenterZ = this.storage.viewCenterZ;

         for(int k = 0; k < this.storage.chunks.length(); ++k) {
            LevelChunk levelchunk = this.storage.chunks.get(k);
            if (levelchunk != null) {
               ChunkPos chunkpos = levelchunk.getPos();
               if (clientchunkcache$storage.inRange(chunkpos.x, chunkpos.z)) {
                  clientchunkcache$storage.replace(clientchunkcache$storage.getIndex(chunkpos.x, chunkpos.z), levelchunk);
               }
            }
         }

         this.storage = clientchunkcache$storage;
      }

   }

   private static int calculateStorageRange(int pViewDistance) {
      return Math.max(2, pViewDistance) + 3;
   }

   /**
    * Converts the instance data to a readable string.
    */
   public String gatherStats() {
      return this.storage.chunks.length() + ", " + this.getLoadedChunksCount();
   }

   public int getLoadedChunksCount() {
      return this.storage.chunkCount;
   }

   public void onLightUpdate(LightLayer pType, SectionPos pPos) {
      Minecraft.getInstance().levelRenderer.setSectionDirty(pPos.x(), pPos.y(), pPos.z());
   }

   @OnlyIn(Dist.CLIENT)
   final class Storage {
      final AtomicReferenceArray<LevelChunk> chunks;
      final int chunkRadius;
      private final int viewRange;
      volatile int viewCenterX;
      volatile int viewCenterZ;
      int chunkCount;

      Storage(int p_104474_) {
         this.chunkRadius = p_104474_;
         this.viewRange = p_104474_ * 2 + 1;
         this.chunks = new AtomicReferenceArray<>(this.viewRange * this.viewRange);
      }

      int getIndex(int pX, int pZ) {
         return Math.floorMod(pZ, this.viewRange) * this.viewRange + Math.floorMod(pX, this.viewRange);
      }

      protected void replace(int pChunkIndex, @Nullable LevelChunk pChunk) {
         LevelChunk levelchunk = this.chunks.getAndSet(pChunkIndex, pChunk);
         if (levelchunk != null) {
            --this.chunkCount;
            ClientChunkCache.this.level.unload(levelchunk);
         }

         if (pChunk != null) {
            ++this.chunkCount;
         }

      }

      protected LevelChunk replace(int pChunkIndex, LevelChunk pChunk, @Nullable LevelChunk pReplaceWith) {
         if (this.chunks.compareAndSet(pChunkIndex, pChunk, pReplaceWith) && pReplaceWith == null) {
            --this.chunkCount;
         }

         ClientChunkCache.this.level.unload(pChunk);
         return pChunk;
      }

      boolean inRange(int pX, int pZ) {
         return Math.abs(pX - this.viewCenterX) <= this.chunkRadius && Math.abs(pZ - this.viewCenterZ) <= this.chunkRadius;
      }

      @Nullable
      protected LevelChunk getChunk(int pChunkIndex) {
         return this.chunks.get(pChunkIndex);
      }

      private void dumpChunks(String p_171623_) {
         try {
            FileOutputStream fileoutputstream = new FileOutputStream(new File(p_171623_));

            try {
               int i = ClientChunkCache.this.storage.chunkRadius;

               for(int j = this.viewCenterZ - i; j <= this.viewCenterZ + i; ++j) {
                  for(int k = this.viewCenterX - i; k <= this.viewCenterX + i; ++k) {
                     LevelChunk levelchunk = ClientChunkCache.this.storage.chunks.get(ClientChunkCache.this.storage.getIndex(k, j));
                     if (levelchunk != null) {
                        ChunkPos chunkpos = levelchunk.getPos();
                        fileoutputstream.write((chunkpos.x + "\t" + chunkpos.z + "\t" + levelchunk.isEmpty() + "\n").getBytes(StandardCharsets.UTF_8));
                     }
                  }
               }
            } catch (Throwable throwable1) {
               try {
                  fileoutputstream.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }

               throw throwable1;
            }

            fileoutputstream.close();
         } catch (IOException ioexception) {
            ClientChunkCache.LOGGER.error(ioexception);
         }

      }
   }
}
