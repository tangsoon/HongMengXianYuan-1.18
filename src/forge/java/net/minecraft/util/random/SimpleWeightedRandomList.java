package net.minecraft.util.random;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class SimpleWeightedRandomList<E> extends WeightedRandomList<WeightedEntry.Wrapper<E>> {
   public static <E> Codec<SimpleWeightedRandomList<E>> wrappedCodec(Codec<E> pElementCodec) {
      return WeightedEntry.Wrapper.<E>codec(pElementCodec).listOf().xmap(SimpleWeightedRandomList::new, WeightedRandomList::unwrap);
   }

   SimpleWeightedRandomList(List<? extends WeightedEntry.Wrapper<E>> p_146262_) {
      super(p_146262_);
   }

   public static <E> SimpleWeightedRandomList.Builder<E> builder() {
      return new SimpleWeightedRandomList.Builder<>();
   }

   public Optional<E> getRandomValue(Random pRandom) {
      return this.getRandom(pRandom).map(WeightedEntry.Wrapper::getData);
   }

   public static class Builder<E> {
      private final ImmutableList.Builder<WeightedEntry.Wrapper<E>> result = ImmutableList.builder();

      public SimpleWeightedRandomList.Builder<E> add(E pData, int pWeight) {
         this.result.add(WeightedEntry.wrap(pData, pWeight));
         return this;
      }

      public SimpleWeightedRandomList<E> build() {
         return new SimpleWeightedRandomList<>(this.result.build());
      }
   }
}