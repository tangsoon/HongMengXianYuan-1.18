package net.minecraft.world.level.levelgen;

import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.synth.NoiseUtils;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoodleCavifier {
   private static final int NOODLES_MAX_Y = 30;
   private static final double SPACING_AND_STRAIGHTNESS = 1.5D;
   private static final double XZ_FREQUENCY = 2.6666666666666665D;
   private static final double Y_FREQUENCY = 2.6666666666666665D;
   private final NormalNoise toggleNoiseSource;
   private final NormalNoise thicknessNoiseSource;
   private final NormalNoise noodleANoiseSource;
   private final NormalNoise noodleBNoiseSource;

   public NoodleCavifier(long pSeed) {
      Random random = new Random(pSeed);
      this.toggleNoiseSource = NormalNoise.create(new SimpleRandomSource(random.nextLong()), -8, 1.0D);
      this.thicknessNoiseSource = NormalNoise.create(new SimpleRandomSource(random.nextLong()), -8, 1.0D);
      this.noodleANoiseSource = NormalNoise.create(new SimpleRandomSource(random.nextLong()), -7, 1.0D);
      this.noodleBNoiseSource = NormalNoise.create(new SimpleRandomSource(random.nextLong()), -7, 1.0D);
   }

   public void fillToggleNoiseColumn(double[] pNoiseValues, int pCellX, int pCellZ, int pMinCellY, int pCellCountY) {
      this.fillNoiseColumn(pNoiseValues, pCellX, pCellZ, pMinCellY, pCellCountY, this.toggleNoiseSource, 1.0D);
   }

   public void fillThicknessNoiseColumn(double[] pNoiseValues, int pCellX, int pCellZ, int pMinCellY, int pCellCountY) {
      this.fillNoiseColumn(pNoiseValues, pCellX, pCellZ, pMinCellY, pCellCountY, this.thicknessNoiseSource, 1.0D);
   }

   public void fillRidgeANoiseColumn(double[] pNoiseValues, int pCellX, int pCellZ, int pMinCellY, int pCellCountY) {
      this.fillNoiseColumn(pNoiseValues, pCellX, pCellZ, pMinCellY, pCellCountY, this.noodleANoiseSource, 2.6666666666666665D, 2.6666666666666665D);
   }

   public void fillRidgeBNoiseColumn(double[] pNoiseValues, int pCellX, int pCellZ, int pMinCellY, int pCellCountY) {
      this.fillNoiseColumn(pNoiseValues, pCellX, pCellZ, pMinCellY, pCellCountY, this.noodleBNoiseSource, 2.6666666666666665D, 2.6666666666666665D);
   }

   public void fillNoiseColumn(double[] pNoiseValues, int pCellX, int pCellZ, int pMinCellY, int pCellCountY, NormalNoise pNoise, double pFrequency) {
      this.fillNoiseColumn(pNoiseValues, pCellX, pCellZ, pMinCellY, pCellCountY, pNoise, pFrequency, pFrequency);
   }

   public void fillNoiseColumn(double[] pNoiseValues, int pCellX, int pCellZ, int pMinCellY, int pCellCountY, NormalNoise pNoise, double pHorizontalFrequency, double pVerticalFrequency) {
      int i = 8;
      int j = 4;

      for(int k = 0; k < pCellCountY; ++k) {
         int l = k + pMinCellY;
         int i1 = pCellX * 4;
         int j1 = l * 8;
         int k1 = pCellZ * 4;
         double d0;
         if (j1 < 38) {
            d0 = NoiseUtils.sampleNoiseAndMapToRange(pNoise, (double)i1 * pHorizontalFrequency, (double)j1 * pVerticalFrequency, (double)k1 * pHorizontalFrequency, -1.0D, 1.0D);
         } else {
            d0 = 1.0D;
         }

         pNoiseValues[k] = d0;
      }

   }

   public double noodleCavify(double pNoise, int pX, int pY, int pZ, double pToggle, double pThickness, double pRidgeA, double pRidgeB, int pMinY) {
      if (pY <= 30 && pY >= pMinY + 4) {
         if (pNoise < 0.0D) {
            return pNoise;
         } else if (pToggle < 0.0D) {
            return pNoise;
         } else {
            double d0 = 0.05D;
            double d1 = 0.1D;
            double d2 = Mth.clampedMap(pThickness, -1.0D, 1.0D, 0.05D, 0.1D);
            double d3 = Math.abs(1.5D * pRidgeA) - d2;
            double d4 = Math.abs(1.5D * pRidgeB) - d2;
            double d5 = Math.max(d3, d4);
            return Math.min(pNoise, d5);
         }
      } else {
         return pNoise;
      }
   }
}