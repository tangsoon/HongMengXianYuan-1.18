package net.minecraft.core;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class IdMapper<T> implements IdMap<T> {
   public static final int DEFAULT = -1;
   protected int nextId;
   protected final IdentityHashMap<T, Integer> tToId;
   protected final List<T> idToT;

   public IdMapper() {
      this(512);
   }

   public IdMapper(int pExpectedSize) {
      this.idToT = Lists.newArrayListWithExpectedSize(pExpectedSize);
      this.tToId = new IdentityHashMap<>(pExpectedSize);
   }

   public void addMapping(T pKey, int pValue) {
      this.tToId.put(pKey, pValue);

      while(this.idToT.size() <= pValue) {
         this.idToT.add((T)null);
      }

      this.idToT.set(pValue, pKey);
      if (this.nextId <= pValue) {
         this.nextId = pValue + 1;
      }

   }

   public void add(T pKey) {
      this.addMapping(pKey, this.nextId);
   }

   /**
    * @return the integer ID used to identify the given object
    */
   public int getId(T pValue) {
      Integer integer = this.tToId.get(pValue);
      return integer == null ? -1 : integer;
   }

   @Nullable
   public final T byId(int pId) {
      return (T)(pId >= 0 && pId < this.idToT.size() ? this.idToT.get(pId) : null);
   }

   public Iterator<T> iterator() {
      return Iterators.filter(this.idToT.iterator(), Predicates.notNull());
   }

   public boolean contains(int pId) {
      return this.byId(pId) != null;
   }

   public int size() {
      return this.tToId.size();
   }
}