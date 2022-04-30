package net.minecraft.world.level.chunk;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;

public class HashMapPalette<T> implements Palette<T> {
   private final IdMapper<T> registry;
   private final CrudeIncrementalIntIdentityHashBiMap<T> values;
   private final PaletteResize<T> resizeHandler;
   private final Function<CompoundTag, T> reader;
   private final Function<T, CompoundTag> writer;
   private final int bits;

   public HashMapPalette(IdMapper<T> pRegistry, int pSize, PaletteResize<T> pResizeHandler, Function<CompoundTag, T> pReader, Function<T, CompoundTag> pWriter) {
      this.registry = pRegistry;
      this.bits = pSize;
      this.resizeHandler = pResizeHandler;
      this.reader = pReader;
      this.writer = pWriter;
      this.values = new CrudeIncrementalIntIdentityHashBiMap<>(1 << pSize);
   }

   public int idFor(T pState) {
      int i = this.values.getId(pState);
      if (i == -1) {
         i = this.values.add(pState);
         if (i >= 1 << this.bits) {
            i = this.resizeHandler.onResize(this.bits + 1, pState);
         }
      }

      return i;
   }

   public boolean maybeHas(Predicate<T> pFilter) {
      for(int i = 0; i < this.getSize(); ++i) {
         if (pFilter.test(this.values.byId(i))) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public T valueFor(int pId) {
      return this.values.byId(pId);
   }

   public void read(FriendlyByteBuf pBuffer) {
      this.values.clear();
      int i = pBuffer.readVarInt();

      for(int j = 0; j < i; ++j) {
         this.values.add(this.registry.byId(pBuffer.readVarInt()));
      }

   }

   public void write(FriendlyByteBuf pBuffer) {
      int i = this.getSize();
      pBuffer.writeVarInt(i);

      for(int j = 0; j < i; ++j) {
         pBuffer.writeVarInt(this.registry.getId(this.values.byId(j)));
      }

   }

   public int getSerializedSize() {
      int i = FriendlyByteBuf.getVarIntSize(this.getSize());

      for(int j = 0; j < this.getSize(); ++j) {
         i += FriendlyByteBuf.getVarIntSize(this.registry.getId(this.values.byId(j)));
      }

      return i;
   }

   public int getSize() {
      return this.values.size();
   }

   public void read(ListTag pTag) {
      this.values.clear();

      for(int i = 0; i < pTag.size(); ++i) {
         this.values.add(this.reader.apply(pTag.getCompound(i)));
      }

   }

   public void write(ListTag pTag) {
      for(int i = 0; i < this.getSize(); ++i) {
         pTag.add(this.writer.apply(this.values.byId(i)));
      }

   }
}