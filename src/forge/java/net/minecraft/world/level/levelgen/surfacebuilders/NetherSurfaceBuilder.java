package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class NetherSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
   private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();
   private static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();
   private static final BlockState SOUL_SAND = Blocks.SOUL_SAND.defaultBlockState();
   protected long seed;
   protected PerlinNoise decorationNoise;

   public NetherSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> p_75072_) {
      super(p_75072_);
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
      double d0 = 0.03125D;
      boolean flag = this.decorationNoise.getValue((double)pX * 0.03125D, (double)pZ * 0.03125D, 0.0D) * 75.0D + pRandom.nextDouble() > 0.0D;
      boolean flag1 = this.decorationNoise.getValue((double)pX * 0.03125D, 109.0D, (double)pZ * 0.03125D) * 75.0D + pRandom.nextDouble() > 0.0D;
      int l = (int)(pNoise / 3.0D + 3.0D + pRandom.nextDouble() * 0.25D);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i1 = -1;
      BlockState blockstate = pConfig.getTopMaterial();
      BlockState blockstate1 = pConfig.getUnderMaterial();

      for(int j1 = 127; j1 >= pMinSurfaceLevel; --j1) {
         blockpos$mutableblockpos.set(j, j1, k);
         BlockState blockstate2 = pChunk.getBlockState(blockpos$mutableblockpos);
         if (blockstate2.isAir()) {
            i1 = -1;
         } else if (blockstate2.is(pDefaultBlock.getBlock())) {
            if (i1 == -1) {
               boolean flag2 = false;
               if (l <= 0) {
                  flag2 = true;
                  blockstate1 = pConfig.getUnderMaterial();
               } else if (j1 >= i - 4 && j1 <= i + 1) {
                  blockstate = pConfig.getTopMaterial();
                  blockstate1 = pConfig.getUnderMaterial();
                  if (flag1) {
                     blockstate = GRAVEL;
                     blockstate1 = pConfig.getUnderMaterial();
                  }

                  if (flag) {
                     blockstate = SOUL_SAND;
                     blockstate1 = SOUL_SAND;
                  }
               }

               if (j1 < i && flag2) {
                  blockstate = pDefaultFluid;
               }

               i1 = l;
               if (j1 >= i - 1) {
                  pChunk.setBlockState(blockpos$mutableblockpos, blockstate, false);
               } else {
                  pChunk.setBlockState(blockpos$mutableblockpos, blockstate1, false);
               }
            } else if (i1 > 0) {
               --i1;
               pChunk.setBlockState(blockpos$mutableblockpos, blockstate1, false);
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
         this.decorationNoise = new PerlinNoise(new WorldgenRandom(pSeed), IntStream.rangeClosed(-3, 0));
      }

      this.seed = pSeed;
   }
}