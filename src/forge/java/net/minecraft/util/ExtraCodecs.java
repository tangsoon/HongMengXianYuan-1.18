package net.minecraft.util;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExtraCodecs {
   public static final Codec<Integer> NON_NEGATIVE_INT = intRangeWithMessage(0, Integer.MAX_VALUE, (p_144656_) -> {
      return "Value must be non-negative: " + p_144656_;
   });
   public static final Codec<Integer> POSITIVE_INT = intRangeWithMessage(1, Integer.MAX_VALUE, (p_144643_) -> {
      return "Value must be positive: " + p_144643_;
   });

   public static <F, S> Codec<Either<F, S>> xor(Codec<F> pFirst, Codec<S> pSecond) {
      return new ExtraCodecs.XorCodec<>(pFirst, pSecond);
   }

   private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRangeWithMessage(N pMin, N pMax, Function<N, String> pErrorMessage) {
      return (p_144652_) -> {
         return p_144652_.compareTo(pMin) >= 0 && p_144652_.compareTo(pMax) <= 0 ? DataResult.success(p_144652_) : DataResult.error(pErrorMessage.apply(p_144652_));
      };
   }

   private static Codec<Integer> intRangeWithMessage(int pMin, int pMax, Function<Integer, String> pErrorMessage) {
      Function<Integer, DataResult<Integer>> function = checkRangeWithMessage(pMin, pMax, pErrorMessage);
      return Codec.INT.flatXmap(function, function);
   }

   public static <T> Function<List<T>, DataResult<List<T>>> nonEmptyListCheck() {
      return (p_144654_) -> {
         return p_144654_.isEmpty() ? DataResult.error("List must have contents") : DataResult.success(p_144654_);
      };
   }

   public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> pCodec) {
      return pCodec.flatXmap(nonEmptyListCheck(), nonEmptyListCheck());
   }

   public static <T> Function<List<Supplier<T>>, DataResult<List<Supplier<T>>>> nonNullSupplierListCheck() {
      return (p_181033_) -> {
         List<String> list = Lists.newArrayList();

         for(int i = 0; i < p_181033_.size(); ++i) {
            Supplier<T> supplier = p_181033_.get(i);

            try {
               if (supplier.get() == null) {
                  list.add("Missing value [" + i + "] : " + supplier);
               }
            } catch (Exception exception) {
               list.add("Invalid value [" + i + "]: " + supplier + ", message: " + exception.getMessage());
            }
         }

         return !list.isEmpty() ? DataResult.error(String.join("; ", list)) : DataResult.success(p_181033_, Lifecycle.stable());
      };
   }

   public static <T> Function<Supplier<T>, DataResult<Supplier<T>>> nonNullSupplierCheck() {
      return (p_181035_) -> {
         try {
            if (p_181035_.get() == null) {
               return DataResult.error("Missing value: " + p_181035_);
            }
         } catch (Exception exception) {
            return DataResult.error("Invalid value: " + p_181035_ + ", message: " + exception.getMessage());
         }

         return DataResult.success(p_181035_, Lifecycle.stable());
      };
   }

   static final class XorCodec<F, S> implements Codec<Either<F, S>> {
      private final Codec<F> first;
      private final Codec<S> second;

      public XorCodec(Codec<F> pFirst, Codec<S> pSecond) {
         this.first = pFirst;
         this.second = pSecond;
      }

      public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> pOps, T pInput) {
         DataResult<Pair<Either<F, S>, T>> dataresult = this.first.decode(pOps, pInput).map((p_144673_) -> {
            return p_144673_.mapFirst(Either::left);
         });
         DataResult<Pair<Either<F, S>, T>> dataresult1 = this.second.decode(pOps, pInput).map((p_144667_) -> {
            return p_144667_.mapFirst(Either::right);
         });
         Optional<Pair<Either<F, S>, T>> optional = dataresult.result();
         Optional<Pair<Either<F, S>, T>> optional1 = dataresult1.result();
         if (optional.isPresent() && optional1.isPresent()) {
            return DataResult.error("Both alternatives read successfully, can not pick the correct one; first: " + optional.get() + " second: " + optional1.get(), optional.get());
         } else {
            return optional.isPresent() ? dataresult : dataresult1;
         }
      }

      public <T> DataResult<T> encode(Either<F, S> pInput, DynamicOps<T> pOps, T pPrefix) {
         return pInput.map((p_144677_) -> {
            return this.first.encode(p_144677_, pOps, pPrefix);
         }, (p_144671_) -> {
            return this.second.encode(p_144671_, pOps, pPrefix);
         });
      }

      public boolean equals(Object pOther) {
         if (this == pOther) {
            return true;
         } else if (pOther != null && this.getClass() == pOther.getClass()) {
            ExtraCodecs.XorCodec<?, ?> xorcodec = (ExtraCodecs.XorCodec)pOther;
            return Objects.equals(this.first, xorcodec.first) && Objects.equals(this.second, xorcodec.second);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(this.first, this.second);
      }

      public String toString() {
         return "XorCodec[" + this.first + ", " + this.second + "]";
      }
   }
}