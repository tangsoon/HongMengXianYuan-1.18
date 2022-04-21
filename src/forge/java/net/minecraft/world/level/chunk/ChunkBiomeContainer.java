package net.minecraft.world.level.chunk;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.core.IdMap;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkBiomeContainer implements BiomeManager.NoiseBiomeSource {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int WIDTH_BITS = Mth.ceillog2(16) - 2;
   private static final int HORIZONTAL_MASK = (1 << WIDTH_BITS) - 1;
   public static final int MAX_SIZE = 1 << WIDTH_BITS + WIDTH_BITS + DimensionType.BITS_FOR_Y - 2;
   private final IdMap<Biome> biomeRegistry;
   private final Biome[] biomes;
   private final int quartMinY;
   private final int quartHeight;

   protected ChunkBiomeContainer(IdMap<Biome> pBiomeRegistry, LevelHeightAccessor pLevel, Biome[] pBiomes) {
      this.biomeRegistry = pBiomeRegistry;
      this.biomes = pBiomes;
      this.quartMinY = QuartPos.fromBlock(pLevel.getMinBuildHeight());
      this.quartHeight = QuartPos.fromBlock(pLevel.getHeight()) - 1;
   }

   public ChunkBiomeContainer(IdMap<Biome> pBiomeRegistry, LevelHeightAccessor pLevel, int[] pBiomes) {
      this(pBiomeRegistry, pLevel, new Biome[pBiomes.length]);
      int i = -1;

      for(int j = 0; j < this.biomes.length; ++j) {
         int k = pBiomes[j];
         Biome biome = pBiomeRegistry.byId(k);
         if (biome == null) {
            if (i == -1) {
               i = j;
            }

            this.biomes[j] = pBiomeRegistry.byId(0);
         } else {
            this.biomes[j] = biome;
         }
      }

      if (i != -1) {
         LOGGER.warn("Invalid biome data received, starting from {}: {}", i, Arrays.toString(pBiomes));
      }

   }

   public ChunkBiomeContainer(IdMap<Biome> pBiomeRegistry, LevelHeightAccessor pLevel, ChunkPos pPos, BiomeSource pBiomeSource) {
      this(pBiomeRegistry, pLevel, pPos, pBiomeSource, (int[])null);
   }

   public ChunkBiomeContainer(IdMap<Biome> pBiomeRegistry, LevelHeightAccessor pLevel, ChunkPos pPos, BiomeSource pBiomeSource, @Nullable int[] pBiomes) {
      this(pBiomeRegistry, pLevel, new Biome[(1 << WIDTH_BITS + WIDTH_BITS) * ceilDiv(pLevel.getHeight(), 4)]);
      int i = QuartPos.fromBlock(pPos.getMinBlockX());
      int j = this.quartMinY;
      int k = QuartPos.fromBlock(pPos.getMinBlockZ());

      for(int l = 0; l < this.biomes.length; ++l) {
         if (pBiomes != null && l < pBiomes.length) {
            this.biomes[l] = pBiomeRegistry.byId(pBiomes[l]);
         }

         if (this.biomes[l] == null) {
            this.biomes[l] = generateBiomeForIndex(pBiomeSource, i, j, k, l);
         }
      }

   }

   private static int ceilDiv(int p_156141_, int p_156142_) {
      return (p_156141_ + p_156142_ - 1) / p_156142_;
   }

   private static Biome generateBiomeForIndex(BiomeSource pSource, int pX, int pY, int pZ, int pIndex) {
      int i = pIndex & HORIZONTAL_MASK;
      int j = pIndex >> WIDTH_BITS + WIDTH_BITS;
      int k = pIndex >> WIDTH_BITS & HORIZONTAL_MASK;
      return pSource.getNoiseBiome(pX + i, pY + j, pZ + k);
   }

   public int[] writeBiomes() {
      int[] aint = new int[this.biomes.length];

      for(int i = 0; i < this.biomes.length; ++i) {
         aint[i] = this.biomeRegistry.getId(this.biomes[i]);
      }

      return aint;
   }

   /**
    * Gets the biome at the given quart positions.
    * Note that the coordinates passed into this method are 1/4 the scale of block coordinates. The noise biome is then
    * used by the {@link net.minecraft.world.level.biome.BiomeZoomer} to produce a biome for each unique position,
    * whilst only saving the biomes once per each 4x4x4 cube.
    */
   public Biome getNoiseBiome(int pX, int pY, int pZ) {
      int i = pX & HORIZONTAL_MASK;
      int j = Mth.clamp(pY - this.quartMinY, 0, this.quartHeight);
      int k = pZ & HORIZONTAL_MASK;
      return this.biomes[j << WIDTH_BITS + WIDTH_BITS | k << WIDTH_BITS | i];
   }
}