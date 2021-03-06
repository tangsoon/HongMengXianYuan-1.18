package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class TheEndBiomeSource extends BiomeSource {
   public static final Codec<TheEndBiomeSource> CODEC = RecordCodecBuilder.create((p_48644_) -> {
      return p_48644_.group(RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter((p_151890_) -> {
         return p_151890_.biomes;
      }), Codec.LONG.fieldOf("seed").stable().forGetter((p_151888_) -> {
         return p_151888_.seed;
      })).apply(p_48644_, p_48644_.stable(TheEndBiomeSource::new));
   });
   private static final float ISLAND_THRESHOLD = -0.9F;
   public static final int ISLAND_CHUNK_DISTANCE = 64;
   private static final long ISLAND_CHUNK_DISTANCE_SQR = 4096L;
   private final SimplexNoise islandNoise;
   private final Registry<Biome> biomes;
   private final long seed;
   private final Biome end;
   private final Biome highlands;
   private final Biome midlands;
   private final Biome islands;
   private final Biome barrens;

   public TheEndBiomeSource(Registry<Biome> p_48628_, long p_48629_) {
      this(p_48628_, p_48629_, p_48628_.getOrThrow(Biomes.THE_END), p_48628_.getOrThrow(Biomes.END_HIGHLANDS), p_48628_.getOrThrow(Biomes.END_MIDLANDS), p_48628_.getOrThrow(Biomes.SMALL_END_ISLANDS), p_48628_.getOrThrow(Biomes.END_BARRENS));
   }

   private TheEndBiomeSource(Registry<Biome> pBiomes, long pSeed, Biome pEnd, Biome pHighlands, Biome pMidlands, Biome pIslands, Biome pBarrens) {
      super(ImmutableList.of(pEnd, pHighlands, pMidlands, pIslands, pBarrens));
      this.biomes = pBiomes;
      this.seed = pSeed;
      this.end = pEnd;
      this.highlands = pHighlands;
      this.midlands = pMidlands;
      this.islands = pIslands;
      this.barrens = pBarrens;
      WorldgenRandom worldgenrandom = new WorldgenRandom(pSeed);
      worldgenrandom.consumeCount(17292);
      this.islandNoise = new SimplexNoise(worldgenrandom);
   }

   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public BiomeSource withSeed(long pSeed) {
      return new TheEndBiomeSource(this.biomes, pSeed, this.end, this.highlands, this.midlands, this.islands, this.barrens);
   }

   /**
    * Gets the biome at the given quart positions.
    * Note that the coordinates passed into this method are 1/4 the scale of block coordinates. The noise biome is then
    * used by the {@link net.minecraft.world.level.biome.BiomeZoomer} to produce a biome for each unique position,
    * whilst only saving the biomes once per each 4x4x4 cube.
    */
   public Biome getNoiseBiome(int pX, int pY, int pZ) {
      int i = pX >> 2;
      int j = pZ >> 2;
      if ((long)i * (long)i + (long)j * (long)j <= 4096L) {
         return this.end;
      } else {
         float f = getHeightValue(this.islandNoise, i * 2 + 1, j * 2 + 1);
         if (f > 40.0F) {
            return this.highlands;
         } else if (f >= 0.0F) {
            return this.midlands;
         } else {
            return f < -20.0F ? this.islands : this.barrens;
         }
      }
   }

   public boolean stable(long pSeed) {
      return this.seed == pSeed;
   }

   public static float getHeightValue(SimplexNoise pNoise, int pX, int pZ) {
      int i = pX / 2;
      int j = pZ / 2;
      int k = pX % 2;
      int l = pZ % 2;
      float f = 100.0F - Mth.sqrt((float)(pX * pX + pZ * pZ)) * 8.0F;
      f = Mth.clamp(f, -100.0F, 80.0F);

      for(int i1 = -12; i1 <= 12; ++i1) {
         for(int j1 = -12; j1 <= 12; ++j1) {
            long k1 = (long)(i + i1);
            long l1 = (long)(j + j1);
            if (k1 * k1 + l1 * l1 > 4096L && pNoise.getValue((double)k1, (double)l1) < (double)-0.9F) {
               float f1 = (Mth.abs((float)k1) * 3439.0F + Mth.abs((float)l1) * 147.0F) % 13.0F + 9.0F;
               float f2 = (float)(k - i1 * 2);
               float f3 = (float)(l - j1 * 2);
               float f4 = 100.0F - Mth.sqrt(f2 * f2 + f3 * f3) * f1;
               f4 = Mth.clamp(f4, -100.0F, 80.0F);
               f = Math.max(f, f4);
            }
         }
      }

      return f;
   }
}