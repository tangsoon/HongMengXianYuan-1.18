package net.minecraft.world.level.levelgen;

import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;

/**
 * An abstraction for the "perlerp" (itself a portmanteau of "perlin", from Perlin noise, and "lerp", indicating linear
 * interpolation) function used in Minecraft's noise generation.
 * Tri-linear interpolation avoids sampling noise at each position, and instead samples cell corners, and linearly
 * interpolates values between said corners. This manages said interpolation, in a way friendly to iteration to avoid
 * unnecessary computations.
 */
public class NoiseInterpolator {
   private double[][] slice0;
   private double[][] slice1;
   private final int cellCountY;
   private final int cellCountZ;
   private final int cellNoiseMinY;
   private final NoiseInterpolator.NoiseColumnFiller noiseColumnFiller;
   private double noise000;
   private double noise001;
   private double noise100;
   private double noise101;
   private double noise010;
   private double noise011;
   private double noise110;
   private double noise111;
   private double valueXZ00;
   private double valueXZ10;
   private double valueXZ01;
   private double valueXZ11;
   private double valueZ0;
   private double valueZ1;
   private final int firstCellXInChunk;
   private final int firstCellZInChunk;

   public NoiseInterpolator(int pCellCountX, int pCellCountY, int pCellCountZ, ChunkPos pChunkPos, int pCellNoiseMinY, NoiseInterpolator.NoiseColumnFiller pNoiseColumnFiller) {
      this.cellCountY = pCellCountY;
      this.cellCountZ = pCellCountZ;
      this.cellNoiseMinY = pCellNoiseMinY;
      this.noiseColumnFiller = pNoiseColumnFiller;
      this.slice0 = allocateSlice(pCellCountY, pCellCountZ);
      this.slice1 = allocateSlice(pCellCountY, pCellCountZ);
      this.firstCellXInChunk = pChunkPos.x * pCellCountX;
      this.firstCellZInChunk = pChunkPos.z * pCellCountZ;
   }

   private static double[][] allocateSlice(int pCellCountY, int pCellCountZ) {
      int i = pCellCountZ + 1;
      int j = pCellCountY + 1;
      double[][] adouble = new double[i][j];

      for(int k = 0; k < i; ++k) {
         adouble[k] = new double[j];
      }

      return adouble;
   }

   public void initializeForFirstCellX() {
      this.fillSlice(this.slice0, this.firstCellXInChunk);
   }

   public void advanceCellX(int pX) {
      this.fillSlice(this.slice1, this.firstCellXInChunk + pX + 1);
   }

   private void fillSlice(double[][] pCell, int pCellX) {
      for(int i = 0; i < this.cellCountZ + 1; ++i) {
         int j = this.firstCellZInChunk + i;
         this.noiseColumnFiller.fillNoiseColumn(pCell[i], pCellX, j, this.cellNoiseMinY, this.cellCountY);
      }

   }

   public void selectCellYZ(int pCellX, int pCellZ) {
      this.noise000 = this.slice0[pCellZ][pCellX];
      this.noise001 = this.slice0[pCellZ + 1][pCellX];
      this.noise100 = this.slice1[pCellZ][pCellX];
      this.noise101 = this.slice1[pCellZ + 1][pCellX];
      this.noise010 = this.slice0[pCellZ][pCellX + 1];
      this.noise011 = this.slice0[pCellZ + 1][pCellX + 1];
      this.noise110 = this.slice1[pCellZ][pCellX + 1];
      this.noise111 = this.slice1[pCellZ + 1][pCellX + 1];
   }

   public void updateForY(double pY) {
      this.valueXZ00 = Mth.lerp(pY, this.noise000, this.noise010);
      this.valueXZ10 = Mth.lerp(pY, this.noise100, this.noise110);
      this.valueXZ01 = Mth.lerp(pY, this.noise001, this.noise011);
      this.valueXZ11 = Mth.lerp(pY, this.noise101, this.noise111);
   }

   public void updateForX(double pX) {
      this.valueZ0 = Mth.lerp(pX, this.valueXZ00, this.valueXZ10);
      this.valueZ1 = Mth.lerp(pX, this.valueXZ01, this.valueXZ11);
   }

   public double calculateValue(double pValue) {
      return Mth.lerp(pValue, this.valueZ0, this.valueZ1);
   }

   public void swapSlices() {
      double[][] adouble = this.slice0;
      this.slice0 = this.slice1;
      this.slice1 = adouble;
   }

   @FunctionalInterface
   public interface NoiseColumnFiller {
      void fillNoiseColumn(double[] pNoiseValues, int pCellX, int pCellZ, int pMinCellY, int pCellCountY);
   }
}