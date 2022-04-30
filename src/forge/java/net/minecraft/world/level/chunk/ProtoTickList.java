package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;

public class ProtoTickList<T> implements TickList<T> {
   protected final Predicate<T> ignore;
   private final ChunkPos chunkPos;
   private final ShortList[] toBeTicked;
   private LevelHeightAccessor levelHeightAccessor;

   public ProtoTickList(Predicate<T> pFilter, ChunkPos pPos, LevelHeightAccessor pLevel) {
      this(pFilter, pPos, new ListTag(), pLevel);
   }

   public ProtoTickList(Predicate<T> pIgnore, ChunkPos pChunkPos, ListTag pTag, LevelHeightAccessor pLevelHeightAccessor) {
      this.ignore = pIgnore;
      this.chunkPos = pChunkPos;
      this.levelHeightAccessor = pLevelHeightAccessor;
      this.toBeTicked = new ShortList[pLevelHeightAccessor.getSectionsCount()];

      for(int i = 0; i < pTag.size(); ++i) {
         ListTag listtag = pTag.getList(i);

         for(int j = 0; j < listtag.size(); ++j) {
            ChunkAccess.getOrCreateOffsetList(this.toBeTicked, i).add(listtag.getShort(j));
         }
      }

   }

   public ListTag save() {
      return ChunkSerializer.packOffsets(this.toBeTicked);
   }

   public void copyOut(TickList<T> pTickList, Function<BlockPos, T> pObjectAccessor) {
      for(int i = 0; i < this.toBeTicked.length; ++i) {
         if (this.toBeTicked[i] != null) {
            for(Short oshort : this.toBeTicked[i]) {
               BlockPos blockpos = ProtoChunk.unpackOffsetCoordinates(oshort, this.levelHeightAccessor.getSectionYFromSectionIndex(i), this.chunkPos);
               pTickList.scheduleTick(blockpos, pObjectAccessor.apply(blockpos), 0);
            }

            this.toBeTicked[i].clear();
         }
      }

   }

   public boolean hasScheduledTick(BlockPos pPos, T pItem) {
      return false;
   }

   public void scheduleTick(BlockPos pPos, T pObject, int pScheduledTime, TickPriority pPriority) {
      int i = this.levelHeightAccessor.getSectionIndex(pPos.getY());
      if (i >= 0 && i < this.levelHeightAccessor.getSectionsCount()) {
         ChunkAccess.getOrCreateOffsetList(this.toBeTicked, i).add(ProtoChunk.packOffsetCoordinates(pPos));
      }
   }

   /**
    * Checks if this position/item is scheduled to be updated this tick
    */
   public boolean willTickThisTick(BlockPos pPos, T pObject) {
      return false;
   }

   public int size() {
      return Stream.of(this.toBeTicked).filter(Objects::nonNull).mapToInt(List::size).sum();
   }
}