package net.minecraft.world.level.levelgen.synth;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import java.util.function.LongFunction;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;

/**
 * This class generates multiple octaves of perlin noise. Each individual octave is an instance of {@link
 * net.minecraft.world.level.levelgen.synth.ImprovedNoise}.
 * Mojang uses the term 'Perlin' to describe octaves or fBm (Fractal Brownian Motion) noise.
 */
public class PerlinNoise implements SurfaceNoise {
   private static final int ROUND_OFF = 33554432;
   private final ImprovedNoise[] noiseLevels;
   private final DoubleList amplitudes;
   private final double lowestFreqValueFactor;
   private final double lowestFreqInputFactor;

   public PerlinNoise(RandomSource pRandomSource, IntStream pOctaves) {
      this(pRandomSource, pOctaves.boxed().collect(ImmutableList.toImmutableList()));
   }

   public PerlinNoise(RandomSource pRandomSource, List<Integer> pOctaves) {
      this(pRandomSource, new IntRBTreeSet(pOctaves));
   }

   public static PerlinNoise create(RandomSource pRandomSource, int pOctaves, double... pAmplitudes) {
      return create(pRandomSource, pOctaves, new DoubleArrayList(pAmplitudes));
   }

   public static PerlinNoise create(RandomSource pRandomSource, int pOctaves, DoubleList pAmplitudes) {
      return new PerlinNoise(pRandomSource, Pair.of(pOctaves, pAmplitudes));
   }

   private static Pair<Integer, DoubleList> makeAmplitudes(IntSortedSet pOctaves) {
      if (pOctaves.isEmpty()) {
         throw new IllegalArgumentException("Need some octaves!");
      } else {
         int i = -pOctaves.firstInt();
         int j = pOctaves.lastInt();
         int k = i + j + 1;
         if (k < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
         } else {
            DoubleList doublelist = new DoubleArrayList(new double[k]);
            IntBidirectionalIterator intbidirectionaliterator = pOctaves.iterator();

            while(intbidirectionaliterator.hasNext()) {
               int l = intbidirectionaliterator.nextInt();
               doublelist.set(l + i, 1.0D);
            }

            return Pair.of(-i, doublelist);
         }
      }
   }

   private PerlinNoise(RandomSource pRandomSource, IntSortedSet pOctaves) {
      this(pRandomSource, pOctaves, WorldgenRandom::new);
   }

   private PerlinNoise(RandomSource pRandomSource, IntSortedSet pOctaves, LongFunction<RandomSource> pRandomSourceFactory) {
      this(pRandomSource, makeAmplitudes(pOctaves), pRandomSourceFactory);
   }

   protected PerlinNoise(RandomSource pRandomSource, Pair<Integer, DoubleList> pAmplitudes) {
      this(pRandomSource, pAmplitudes, WorldgenRandom::new);
   }

   protected PerlinNoise(RandomSource pRandomSource, Pair<Integer, DoubleList> pAmplitudes, LongFunction<RandomSource> pRandomSourceFactory) {
      int i = pAmplitudes.getFirst();
      this.amplitudes = pAmplitudes.getSecond();
      ImprovedNoise improvednoise = new ImprovedNoise(pRandomSource);
      int j = this.amplitudes.size();
      int k = -i;
      this.noiseLevels = new ImprovedNoise[j];
      if (k >= 0 && k < j) {
         double d0 = this.amplitudes.getDouble(k);
         if (d0 != 0.0D) {
            this.noiseLevels[k] = improvednoise;
         }
      }

      for(int l = k - 1; l >= 0; --l) {
         if (l < j) {
            double d1 = this.amplitudes.getDouble(l);
            if (d1 != 0.0D) {
               this.noiseLevels[l] = new ImprovedNoise(pRandomSource);
            } else {
               skipOctave(pRandomSource);
            }
         } else {
            skipOctave(pRandomSource);
         }
      }

      if (k < j - 1) {
         throw new IllegalArgumentException("Positive octaves are temporarily disabled");
      } else {
         this.lowestFreqInputFactor = Math.pow(2.0D, (double)(-k));
         this.lowestFreqValueFactor = Math.pow(2.0D, (double)(j - 1)) / (Math.pow(2.0D, (double)j) - 1.0D);
      }
   }

   private static void skipOctave(RandomSource pRandomSource) {
      pRandomSource.consumeCount(262);
   }

   public double getValue(double pX, double pY, double pZ) {
      return this.getValue(pX, pY, pZ, 0.0D, 0.0D, false);
   }

   @Deprecated
   public double getValue(double pX, double pY, double pZ, double pYScale, double pYMax, boolean pUseFixedY) {
      double d0 = 0.0D;
      double d1 = this.lowestFreqInputFactor;
      double d2 = this.lowestFreqValueFactor;

      for(int i = 0; i < this.noiseLevels.length; ++i) {
         ImprovedNoise improvednoise = this.noiseLevels[i];
         if (improvednoise != null) {
            double d3 = improvednoise.noise(wrap(pX * d1), pUseFixedY ? -improvednoise.yo : wrap(pY * d1), wrap(pZ * d1), pYScale * d1, pYMax * d1);
            d0 += this.amplitudes.getDouble(i) * d3 * d2;
         }

         d1 *= 2.0D;
         d2 /= 2.0D;
      }

      return d0;
   }

   /**
    * @return A single octave of Perlin noise.
    */
   @Nullable
   public ImprovedNoise getOctaveNoise(int pOctave) {
      return this.noiseLevels[this.noiseLevels.length - 1 - pOctave];
   }

   public static double wrap(double pValue) {
      return pValue - (double)Mth.lfloor(pValue / 3.3554432E7D + 0.5D) * 3.3554432E7D;
   }

   public double getSurfaceNoiseValue(double pX, double pY, double pZ, double pYMax) {
      return this.getValue(pX, pY, 0.0D, pZ, pYMax, false);
   }
}