package net.minecraft.world.level.levelgen;

import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.synth.NoiseUtils;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class Cavifier implements NoiseModifier {
   private final int minCellY;
   private final NormalNoise layerNoiseSource;
   private final NormalNoise pillarNoiseSource;
   private final NormalNoise pillarRarenessModulator;
   private final NormalNoise pillarThicknessModulator;
   private final NormalNoise spaghetti2dNoiseSource;
   private final NormalNoise spaghetti2dElevationModulator;
   private final NormalNoise spaghetti2dRarityModulator;
   private final NormalNoise spaghetti2dThicknessModulator;
   private final NormalNoise spaghetti3dNoiseSource1;
   private final NormalNoise spaghetti3dNoiseSource2;
   private final NormalNoise spaghetti3dRarityModulator;
   private final NormalNoise spaghetti3dThicknessModulator;
   private final NormalNoise spaghettiRoughnessNoise;
   private final NormalNoise spaghettiRoughnessModulator;
   private final NormalNoise caveEntranceNoiseSource;
   private final NormalNoise cheeseNoiseSource;
   private static final int CHEESE_NOISE_RANGE = 128;
   private static final int SURFACE_DENSITY_THRESHOLD = 170;

   public Cavifier(RandomSource pRandom, int pMinCellY) {
      this.minCellY = pMinCellY;
      this.pillarNoiseSource = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -7, 1.0D, 1.0D);
      this.pillarRarenessModulator = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -8, 1.0D);
      this.pillarThicknessModulator = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -8, 1.0D);
      this.spaghetti2dNoiseSource = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -7, 1.0D);
      this.spaghetti2dElevationModulator = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -8, 1.0D);
      this.spaghetti2dRarityModulator = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -11, 1.0D);
      this.spaghetti2dThicknessModulator = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -11, 1.0D);
      this.spaghetti3dNoiseSource1 = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -7, 1.0D);
      this.spaghetti3dNoiseSource2 = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -7, 1.0D);
      this.spaghetti3dRarityModulator = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -11, 1.0D);
      this.spaghetti3dThicknessModulator = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -8, 1.0D);
      this.spaghettiRoughnessNoise = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -5, 1.0D);
      this.spaghettiRoughnessModulator = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -8, 1.0D);
      this.caveEntranceNoiseSource = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -8, 1.0D, 1.0D, 1.0D);
      this.layerNoiseSource = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -8, 1.0D);
      this.cheeseNoiseSource = NormalNoise.create(new SimpleRandomSource(pRandom.nextLong()), -8, 0.5D, 1.0D, 2.0D, 1.0D, 2.0D, 1.0D, 0.0D, 2.0D, 0.0D);
   }

   /**
    * Modifies the passed in noise value, at the given coordinates.
    * 
    * Note: in most uses of this function, these coordinates are ordered (x, y, z). However, notably, {@link Cavifier}
    * expects them in the order (y, z, x) despite implementing this interface.
    */
   public double modifyNoise(double pNoise, int pY, int pZ, int pX) {
      boolean flag = pNoise < 170.0D;
      double d0 = this.spaghettiRoughness(pX, pY, pZ);
      double d1 = this.getSpaghetti3d(pX, pY, pZ);
      if (flag) {
         return Math.min(pNoise, (d1 + d0) * 128.0D * 5.0D);
      } else {
         double d2 = this.cheeseNoiseSource.getValue((double)pX, (double)pY / 1.5D, (double)pZ);
         double d3 = Mth.clamp(d2 + 0.25D, -1.0D, 1.0D);
         double d4 = (double)((float)(30 - pY) / 8.0F);
         double d5 = d3 + Mth.clampedLerp(0.5D, 0.0D, d4);
         double d6 = this.getLayerizedCaverns(pX, pY, pZ);
         double d7 = this.getSpaghetti2d(pX, pY, pZ);
         double d8 = d5 + d6;
         double d9 = Math.min(d8, Math.min(d1, d7) + d0);
         double d10 = Math.max(d9, this.getPillars(pX, pY, pZ));
         return 128.0D * Mth.clamp(d10, -1.0D, 1.0D);
      }
   }

   private double addEntrances(double pNoise, int pX, int pY, int pZ) {
      double d0 = this.caveEntranceNoiseSource.getValue((double)(pX * 2), (double)pY, (double)(pZ * 2));
      d0 = NoiseUtils.biasTowardsExtreme(d0, 1.0D);
      int i = 0;
      double d1 = (double)(pY - 0) / 40.0D;
      d0 = d0 + Mth.clampedLerp(0.5D, pNoise, d1);
      double d2 = 3.0D;
      d0 = 4.0D * d0 + 3.0D;
      return Math.min(pNoise, d0);
   }

   private double getPillars(int pX, int pY, int pZ) {
      double d0 = 0.0D;
      double d1 = 2.0D;
      double d2 = NoiseUtils.sampleNoiseAndMapToRange(this.pillarRarenessModulator, (double)pX, (double)pY, (double)pZ, 0.0D, 2.0D);
      double d3 = 0.0D;
      double d4 = 1.1D;
      double d5 = NoiseUtils.sampleNoiseAndMapToRange(this.pillarThicknessModulator, (double)pX, (double)pY, (double)pZ, 0.0D, 1.1D);
      d5 = Math.pow(d5, 3.0D);
      double d6 = 25.0D;
      double d7 = 0.3D;
      double d8 = this.pillarNoiseSource.getValue((double)pX * 25.0D, (double)pY * 0.3D, (double)pZ * 25.0D);
      d8 = d5 * (d8 * 2.0D - d2);
      return d8 > 0.03D ? d8 : Double.NEGATIVE_INFINITY;
   }

   private double getLayerizedCaverns(int pX, int pY, int pZ) {
      double d0 = this.layerNoiseSource.getValue((double)pX, (double)(pY * 8), (double)pZ);
      return Mth.square(d0) * 4.0D;
   }

   private double getSpaghetti3d(int pX, int pY, int pZ) {
      double d0 = this.spaghetti3dRarityModulator.getValue((double)(pX * 2), (double)pY, (double)(pZ * 2));
      double d1 = Cavifier.QuantizedSpaghettiRarity.getSpaghettiRarity3D(d0);
      double d2 = 0.065D;
      double d3 = 0.088D;
      double d4 = NoiseUtils.sampleNoiseAndMapToRange(this.spaghetti3dThicknessModulator, (double)pX, (double)pY, (double)pZ, 0.065D, 0.088D);
      double d5 = sampleWithRarity(this.spaghetti3dNoiseSource1, (double)pX, (double)pY, (double)pZ, d1);
      double d6 = Math.abs(d1 * d5) - d4;
      double d7 = sampleWithRarity(this.spaghetti3dNoiseSource2, (double)pX, (double)pY, (double)pZ, d1);
      double d8 = Math.abs(d1 * d7) - d4;
      return clampToUnit(Math.max(d6, d8));
   }

   private double getSpaghetti2d(int pX, int pY, int pZ) {
      double d0 = this.spaghetti2dRarityModulator.getValue((double)(pX * 2), (double)pY, (double)(pZ * 2));
      double d1 = Cavifier.QuantizedSpaghettiRarity.getSphaghettiRarity2D(d0);
      double d2 = 0.6D;
      double d3 = 1.3D;
      double d4 = NoiseUtils.sampleNoiseAndMapToRange(this.spaghetti2dThicknessModulator, (double)(pX * 2), (double)pY, (double)(pZ * 2), 0.6D, 1.3D);
      double d5 = sampleWithRarity(this.spaghetti2dNoiseSource, (double)pX, (double)pY, (double)pZ, d1);
      double d6 = 0.083D;
      double d7 = Math.abs(d1 * d5) - 0.083D * d4;
      int i = this.minCellY;
      int j = 8;
      double d8 = NoiseUtils.sampleNoiseAndMapToRange(this.spaghetti2dElevationModulator, (double)pX, 0.0D, (double)pZ, (double)i, 8.0D);
      double d9 = Math.abs(d8 - (double)pY / 8.0D) - 1.0D * d4;
      d9 = d9 * d9 * d9;
      return clampToUnit(Math.max(d9, d7));
   }

   private double spaghettiRoughness(int pX, int pY, int pZ) {
      double d0 = NoiseUtils.sampleNoiseAndMapToRange(this.spaghettiRoughnessModulator, (double)pX, (double)pY, (double)pZ, 0.0D, 0.1D);
      return (0.4D - Math.abs(this.spaghettiRoughnessNoise.getValue((double)pX, (double)pY, (double)pZ))) * d0;
   }

   private static double clampToUnit(double pValue) {
      return Mth.clamp(pValue, -1.0D, 1.0D);
   }

   private static double sampleWithRarity(NormalNoise pNoise, double pX, double pY, double pZ, double pFrequency) {
      return pNoise.getValue(pX / pFrequency, pY / pFrequency, pZ / pFrequency);
   }

   static final class QuantizedSpaghettiRarity {
      private QuantizedSpaghettiRarity() {
      }

      static double getSphaghettiRarity2D(double pRarityModulator) {
         if (pRarityModulator < -0.75D) {
            return 0.5D;
         } else if (pRarityModulator < -0.5D) {
            return 0.75D;
         } else if (pRarityModulator < 0.5D) {
            return 1.0D;
         } else {
            return pRarityModulator < 0.75D ? 2.0D : 3.0D;
         }
      }

      static double getSpaghettiRarity3D(double pRarityModulator) {
         if (pRarityModulator < -0.5D) {
            return 0.75D;
         } else if (pRarityModulator < 0.0D) {
            return 1.0D;
         } else {
            return pRarityModulator < 0.5D ? 1.5D : 2.0D;
         }
      }
   }
}