package net.minecraft.server.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.util.SortedArraySet;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class DistanceManager {
   static final Logger LOGGER = LogManager.getLogger();
   private static final int ENTITY_TICKING_RANGE = 2;
   static final int PLAYER_TICKET_LEVEL = 33 + ChunkStatus.getDistance(ChunkStatus.FULL) - 2;
   private static final int INITIAL_TICKET_LIST_CAPACITY = 4;
   final Long2ObjectMap<ObjectSet<ServerPlayer>> playersPerChunk = new Long2ObjectOpenHashMap<>();
   final Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> tickets = new Long2ObjectOpenHashMap<>();
   private final DistanceManager.ChunkTicketTracker ticketTracker = new DistanceManager.ChunkTicketTracker();
   private final DistanceManager.FixedPlayerDistanceChunkTracker naturalSpawnChunkCounter = new DistanceManager.FixedPlayerDistanceChunkTracker(8);
   private final DistanceManager.PlayerTicketTracker playerTicketManager = new DistanceManager.PlayerTicketTracker(33);
   final Set<ChunkHolder> chunksToUpdateFutures = Sets.newHashSet();
   final ChunkTaskPriorityQueueSorter ticketThrottler;
   final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> ticketThrottlerInput;
   final ProcessorHandle<ChunkTaskPriorityQueueSorter.Release> ticketThrottlerReleaser;
   final LongSet ticketsToRelease = new LongOpenHashSet();
   final Executor mainThreadExecutor;
   private long ticketTickCounter;

   private final Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> forcedTickets = new Long2ObjectOpenHashMap<>();

   protected DistanceManager(Executor p_140774_, Executor p_140775_) {
      ProcessorHandle<Runnable> processorhandle = ProcessorHandle.of("player ticket throttler", p_140775_::execute);
      ChunkTaskPriorityQueueSorter chunktaskpriorityqueuesorter = new ChunkTaskPriorityQueueSorter(ImmutableList.of(processorhandle), p_140774_, 4);
      this.ticketThrottler = chunktaskpriorityqueuesorter;
      this.ticketThrottlerInput = chunktaskpriorityqueuesorter.getProcessor(processorhandle, true);
      this.ticketThrottlerReleaser = chunktaskpriorityqueuesorter.getReleaseProcessor(processorhandle);
      this.mainThreadExecutor = p_140775_;
   }

   protected void purgeStaleTickets() {
      ++this.ticketTickCounter;
      ObjectIterator<Entry<SortedArraySet<Ticket<?>>>> objectiterator = this.tickets.long2ObjectEntrySet().fastIterator();

      while(objectiterator.hasNext()) {
         Entry<SortedArraySet<Ticket<?>>> entry = objectiterator.next();
         if (entry.getValue().removeIf((p_140822_) -> {
            return p_140822_.timedOut(this.ticketTickCounter);
         })) {
            this.ticketTracker.update(entry.getLongKey(), getTicketLevelAt(entry.getValue()), false);
         }

         if (entry.getValue().isEmpty()) {
            objectiterator.remove();
         }
      }

   }

   private static int getTicketLevelAt(SortedArraySet<Ticket<?>> p_140798_) {
      return !p_140798_.isEmpty() ? p_140798_.first().getTicketLevel() : ChunkMap.MAX_CHUNK_DISTANCE + 1;
   }

   protected abstract boolean isChunkToRemove(long p_140779_);

   @Nullable
   protected abstract ChunkHolder getChunk(long pChunkPos);

   @Nullable
   protected abstract ChunkHolder updateChunkScheduling(long pChunkPos, int pNewLevel, @Nullable ChunkHolder pHolder, int pOldLevel);

   public boolean runAllUpdates(ChunkMap pChunkManager) {
      this.naturalSpawnChunkCounter.runAllUpdates();
      this.playerTicketManager.runAllUpdates();
      int i = Integer.MAX_VALUE - this.ticketTracker.runDistanceUpdates(Integer.MAX_VALUE);
      boolean flag = i != 0;
      if (flag) {
      }

      if (!this.chunksToUpdateFutures.isEmpty()) {
         this.chunksToUpdateFutures.forEach((p_140809_) -> {
            p_140809_.updateFutures(pChunkManager, this.mainThreadExecutor);
         });
         this.chunksToUpdateFutures.clear();
         return true;
      } else {
         if (!this.ticketsToRelease.isEmpty()) {
            LongIterator longiterator = this.ticketsToRelease.iterator();

            while(longiterator.hasNext()) {
               long j = longiterator.nextLong();
               if (this.getTickets(j).stream().anyMatch((p_140791_) -> {
                  return p_140791_.getType() == TicketType.PLAYER;
               })) {
                  ChunkHolder chunkholder = pChunkManager.getUpdatingChunkIfPresent(j);
                  if (chunkholder == null) {
                     throw new IllegalStateException();
                  }

                  CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> completablefuture = chunkholder.getEntityTickingChunkFuture();
                  completablefuture.thenAccept((p_140789_) -> {
                     this.mainThreadExecutor.execute(() -> {
                        this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {
                        }, j, false));
                     });
                  });
               }
            }

            this.ticketsToRelease.clear();
         }

         return flag;
      }
   }

   void addTicket(long pChunkPos, Ticket<?> pTicket) {
      SortedArraySet<Ticket<?>> sortedarrayset = this.getTickets(pChunkPos);
      int i = getTicketLevelAt(sortedarrayset);
      Ticket<?> ticket = sortedarrayset.addOrGet(pTicket);
      ticket.setCreatedTick(this.ticketTickCounter);
      if (pTicket.getTicketLevel() < i) {
         this.ticketTracker.update(pChunkPos, pTicket.getTicketLevel(), true);
      }

      if (pTicket.isForceTicks()) {
          SortedArraySet<Ticket<?>> tickets = forcedTickets.computeIfAbsent(pChunkPos, e -> SortedArraySet.create(4));
          tickets.addOrGet(ticket);
      }
   }

   void removeTicket(long pChunkPos, Ticket<?> pTicket) {
      SortedArraySet<Ticket<?>> sortedarrayset = this.getTickets(pChunkPos);
      if (sortedarrayset.remove(pTicket)) {
      }

      if (sortedarrayset.isEmpty()) {
         this.tickets.remove(pChunkPos);
      }

      this.ticketTracker.update(pChunkPos, getTicketLevelAt(sortedarrayset), false);

      if (pTicket.isForceTicks()) {
          SortedArraySet<Ticket<?>> tickets = forcedTickets.get(pChunkPos);
          if (tickets != null) {
              tickets.remove(pTicket);
          }
      }
   }

   public <T> void addTicket(TicketType<T> pType, ChunkPos pPos, int pLevel, T pValue) {
      this.addTicket(pPos.toLong(), new Ticket<>(pType, pLevel, pValue));
   }

   public <T> void removeTicket(TicketType<T> pType, ChunkPos pPos, int pLevel, T pValue) {
      Ticket<T> ticket = new Ticket<>(pType, pLevel, pValue);
      this.removeTicket(pPos.toLong(), ticket);
   }

   public <T> void addRegionTicket(TicketType<T> pType, ChunkPos pPos, int pDistance, T pValue) {
      this.addTicket(pPos.toLong(), new Ticket<>(pType, 33 - pDistance, pValue));
   }

   public <T> void removeRegionTicket(TicketType<T> pType, ChunkPos pPos, int pDistance, T pValue) {
      Ticket<T> ticket = new Ticket<>(pType, 33 - pDistance, pValue);
      this.removeTicket(pPos.toLong(), ticket);
   }

   public <T> void registerTicking(TicketType<T> type, ChunkPos pos, int distance, T value) {
      this.addTicket(pos.toLong(), new Ticket<>(type, 33 - distance, value, true));
   }

   public <T> void releaseTicking(TicketType<T> type, ChunkPos pos, int distance, T value) {
      this.removeTicket(pos.toLong(), new Ticket<>(type, 33 - distance, value, true));
   }

   private SortedArraySet<Ticket<?>> getTickets(long p_140858_) {
      return this.tickets.computeIfAbsent(p_140858_, (p_140867_) -> {
         return SortedArraySet.create(4);
      });
   }

   protected void updateChunkForced(ChunkPos pPos, boolean pAdd) {
      Ticket<ChunkPos> ticket = new Ticket<>(TicketType.FORCED, 31, pPos);
      if (pAdd) {
         this.addTicket(pPos.toLong(), ticket);
      } else {
         this.removeTicket(pPos.toLong(), ticket);
      }

   }

   public void addPlayer(SectionPos pSectionPos, ServerPlayer pPlayer) {
      long i = pSectionPos.chunk().toLong();
      this.playersPerChunk.computeIfAbsent(i, (p_140863_) -> {
         return new ObjectOpenHashSet();
      }).add(pPlayer);
      this.naturalSpawnChunkCounter.update(i, 0, true);
      this.playerTicketManager.update(i, 0, true);
   }

   public void removePlayer(SectionPos pSectionPos, ServerPlayer pPlayer) {
      long i = pSectionPos.chunk().toLong();
      ObjectSet<ServerPlayer> objectset = this.playersPerChunk.get(i);
      objectset.remove(pPlayer);
      if (objectset.isEmpty()) {
         this.playersPerChunk.remove(i);
         this.naturalSpawnChunkCounter.update(i, Integer.MAX_VALUE, false);
         this.playerTicketManager.update(i, Integer.MAX_VALUE, false);
      }

   }

   protected String getTicketDebugString(long p_140839_) {
      SortedArraySet<Ticket<?>> sortedarrayset = this.tickets.get(p_140839_);
      String s;
      if (sortedarrayset != null && !sortedarrayset.isEmpty()) {
         s = sortedarrayset.first().toString();
      } else {
         s = "no_ticket";
      }

      return s;
   }

   protected void updatePlayerTickets(int pViewDistance) {
      this.playerTicketManager.updateViewDistance(pViewDistance);
   }

   /**
    * Returns the number of chunks taken into account when calculating the mob cap
    */
   public int getNaturalSpawnChunkCount() {
      this.naturalSpawnChunkCounter.runAllUpdates();
      return this.naturalSpawnChunkCounter.chunks.size();
   }

   public boolean hasPlayersNearby(long pChunkPos) {
      this.naturalSpawnChunkCounter.runAllUpdates();
      return this.naturalSpawnChunkCounter.chunks.containsKey(pChunkPos);
   }

   public String getDebugStatus() {
      return this.ticketThrottler.getDebugStatus();
   }

   public boolean shouldForceTicks(long chunkPos) {
       SortedArraySet<Ticket<?>> tickets = forcedTickets.get(chunkPos);
       return tickets != null && !tickets.isEmpty();
   }

   private void dumpTickets(String p_143208_) {
      try {
         FileOutputStream fileoutputstream = new FileOutputStream(new File(p_143208_));

         try {
            for(Entry<SortedArraySet<Ticket<?>>> entry : this.tickets.long2ObjectEntrySet()) {
               ChunkPos chunkpos = new ChunkPos(entry.getLongKey());

               for(Ticket<?> ticket : entry.getValue()) {
                  fileoutputstream.write((chunkpos.x + "\t" + chunkpos.z + "\t" + ticket.getType() + "\t" + ticket.getTicketLevel() + "\t\n").getBytes(StandardCharsets.UTF_8));
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
         LOGGER.error(ioexception);
      }

   }

   class ChunkTicketTracker extends ChunkTracker {
      public ChunkTicketTracker() {
         super(ChunkMap.MAX_CHUNK_DISTANCE + 2, 16, 256);
      }

      protected int getLevelFromSource(long pPos) {
         SortedArraySet<Ticket<?>> sortedarrayset = DistanceManager.this.tickets.get(pPos);
         if (sortedarrayset == null) {
            return Integer.MAX_VALUE;
         } else {
            return sortedarrayset.isEmpty() ? Integer.MAX_VALUE : sortedarrayset.first().getTicketLevel();
         }
      }

      protected int getLevel(long pSectionPos) {
         if (!DistanceManager.this.isChunkToRemove(pSectionPos)) {
            ChunkHolder chunkholder = DistanceManager.this.getChunk(pSectionPos);
            if (chunkholder != null) {
               return chunkholder.getTicketLevel();
            }
         }

         return ChunkMap.MAX_CHUNK_DISTANCE + 1;
      }

      protected void setLevel(long pSectionPos, int pLevel) {
         ChunkHolder chunkholder = DistanceManager.this.getChunk(pSectionPos);
         int i = chunkholder == null ? ChunkMap.MAX_CHUNK_DISTANCE + 1 : chunkholder.getTicketLevel();
         if (i != pLevel) {
            chunkholder = DistanceManager.this.updateChunkScheduling(pSectionPos, pLevel, chunkholder, i);
            if (chunkholder != null) {
               DistanceManager.this.chunksToUpdateFutures.add(chunkholder);
            }

         }
      }

      public int runDistanceUpdates(int p_140878_) {
         return this.runUpdates(p_140878_);
      }
   }

   class FixedPlayerDistanceChunkTracker extends ChunkTracker {
      /** Chunks that are at most {@link #range} chunks away from the closest player. */
      protected final Long2ByteMap chunks = new Long2ByteOpenHashMap();
      protected final int maxDistance;

      protected FixedPlayerDistanceChunkTracker(int p_140891_) {
         super(p_140891_ + 2, 16, 256);
         this.maxDistance = p_140891_;
         this.chunks.defaultReturnValue((byte)(p_140891_ + 2));
      }

      protected int getLevel(long pSectionPos) {
         return this.chunks.get(pSectionPos);
      }

      protected void setLevel(long pSectionPos, int pLevel) {
         byte b0;
         if (pLevel > this.maxDistance) {
            b0 = this.chunks.remove(pSectionPos);
         } else {
            b0 = this.chunks.put(pSectionPos, (byte)pLevel);
         }

         this.onLevelChange(pSectionPos, b0, pLevel);
      }

      /**
       * Called after {@link PlayerChunkTracker#setLevel(long, int)} puts/removes chunk into/from {@link
       * #chunksInRange}.
       * @param pOldLevel Previous level of the chunk if it was smaller than {@link #range}, {@code range + 2}
       * otherwise.
       */
      protected void onLevelChange(long pChunkPos, int pOldLevel, int pNewLevel) {
      }

      protected int getLevelFromSource(long pPos) {
         return this.havePlayer(pPos) ? 0 : Integer.MAX_VALUE;
      }

      private boolean havePlayer(long pChunkPos) {
         ObjectSet<ServerPlayer> objectset = DistanceManager.this.playersPerChunk.get(pChunkPos);
         return objectset != null && !objectset.isEmpty();
      }

      public void runAllUpdates() {
         this.runUpdates(Integer.MAX_VALUE);
      }

      private void dumpChunks(String p_143213_) {
         try {
            FileOutputStream fileoutputstream = new FileOutputStream(new File(p_143213_));

            try {
               for(it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry entry : this.chunks.long2ByteEntrySet()) {
                  ChunkPos chunkpos = new ChunkPos(entry.getLongKey());
                  String s = Byte.toString(entry.getByteValue());
                  fileoutputstream.write((chunkpos.x + "\t" + chunkpos.z + "\t" + s + "\n").getBytes(StandardCharsets.UTF_8));
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
            DistanceManager.LOGGER.error(ioexception);
         }

      }
   }

   class PlayerTicketTracker extends DistanceManager.FixedPlayerDistanceChunkTracker {
      private int viewDistance;
      private final Long2IntMap queueLevels = Long2IntMaps.synchronize(new Long2IntOpenHashMap());
      private final LongSet toUpdate = new LongOpenHashSet();

      protected PlayerTicketTracker(int p_140910_) {
         super(p_140910_);
         this.viewDistance = 0;
         this.queueLevels.defaultReturnValue(p_140910_ + 2);
      }

      /**
       * Called after {@link PlayerChunkTracker#setLevel(long, int)} puts/removes chunk into/from {@link
       * #chunksInRange}.
       * @param pOldLevel Previous level of the chunk if it was smaller than {@link #range}, {@code range + 2}
       * otherwise.
       */
      protected void onLevelChange(long pChunkPos, int pOldLevel, int pNewLevel) {
         this.toUpdate.add(pChunkPos);
      }

      public void updateViewDistance(int pViewDistance) {
         for(it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry entry : this.chunks.long2ByteEntrySet()) {
            byte b0 = entry.getByteValue();
            long i = entry.getLongKey();
            this.onLevelChange(i, b0, this.haveTicketFor(b0), b0 <= pViewDistance - 2);
         }

         this.viewDistance = pViewDistance;
      }

      private void onLevelChange(long p_140919_, int p_140920_, boolean p_140921_, boolean p_140922_) {
         if (p_140921_ != p_140922_) {
            Ticket<?> ticket = new Ticket<>(TicketType.PLAYER, DistanceManager.PLAYER_TICKET_LEVEL, new ChunkPos(p_140919_));
            if (p_140922_) {
               DistanceManager.this.ticketThrottlerInput.tell(ChunkTaskPriorityQueueSorter.message(() -> {
                  DistanceManager.this.mainThreadExecutor.execute(() -> {
                     if (this.haveTicketFor(this.getLevel(p_140919_))) {
                        DistanceManager.this.addTicket(p_140919_, ticket);
                        DistanceManager.this.ticketsToRelease.add(p_140919_);
                     } else {
                        DistanceManager.this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {
                        }, p_140919_, false));
                     }

                  });
               }, p_140919_, () -> {
                  return p_140920_;
               }));
            } else {
               DistanceManager.this.ticketThrottlerReleaser.tell(ChunkTaskPriorityQueueSorter.release(() -> {
                  DistanceManager.this.mainThreadExecutor.execute(() -> {
                     DistanceManager.this.removeTicket(p_140919_, ticket);
                  });
               }, p_140919_, true));
            }
         }

      }

      public void runAllUpdates() {
         super.runAllUpdates();
         if (!this.toUpdate.isEmpty()) {
            LongIterator longiterator = this.toUpdate.iterator();

            while(longiterator.hasNext()) {
               long i = longiterator.nextLong();
               int j = this.queueLevels.get(i);
               int k = this.getLevel(i);
               if (j != k) {
                  DistanceManager.this.ticketThrottler.onLevelChange(new ChunkPos(i), () -> {
                     return this.queueLevels.get(i);
                  }, k, (p_140928_) -> {
                     if (p_140928_ >= this.queueLevels.defaultReturnValue()) {
                        this.queueLevels.remove(i);
                     } else {
                        this.queueLevels.put(i, p_140928_);
                     }

                  });
                  this.onLevelChange(i, k, this.haveTicketFor(j), this.haveTicketFor(k));
               }
            }

            this.toUpdate.clear();
         }

      }

      private boolean haveTicketFor(int p_140933_) {
         return p_140933_ <= this.viewDistance - 2;
      }
   }
}
