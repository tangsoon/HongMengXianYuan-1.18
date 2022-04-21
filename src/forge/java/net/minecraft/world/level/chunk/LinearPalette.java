package net.minecraft.world.level.chunk;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;

public class LinearPalette<T> implements Palette<T> {
   private final IdMapper<T> registry;
   private final T[] values;
   private final PaletteResize<T> resizeHandler;
   private final Function<CompoundTag, T> reader;
   private final int bits;
   private int size;

   public LinearPalette(IdMapper<T> pRegistry, int pBits, PaletteResize<T> pResizeHandler, Function<CompoundTag, T> pReader) {
      this.registry = pRegistry;
      this.values = (T[])(new Object[1 << pBits]);
      this.bits = pBits;
      this.resizeHandler = pResizeHandler;
      this.reader = pReader;
   }

   public int idFor(T pState) {
      for(int i = 0; i < this.size; ++i) {
         if (this.values[i] == pState) {
            return i;
         }
      }

      int j = this.size;
      if (j < this.values.length) {
         this.values[j] = pState;
         ++this.size;
         return j;
      } else {
         return this.resizeHandler.onResize(this.bits + 1, pState);
      }
   }

   public boolean maybeHas(Predicate<T> pFilter) {
      for(int i = 0; i < this.size; ++i) {
         if (pFilter.test(this.values[i])) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public T valueFor(int pId) {
      return (T)(pId >= 0 && pId < this.size ? this.values[pId] : null);
   }

   public void read(FriendlyByteBuf pBuffer) {
      this.size = pBuffer.readVarInt();

      for(int i = 0; i < this.size; ++i) {
         this.values[i] = this.registry.byId(pBuffer.readVarInt());
      }

   }

   public void write(FriendlyByteBuf pBuffer) {
      pBuffer.writeVarInt(this.size);

      for(int i = 0; i < this.size; ++i) {
         pBuffer.writeVarInt(this.registry.getId(this.values[i]));
      }

   }

   public int getSerializedSize() {
      int i = FriendlyByteBuf.getVarIntSize(this.getSize());

      for(int j = 0; j < this.getSize(); ++j) {
         i += FriendlyByteBuf.getVarIntSize(this.registry.getId(this.values[j]));
      }

      return i;
   }

   public int getSize() {
      return this.size;
   }

   public void read(ListTag pTag) {
      for(int i = 0; i < pTag.size(); ++i) {
         this.values[i] = this.reader.apply(pTag.getCompound(i));
      }

      this.size = pTag.size();
   }
}