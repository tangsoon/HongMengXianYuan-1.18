package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;

public class GlobalPalette<T> implements Palette<T> {
   private final IdMapper<T> registry;
   private final T defaultValue;

   public GlobalPalette(IdMapper<T> pRegistry, T pDefaultValue) {
      this.registry = pRegistry;
      this.defaultValue = pDefaultValue;
   }

   public int idFor(T pState) {
      int i = this.registry.getId(pState);
      return i == -1 ? 0 : i;
   }

   public boolean maybeHas(Predicate<T> pFilter) {
      return true;
   }

   public T valueFor(int pId) {
      T t = this.registry.byId(pId);
      return (T)(t == null ? this.defaultValue : t);
   }

   public void read(FriendlyByteBuf pBuffer) {
   }

   public void write(FriendlyByteBuf pBuffer) {
   }

   public int getSerializedSize() {
      return FriendlyByteBuf.getVarIntSize(0);
   }

   public int getSize() {
      return this.registry.size();
   }

   public void read(ListTag pTag) {
   }
}