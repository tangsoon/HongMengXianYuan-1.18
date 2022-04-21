package net.minecraft.world.level.biome;

import com.google.common.hash.Hashing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;

public class BiomeManager {
   static final int CHUNK_CENTER_QUART = QuartPos.fromBlock(8);
   private final BiomeManager.NoiseBiomeSource noiseBiomeSource;
   private final long biomeZoomSeed;
   private final BiomeZoomer zoomer;

   public BiomeManager(BiomeManager.NoiseBiomeSource pNoiseBiomeSource, long pBiomeZoomSeed, BiomeZoomer pZoomer) {
      this.noiseBiomeSource = pNoiseBiomeSource;
      this.biomeZoomSeed = pBiomeZoomSeed;
      this.zoomer = pZoomer;
   }

   public static long obfuscateSeed(long pSeed) {
      return Hashing.sha256().hashLong(pSeed).asLong();
   }

   public BiomeManager withDifferentSource(BiomeSource pNewSource) {
      return new BiomeManager(pNewSource, this.biomeZoomSeed, this.zoomer);
   }

   public Biome getBiome(BlockPos pPos) {
      return this.zoomer.getBiome(this.biomeZoomSeed, pPos.getX(), pPos.getY(), pPos.getZ(), this.noiseBiomeSource);
   }

   public Biome getNoiseBiomeAtPosition(double pX, double pY, double pZ) {
      int i = QuartPos.fromBlock(Mth.floor(pX));
      int j = QuartPos.fromBlock(Mth.floor(pY));
      int k = QuartPos.fromBlock(Mth.floor(pZ));
      return this.getNoiseBiomeAtQuart(i, j, k);
   }

   public Biome getNoiseBiomeAtPosition(BlockPos pPos) {
      int i = QuartPos.fromBlock(pPos.getX());
      int j = QuartPos.fromBlock(pPos.getY());
      int k = QuartPos.fromBlock(pPos.getZ());
      return this.getNoiseBiomeAtQuart(i, j, k);
   }

   public Biome getNoiseBiomeAtQuart(int pX, int pY, int pZ) {
      return this.noiseBiomeSource.getNoiseBiome(pX, pY, pZ);
   }

   public Biome getPrimaryBiomeAtChunk(ChunkPos pChunkPos) {
      return this.noiseBiomeSource.getPrimaryBiome(pChunkPos);
   }

   public interface NoiseBiomeSource {
      /**
       * Gets the biome at the given quart positions.
       * Note that the coordinates passed into this method are 1/4 the scale of block coordinates. The noise biome is
       * then used by the {@link net.minecraft.world.level.biome.BiomeZoomer} to produce a biome for each unique
       * position, whilst only saving the biomes once per each 4x4x4 cube.
       */
      Biome getNoiseBiome(int pX, int pY, int pZ);

      default Biome getPrimaryBiome(ChunkPos pChunkPos) {
         return this.getNoiseBiome(QuartPos.fromSection(pChunkPos.x) + BiomeManager.CHUNK_CENTER_QUART, 0, QuartPos.fromSection(pChunkPos.z) + BiomeManager.CHUNK_CENTER_QUART);
      }
   }
}