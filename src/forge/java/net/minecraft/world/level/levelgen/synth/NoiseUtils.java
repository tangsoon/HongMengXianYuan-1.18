package net.minecraft.world.level.levelgen.synth;

import net.minecraft.util.Mth;

public class NoiseUtils {
   /**
    * Samples the value of {@code noise}, and maps it linearly to the range [{@code min}, {@code max}].
    */
   public static double sampleNoiseAndMapToRange(NormalNoise pNoise, double pX, double pY, double pZ, double pMin, double pMax) {
      double d0 = pNoise.getValue(pX, pY, pZ);
      return Mth.map(d0, -1.0D, 1.0D, pMin, pMax);
   }

   /**
    * Takes an input value and biases it using a sine function towards two larger magnitude values.
    * @param pValue A value in the range [-1, 1]
    * @param pBias The effect of the bias. At {@code 0.0}, there will be no bias. Mojang only uses {@code 1.0} here.
    */
   public static double biasTowardsExtreme(double pValue, double pBias) {
      return pValue + Math.sin(Math.PI * pValue) * pBias / Math.PI;
   }
}