package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.material.Material;

public class FrozenOceanSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
   protected static final BlockState PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();
   protected static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.defaultBlockState();
   private static final BlockState AIR = Blocks.AIR.defaultBlockState();
   private static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();
   private static final BlockState ICE = Blocks.ICE.defaultBlockState();
   private PerlinSimplexNoise icebergNoise;
   private PerlinSimplexNoise icebergRoofNoise;
   private long seed;

   public FrozenOceanSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> p_74871_) {
      super(p_74871_);
   }

   /**
    * Applies this surface builder. Surface builders are ran for each x/z position within a chunk, and only have access
    * to the single chunk (and in general, do not process anything other than the x/z column they are considering).
    * At this point during level generation, the chunk (in general) will consist just of air, the {@code defaultBlock}
    * and {@code defaultFluid}.
    * @param pRandom A seeded random to use during surface placement
    * @param pX The x position, in global block coordinates.
    * @param pZ The z position, in global block coordinates
    * @param pHeight The initial height to place surfaces from. Some surface builders may place above this (i.e.
    * icebergs) for extra surface decoration.
    * @param pNoise A noise value which is sampled once per x/z position. Used to place patches of different surface
    * material.
    * @param pDefaultBlock The default block state used by the chunk generator
    * @param pDefaultFluid The default fluid state used by the chunk generator
    * @param pSeaLevel The chunk generator's sea level
    * @param pSeed The world seed.
    * @param pConfig The individual surface builder's configuration
    */
   public void apply(Random pRandom, ChunkAccess pChunk, Biome pBiome, int pX, int pZ, int pHeight, double pNoise, BlockState pDefaultBlock, BlockState pDefaultFluid, int pSeaLevel, int pMinSurfaceLevel, long pSeed, SurfaceBuilderBaseConfiguration pConfig) {
      double d0 = 0.0D;
      double d1 = 0.0D;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      float f = pBiome.getTemperature(blockpos$mutableblockpos.set(pX, 63, pZ));
      double d2 = Math.min(Math.abs(pNoise), this.icebergNoise.getValue((double)pX * 0.1D, (double)pZ * 0.1D, false) * 15.0D);
      if (d2 > 1.8D) {
         double d3 = 0.09765625D;
         double d4 = Math.abs(this.icebergRoofNoise.getValue((double)pX * 0.09765625D, (double)pZ * 0.09765625D, false));
         d0 = d2 * d2 * 1.2D;
         double d5 = Math.ceil(d4 * 40.0D) + 14.0D;
         if (d0 > d5) {
            d0 = d5;
         }

         if (f > 0.1F) {
            d0 -= 2.0D;
         }

         if (d0 > 2.0D) {
            d1 = (double)pSeaLevel - d0 - 7.0D;
            d0 = d0 + (double)pSeaLevel;
         } else {
            d0 = 0.0D;
         }
      }

      int l1 = pX & 15;
      int i = pZ & 15;
      SurfaceBuilderConfiguration surfacebuilderconfiguration = pBiome.getGenerationSettings().getSurfaceBuilderConfig();
      BlockState blockstate = surfacebuilderconfiguration.getUnderMaterial();
      BlockState blockstate4 = surfacebuilderconfiguration.getTopMaterial();
      BlockState blockstate1 = blockstate;
      BlockState blockstate2 = blockstate4;
      int j = (int)(pNoise / 3.0D + 3.0D + pRandom.nextDouble() * 0.25D);
      int k = -1;
      int l = 0;
      int i1 = 2 + pRandom.nextInt(4);
      int j1 = pSeaLevel + 18 + pRandom.nextInt(10);

      for(int k1 = Math.max(pHeight, (int)d0 + 1); k1 >= pMinSurfaceLevel; --k1) {
         blockpos$mutableblockpos.set(l1, k1, i);
         if (pChunk.getBlockState(blockpos$mutableblockpos).isAir() && k1 < (int)d0 && pRandom.nextDouble() > 0.01D) {
            pChunk.setBlockState(blockpos$mutableblockpos, PACKED_ICE, false);
         } else if (pChunk.getBlockState(blockpos$mutableblockpos).getMaterial() == Material.WATER && k1 > (int)d1 && k1 < pSeaLevel && d1 != 0.0D && pRandom.nextDouble() > 0.15D) {
            pChunk.setBlockState(blockpos$mutableblockpos, PACKED_ICE, false);
         }

         BlockState blockstate3 = pChunk.getBlockState(blockpos$mutableblockpos);
         if (blockstate3.isAir()) {
            k = -1;
         } else if (!blockstate3.is(pDefaultBlock.getBlock())) {
            if (blockstate3.is(Blocks.PACKED_ICE) && l <= i1 && k1 > j1) {
               pChunk.setBlockState(blockpos$mutableblockpos, SNOW_BLOCK, false);
               ++l;
            }
         } else if (k == -1) {
            if (j <= 0) {
               blockstate2 = AIR;
               blockstate1 = pDefaultBlock;
            } else if (k1 >= pSeaLevel - 4 && k1 <= pSeaLevel + 1) {
               blockstate2 = blockstate4;
               blockstate1 = blockstate;
            }

            if (k1 < pSeaLevel && (blockstate2 == null || blockstate2.isAir())) {
               if (pBiome.getTemperature(blockpos$mutableblockpos.set(pX, k1, pZ)) < 0.15F) {
                  blockstate2 = ICE;
               } else {
                  blockstate2 = pDefaultFluid;
               }
            }

            k = j;
            if (k1 >= pSeaLevel - 1) {
               pChunk.setBlockState(blockpos$mutableblockpos, blockstate2, false);
            } else if (k1 < pSeaLevel - 7 - j) {
               blockstate2 = AIR;
               blockstate1 = pDefaultBlock;
               pChunk.setBlockState(blockpos$mutableblockpos, GRAVEL, false);
            } else {
               pChunk.setBlockState(blockpos$mutableblockpos, blockstate1, false);
            }
         } else if (k > 0) {
            --k;
            pChunk.setBlockState(blockpos$mutableblockpos, blockstate1, false);
            if (k == 0 && blockstate1.is(Blocks.SAND) && j > 1) {
               k = pRandom.nextInt(4) + Math.max(0, k1 - 63);
               blockstate1 = blockstate1.is(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState();
            }
         }
      }

   }

   /**
    * Initialize this surface builder with the current world seed.
    * This is called prior to {@link #apply}. In general, most subclasses cache the world seed and only re-initialize if
    * the cached seed is different from the provided seed, for performance.
    */
   public void initNoise(long pSeed) {
      if (this.seed != pSeed || this.icebergNoise == null || this.icebergRoofNoise == null) {
         WorldgenRandom worldgenrandom = new WorldgenRandom(pSeed);
         this.icebergNoise = new PerlinSimplexNoise(worldgenrandom, IntStream.rangeClosed(-3, 0));
         this.icebergRoofNoise = new PerlinSimplexNoise(worldgenrandom, ImmutableList.of(0));
      }

      this.seed = pSeed;
   }
}