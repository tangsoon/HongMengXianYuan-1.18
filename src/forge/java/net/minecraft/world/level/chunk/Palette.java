package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;

public interface Palette<T> {
   int idFor(T pState);

   boolean maybeHas(Predicate<T> pFilter);

   @Nullable
   T valueFor(int pId);

   void read(FriendlyByteBuf pBuffer);

   void write(FriendlyByteBuf pBuffer);

   int getSerializedSize();

   int getSize();

   void read(ListTag pTag);
}