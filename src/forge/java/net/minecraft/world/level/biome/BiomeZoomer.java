package net.minecraft.world.level.biome;

public interface BiomeZoomer {
   Biome getBiome(long pSeed, int pX, int pY, int pZ, BiomeManager.NoiseBiomeSource pSource);
}