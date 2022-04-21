package net.minecraft.world.level.levelgen;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

/**
 * Samples the base terrain density noise, using the biome's depth and scale factors.
 */
public class NoiseSampler {
   private static final int OLD_CELL_COUNT_Y = 32;
   protected static final float[] BIOME_WEIGHTS = Util.make(new float[25], (p_158687_) -> {
      for(int i = -2; i <= 2; ++i) {
         for(int j = -2; j <= 2; ++j) {
            float f = 10.0F / Mth.sqrt((float)(i * i + j * j) + 0.2F);
            p_158687_[i + 2 + (j + 2) * 5] = f;
         }
      }

   });
   protected final BiomeSource biomeSource;
   protected final int cellWidth;
   protected final int cellHeight;
   protected final int cellCountY;
   protected final NoiseSettings noiseSettings;
   public final BlendedNoise blendedNoise;
   @Nullable
   public final SimplexNoise islandNoise;
   public final PerlinNoise depthNoise;
   private final double topSlideTarget;
   private final double topSlideSize;
   private final double topSlideOffset;
   private final double bottomSlideTarget;
   private final double bottomSlideSize;
   private final double bottomSlideOffset;
   private final double dimensionDensityFactor;
   private final double dimensionDensityOffset;
   public final NoiseModifier caveNoiseModifier;

   public NoiseSampler(BiomeSource pBiomeSource, int pCellWidth, int pCellHeight, int pCellCountY, NoiseSettings pNoiseSettings, BlendedNoise pBlendedNoise, @Nullable SimplexNoise pIslandNoise, PerlinNoise pDepthNoise, NoiseModifier pCaveNoiseModifier) {
      this.cellWidth = pCellWidth;
      this.cellHeight = pCellHeight;
      this.biomeSource = pBiomeSource;
      this.cellCountY = pCellCountY;
      this.noiseSettings = pNoiseSettings;
      this.blendedNoise = pBlendedNoise;
      this.islandNoise = pIslandNoise;
      this.depthNoise = pDepthNoise;
      this.topSlideTarget = (double)pNoiseSettings.topSlideSettings().target();
      this.topSlideSize = (double)pNoiseSettings.topSlideSettings().size();
      this.topSlideOffset = (double)pNoiseSettings.topSlideSettings().offset();
      this.bottomSlideTarget = (double)pNoiseSettings.bottomSlideSettings().target();
      this.bottomSlideSize = (double)pNoiseSettings.bottomSlideSettings().size();
      this.bottomSlideOffset = (double)pNoiseSettings.bottomSlideSettings().offset();
      this.dimensionDensityFactor = pNoiseSettings.densityFactor();
      this.dimensionDensityOffset = pNoiseSettings.densityOffset();
      this.caveNoiseModifier = pCaveNoiseModifier;
   }

   public void fillNoiseColumn(double[] pNoiseValues, int pCellX, int pCellZ, NoiseSettings pNoiseSettings, int pBiomeY, int pMinCellY, int pCellCountY) {
      double d0;
      double d1;
      if (this.islandNoise != null) {
         d0 = (double)(TheEndBiomeSource.getHeightValue(this.islandNoise, pCellX, pCellZ) - 8.0F);
         if (d0 > 0.0D) {
            d1 = 0.25D;
         } else {
            d1 = 1.0D;
         }
      } else {
         float f = 0.0F;
         float f1 = 0.0F;
         float f2 = 0.0F;
         int i = 2;
         int j = pBiomeY;
         float f3 = this.biomeSource.getNoiseBiome(pCellX, pBiomeY, pCellZ).getDepth();

         for(int k = -2; k <= 2; ++k) {
            for(int l = -2; l <= 2; ++l) {
               Biome biome = this.biomeSource.getNoiseBiome(pCellX + k, j, pCellZ + l);
               float f4 = biome.getDepth();
               float f5 = biome.getScale();
               float f6;
               float f7;
               if (pNoiseSettings.isAmplified() && f4 > 0.0F) {
                  f6 = 1.0F + f4 * 2.0F;
                  f7 = 1.0F + f5 * 4.0F;
               } else {
                  f6 = f4;
                  f7 = f5;
               }

               float f8 = f4 > f3 ? 0.5F : 1.0F;
               float f9 = f8 * BIOME_WEIGHTS[k + 2 + (l + 2) * 5] / (f6 + 2.0F);
               f += f7 * f9;
               f1 += f6 * f9;
               f2 += f9;
            }
         }

         float f10 = f1 / f2;
         float f11 = f / f2;
         double d6 = (double)(f10 * 0.5F - 0.125F);
         double d8 = (double)(f11 * 0.9F + 0.1F);
         d0 = d6 * 0.265625D;
         d1 = 96.0D / d8;
      }

      double d2 = 684.412D * pNoiseSettings.noiseSamplingSettings().xzScale();
      double d3 = 684.412D * pNoiseSettings.noiseSamplingSettings().yScale();
      double d4 = d2 / pNoiseSettings.noiseSamplingSettings().xzFactor();
      double d5 = d3 / pNoiseSettings.noiseSamplingSettings().yFactor();
      double d7 = pNoiseSettings.randomDensityOffset() ? this.getRandomDensity(pCellX, pCellZ) : 0.0D;

      for(int i1 = 0; i1 <= pCellCountY; ++i1) {
         int j1 = i1 + pMinCellY;
         double d9 = this.blendedNoise.sampleAndClampNoise(pCellX, j1, pCellZ, d2, d3, d4, d5);
         double d10 = this.computeInitialDensity(j1, d0, d1, d7) + d9;
         d10 = this.caveNoiseModifier.modifyNoise(d10, j1 * this.cellHeight, pCellZ * this.cellWidth, pCellX * this.cellWidth);
         d10 = this.applySlide(d10, j1);
         pNoiseValues[i1] = d10;
      }

   }

   /**
    * Computes an initial density for the noise, which controls the overall style of the terrain.
    * For example, in the overworld, this density is inversely proportional to the y value, and results in terrain that
    * is solid at the bottom and air at the top.
    */
   protected double computeInitialDensity(int pY, double pDepth, double pScale, double pRandomDensityOffset) {
      double d0 = 1.0D - (double)pY * 2.0D / 32.0D + pRandomDensityOffset;
      double d1 = d0 * this.dimensionDensityFactor + this.dimensionDensityOffset;
      double d2 = (d1 + pDepth) * pScale;
      return d2 * (double)(d2 > 0.0D ? 4 : 1);
   }

   /**
    * Applies a slide factor to the density noise, which interpolates the edges of the world towards a target value.
    */
   protected double applySlide(double pNoise, int pY) {
      int i = Mth.intFloorDiv(this.noiseSettings.minY(), this.cellHeight);
      int j = pY - i;
      if (this.topSlideSize > 0.0D) {
         double d0 = ((double)(this.cellCountY - j) - this.topSlideOffset) / this.topSlideSize;
         pNoise = Mth.clampedLerp(this.topSlideTarget, pNoise, d0);
      }

      if (this.bottomSlideSize > 0.0D) {
         double d1 = ((double)j - this.bottomSlideOffset) / this.bottomSlideSize;
         pNoise = Mth.clampedLerp(this.bottomSlideTarget, pNoise, d1);
      }

      return pNoise;
   }

   protected double getRandomDensity(int pX, int pZ) {
      double d0 = this.depthNoise.getValue((double)(pX * 200), 10.0D, (double)(pZ * 200), 1.0D, 0.0D, true);
      double d1;
      if (d0 < 0.0D) {
         d1 = -d0 * 0.3D;
      } else {
         d1 = d0;
      }

      double d2 = d1 * 24.575625D - 2.0D;
      return d2 < 0.0D ? d2 * 0.009486607142857142D : Math.min(d2, 1.0D) * 0.006640625D;
   }
}