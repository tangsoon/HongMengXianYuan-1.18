package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class NetherForestSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
   private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();
   protected long seed;
   private PerlinNoise decorationNoise;

   public NetherForestSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> p_75036_) {
      super(p_75036_);
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
      int i = pSeaLevel;
      int j = pX & 15;
      int k = pZ & 15;
      double d0 = this.decorationNoise.getValue((double)pX * 0.1D, (double)pSeaLevel, (double)pZ * 0.1D);
      boolean flag = d0 > 0.15D + pRandom.nextDouble() * 0.35D;
      double d1 = this.decorationNoise.getValue((double)pX * 0.1D, 109.0D, (double)pZ * 0.1D);
      boolean flag1 = d1 > 0.25D + pRandom.nextDouble() * 0.9D;
      int l = (int)(pNoise / 3.0D + 3.0D + pRandom.nextDouble() * 0.25D);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i1 = -1;
      BlockState blockstate = pConfig.getUnderMaterial();

      for(int j1 = 127; j1 >= pMinSurfaceLevel; --j1) {
         blockpos$mutableblockpos.set(j, j1, k);
         BlockState blockstate1 = pConfig.getTopMaterial();
         BlockState blockstate2 = pChunk.getBlockState(blockpos$mutableblockpos);
         if (blockstate2.isAir()) {
            i1 = -1;
         } else if (blockstate2.is(pDefaultBlock.getBlock())) {
            if (i1 == -1) {
               boolean flag2 = false;
               if (l <= 0) {
                  flag2 = true;
                  blockstate = pConfig.getUnderMaterial();
               }

               if (flag) {
                  blockstate1 = pConfig.getUnderMaterial();
               } else if (flag1) {
                  blockstate1 = pConfig.getUnderwaterMaterial();
               }

               if (j1 < i && flag2) {
                  blockstate1 = pDefaultFluid;
               }

               i1 = l;
               if (j1 >= i - 1) {
                  pChunk.setBlockState(blockpos$mutableblockpos, blockstate1, false);
               } else {
                  pChunk.setBlockState(blockpos$mutableblockpos, blockstate, false);
               }
            } else if (i1 > 0) {
               --i1;
               pChunk.setBlockState(blockpos$mutableblockpos, blockstate, false);
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
      if (this.seed != pSeed || this.decorationNoise == null) {
         this.decorationNoise = new PerlinNoise(new WorldgenRandom(pSeed), ImmutableList.of(0));
      }

      this.seed = pSeed;
   }
}