package net.minecraft.world.level.chunk;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.BitStorage;
import net.minecraft.util.DebugBuffer;
import net.minecraft.util.Mth;
import net.minecraft.util.ThreadingDetector;

public class PalettedContainer<T> implements PaletteResize<T> {
   private static final int SIZE = 4096;
   public static final int GLOBAL_PALETTE_BITS = 9;
   public static final int MIN_PALETTE_SIZE = 4;
   private final Palette<T> globalPalette;
   private final PaletteResize<T> dummyPaletteResize = (p_63139_, p_63140_) -> {
      return 0;
   };
   private final IdMapper<T> registry;
   private final Function<CompoundTag, T> reader;
   private final Function<T, CompoundTag> writer;
   private final T defaultValue;
   protected BitStorage storage;
   private Palette<T> palette;
   private int bits;
   private final Semaphore lock = new Semaphore(1);
   @Nullable
   private final DebugBuffer<Pair<Thread, StackTraceElement[]>> traces = null;

   public void acquire() {
      if (this.traces != null) {
         Thread thread = Thread.currentThread();
         this.traces.push(Pair.of(thread, thread.getStackTrace()));
      }

      ThreadingDetector.checkAndLock(this.lock, this.traces, "PalettedContainer");
   }

   public void release() {
      this.lock.release();
   }

   public PalettedContainer(Palette<T> pGlobalPalette, IdMapper<T> pRegistry, Function<CompoundTag, T> pReader, Function<T, CompoundTag> pWriter, T pDefaultValue) {
      this.globalPalette = pGlobalPalette;
      this.registry = pRegistry;
      this.reader = pReader;
      this.writer = pWriter;
      this.defaultValue = pDefaultValue;
      this.setBits(4);
   }

   private static int getIndex(int pX, int pY, int pZ) {
      return pY << 8 | pZ << 4 | pX;
   }

   private void setBits(int pBits) {
      setBits(pBits, false);
   }
   private void setBits(int pBits, boolean forceBits) {
      if (pBits != this.bits) {
         this.bits = pBits;
         if (this.bits <= 4) {
            this.bits = 4;
            this.palette = new LinearPalette<>(this.registry, this.bits, this, this.reader);
         } else if (this.bits < 9) {
            this.palette = new HashMapPalette<>(this.registry, this.bits, this, this.reader, this.writer);
         } else {
            this.palette = this.globalPalette;
            this.bits = Mth.ceillog2(this.registry.size());
            if (forceBits)
               this.bits = pBits;
         }

         this.palette.idFor(this.defaultValue);
         this.storage = new BitStorage(this.bits, 4096);
      }
   }

   /**
    * Called when the underlying palette needs to resize itself to support additional objects.
    * @return The new integer mapping for the object added.
    * @param pBits The new palette size, in bits.
    */
   public int onResize(int pBits, T pObjectAdded) {
      BitStorage bitstorage = this.storage;
      Palette<T> palette = this.palette;
      this.setBits(pBits);

      for(int i = 0; i < bitstorage.getSize(); ++i) {
         T t = palette.valueFor(bitstorage.get(i));
         if (t != null) {
            this.set(i, t);
         }
      }

      return this.palette.idFor(pObjectAdded);
   }

   public T getAndSet(int pX, int pY, int pZ, T pState) {
      Object object;
      try {
         this.acquire();
         T t = this.getAndSet(getIndex(pX, pY, pZ), pState);
         object = t;
      } finally {
         this.release();
      }

      return (T)object;
   }

   public T getAndSetUnchecked(int pX, int pY, int pZ, T pState) {
      return this.getAndSet(getIndex(pX, pY, pZ), pState);
   }

   private T getAndSet(int pIndex, T pState) {
      int i = this.palette.idFor(pState);
      int j = this.storage.getAndSet(pIndex, i);
      T t = this.palette.valueFor(j);
      return (T)(t == null ? this.defaultValue : t);
   }

   public void set(int pX, int pY, int pZ, T pState) {
      try {
         this.acquire();
         this.set(getIndex(pX, pY, pZ), pState);
      } finally {
         this.release();
      }

   }

   private void set(int pIndex, T pState) {
      int i = this.palette.idFor(pState);
      this.storage.set(pIndex, i);
   }

   public T get(int pX, int pY, int pZ) {
      return this.get(getIndex(pX, pY, pZ));
   }

   protected T get(int pIndex) {
      T t = this.palette.valueFor(this.storage.get(pIndex));
      return (T)(t == null ? this.defaultValue : t);
   }

   public void read(FriendlyByteBuf pBuffer) {
      try {
         this.acquire();
         int i = pBuffer.readByte();
         if (this.bits != i) {
            this.setBits(i, true); //Forge, Force bit density to fix network issues, resize below if needed.
         }

         this.palette.read(pBuffer);
         pBuffer.readLongArray(this.storage.getRaw());
      } finally {
         this.release();
      }

      int regSize = Mth.ceillog2(this.registry.size());
      if (this.palette == globalPalette && this.bits != regSize) // Resize bits to fit registry.
         this.onResize(regSize, defaultValue);
   }

   public void write(FriendlyByteBuf pBuffer) {
      try {
         this.acquire();
         pBuffer.writeByte(this.bits);
         this.palette.write(pBuffer);
         pBuffer.writeLongArray(this.storage.getRaw());
      } finally {
         this.release();
      }

   }

   public void read(ListTag pTag, long[] pData) {
      try {
         this.acquire();
         int i = Math.max(4, Mth.ceillog2(pTag.size()));
         if (i != this.bits) {
            this.setBits(i);
         }

         this.palette.read(pTag);
         int j = pData.length * 64 / 4096;
         if (this.palette == this.globalPalette) {
            Palette<T> palette = new HashMapPalette<>(this.registry, i, this.dummyPaletteResize, this.reader, this.writer);
            palette.read(pTag);
            BitStorage bitstorage = new BitStorage(i, 4096, pData);

            for(int k = 0; k < 4096; ++k) {
               this.storage.set(k, this.globalPalette.idFor(palette.valueFor(bitstorage.get(k))));
            }
         } else if (j == this.bits) {
            System.arraycopy(pData, 0, this.storage.getRaw(), 0, pData.length);
         } else {
            BitStorage bitstorage1 = new BitStorage(j, 4096, pData);

            for(int l = 0; l < 4096; ++l) {
               this.storage.set(l, bitstorage1.get(l));
            }
         }
      } finally {
         this.release();
      }

   }

   public void write(CompoundTag pTag, String pPaletteName, String pPaletteDataName) {
      try {
         this.acquire();
         HashMapPalette<T> hashmappalette = new HashMapPalette<>(this.registry, this.bits, this.dummyPaletteResize, this.reader, this.writer);
         T t = this.defaultValue;
         int i = hashmappalette.idFor(this.defaultValue);
         int[] aint = new int[4096];

         for(int j = 0; j < 4096; ++j) {
            T t1 = this.get(j);
            if (t1 != t) {
               t = t1;
               i = hashmappalette.idFor(t1);
            }

            aint[j] = i;
         }

         ListTag listtag = new ListTag();
         hashmappalette.write(listtag);
         pTag.put(pPaletteName, listtag);
         int l = Math.max(4, Mth.ceillog2(listtag.size()));
         BitStorage bitstorage = new BitStorage(l, 4096);

         for(int k = 0; k < aint.length; ++k) {
            bitstorage.set(k, aint[k]);
         }

         pTag.putLongArray(pPaletteDataName, bitstorage.getRaw());
      } finally {
         this.release();
      }

   }

   public int getSerializedSize() {
      return 1 + this.palette.getSerializedSize() + FriendlyByteBuf.getVarIntSize(this.storage.getSize()) + this.storage.getRaw().length * 8;
   }

   public boolean maybeHas(Predicate<T> pPredicate) {
      return this.palette.maybeHas(pPredicate);
   }

   /**
    * Counts the number of instances of each state in the container.
    * The provided consumer is invoked for each state with the number of instances.
    */
   public void count(PalettedContainer.CountConsumer<T> pCountConsumer) {
      Int2IntMap int2intmap = new Int2IntOpenHashMap();
      this.storage.getAll((p_156469_) -> {
         int2intmap.put(p_156469_, int2intmap.get(p_156469_) + 1);
      });
      int2intmap.int2IntEntrySet().forEach((p_156466_) -> {
         pCountConsumer.accept(this.palette.valueFor(p_156466_.getIntKey()), p_156466_.getIntValue());
      });
   }

   @FunctionalInterface
   public interface CountConsumer<T> {
      void accept(T pState, int pCount);
   }
}
