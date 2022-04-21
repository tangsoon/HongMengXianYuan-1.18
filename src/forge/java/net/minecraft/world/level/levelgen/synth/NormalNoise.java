package net.minecraft.world.level.levelgen.synth;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import net.minecraft.world.level.levelgen.RandomSource;

/**
 * This samples the sum of two individual samplers of perlin noise octaves.
 * The input coordinates are scaled by {@link #INPUT_FACTOR}, and the result is scaled by {@link #valueFactor}.
 */
public class NormalNoise {
   private static final double INPUT_FACTOR = 1.0181268882175227D;
   private static final double TARGET_DEVIATION = 0.3333333333333333D;
   private final double valueFactor;
   private final PerlinNoise first;
   private final PerlinNoise second;

   public static NormalNoise create(RandomSource pRandomSource, int pOctaves, double... pAmplitudes) {
      return new NormalNoise(pRandomSource, pOctaves, new DoubleArrayList(pAmplitudes));
   }

   public static NormalNoise create(RandomSource pRandomSource, int pOctaves, DoubleList pAmplitudes) {
      return new NormalNoise(pRandomSource, pOctaves, pAmplitudes);
   }

   private NormalNoise(RandomSource pRandomSource, int pOctaves, DoubleList pAmplitudes) {
      this.first = PerlinNoise.create(pRandomSource, pOctaves, pAmplitudes);
      this.second = PerlinNoise.create(pRandomSource, pOctaves, pAmplitudes);
      int i = Integer.MAX_VALUE;
      int j = Integer.MIN_VALUE;
      DoubleListIterator doublelistiterator = pAmplitudes.iterator();

      while(doublelistiterator.hasNext()) {
         int k = doublelistiterator.nextIndex();
         double d0 = doublelistiterator.nextDouble();
         if (d0 != 0.0D) {
            i = Math.min(i, k);
            j = Math.max(j, k);
         }
      }

      this.valueFactor = 0.16666666666666666D / expectedDeviation(j - i);
   }

   private static double expectedDeviation(int pOctaves) {
      return 0.1D * (1.0D + 1.0D / (double)(pOctaves + 1));
   }

   public double getValue(double pX, double pY, double pZ) {
      double d0 = pX * 1.0181268882175227D;
      double d1 = pY * 1.0181268882175227D;
      double d2 = pZ * 1.0181268882175227D;
      return (this.first.getValue(pX, pY, pZ) + this.second.getValue(d0, d1, d2)) * this.valueFactor;
   }
}