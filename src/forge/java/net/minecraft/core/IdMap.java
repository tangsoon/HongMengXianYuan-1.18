package net.minecraft.core;

import javax.annotation.Nullable;

public interface IdMap<T> extends Iterable<T> {
   /**
    * @return the integer ID used to identify the given object
    */
   int getId(T pValue);

   @Nullable
   T byId(int pId);
}