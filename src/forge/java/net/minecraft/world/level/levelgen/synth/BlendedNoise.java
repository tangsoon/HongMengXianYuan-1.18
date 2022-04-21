package net.minecraft.world.level.levelgen.synth;

import java.util.stream.IntStream;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.RandomSource;

/**
 * This class wraps three individual perlin noise octaves samplers.
 * It computes the octaves of the main noise, and then uses that as a linear interpolation value between the minimum and
 * maximum limit noises.
 */
public class BlendedNoise {
   private final PerlinNoise minLimitNoise;
   private final PerlinNoise maxLimitNoise;
   private final PerlinNoise mainNoise;

   public BlendedNoise(PerlinNoise pMinLimitNoise, PerlinNoise pMaxLimitNoise, PerlinNoise pMainNoise) {
      this.minLimitNoise = pMinLimitNoise;
      this.maxLimitNoise = pMaxLimitNoise;
      this.mainNoise = pMainNoise;
   }

   public BlendedNoise(RandomSource pRandomSource) {
      this(new PerlinNoise(pRandomSource, IntStream.rangeClosed(-15, 0)), new PerlinNoise(pRandomSource, IntStream.rangeClosed(-15, 0)), new PerlinNoise(pRandomSource, IntStream.rangeClosed(-7, 0)));
   }

   public double sampleAndClampNoise(int pX, int pY, int pZ, double pLimitHorizontalScale, double pLimitVerticalScale, double pMainHorizontalScale, double pMainVerticalScale) {
      double d0 = 0.0D;
      double d1 = 0.0D;
      double d2 = 0.0D;
      boolean flag = true;
      double d3 = 1.0D;

      for(int i = 0; i < 8; ++i) {
         ImprovedNoise improvednoise = this.mainNoise.getOctaveNoise(i);
         if (improvednoise != null) {
            d2 += improvednoise.noise(PerlinNoise.wrap((double)pX * pMainHorizontalScale * d3), PerlinNoise.wrap((double)pY * pMainVerticalScale * d3), PerlinNoise.wrap((double)pZ * pMainHorizontalScale * d3), pMainVerticalScale * d3, (double)pY * pMainVerticalScale * d3) / d3;
         }

         d3 /= 2.0D;
      }

      double d8 = (d2 / 10.0D + 1.0D) / 2.0D;
      boolean flag1 = d8 >= 1.0D;
      boolean flag2 = d8 <= 0.0D;
      d3 = 1.0D;

      for(int j = 0; j < 16; ++j) {
         double d4 = PerlinNoise.wrap((double)pX * pLimitHorizontalScale * d3);
         double d5 = PerlinNoise.wrap((double)pY * pLimitVerticalScale * d3);
         double d6 = PerlinNoise.wrap((double)pZ * pLimitHorizontalScale * d3);
         double d7 = pLimitVerticalScale * d3;
         if (!flag1) {
            ImprovedNoise improvednoise1 = this.minLimitNoise.getOctaveNoise(j);
            if (improvednoise1 != null) {
               d0 += improvednoise1.noise(d4, d5, d6, d7, (double)pY * d7) / d3;
            }
         }

         if (!flag2) {
            ImprovedNoise improvednoise2 = this.maxLimitNoise.getOctaveNoise(j);
            if (improvednoise2 != null) {
               d1 += improvednoise2.noise(d4, d5, d6, d7, (double)pY * d7) / d3;
            }
         }

         d3 /= 2.0D;
      }

      return Mth.clampedLerp(d0 / 512.0D, d1 / 512.0D, d8);
   }
}