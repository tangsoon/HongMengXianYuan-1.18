package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class DefaultSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
   public DefaultSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> p_74788_) {
      super(p_74788_);
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
      this.apply(pRandom, pChunk, pBiome, pX, pZ, pHeight, pNoise, pDefaultBlock, pDefaultFluid, pConfig.getTopMaterial(), pConfig.getUnderMaterial(), pConfig.getUnderwaterMaterial(), pSeaLevel, pMinSurfaceLevel);
   }

   /**
    * Builds the default surface, using the three block states from the surface builder's configuration.
    * This is used by multiple surface builders with specific block states, to build surfaces based on other noise, for
    * example.
    */
   protected void apply(Random pRandom, ChunkAccess pChunk, Biome pBiome, int pX, int pZ, int pHeight, double pNoise, BlockState pDefaultBlock, BlockState pDefaultFluid, BlockState pTopMaterial, BlockState pUnderMaterial, BlockState pUnderwaterMaterial, int pSeaLevel, int pMinSurfaceLevel) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i = (int)(pNoise / 3.0D + 3.0D + pRandom.nextDouble() * 0.25D);
      if (i == 0) {
         boolean flag = false;

         for(int j = pHeight; j >= pMinSurfaceLevel; --j) {
            blockpos$mutableblockpos.set(pX, j, pZ);
            BlockState blockstate = pChunk.getBlockState(blockpos$mutableblockpos);
            if (blockstate.isAir()) {
               flag = false;
            } else if (blockstate.is(pDefaultBlock.getBlock())) {
               if (!flag) {
                  BlockState blockstate1;
                  if (j >= pSeaLevel) {
                     blockstate1 = Blocks.AIR.defaultBlockState();
                  } else if (j == pSeaLevel - 1) {
                     blockstate1 = pBiome.getTemperature(blockpos$mutableblockpos) < 0.15F ? Blocks.ICE.defaultBlockState() : pDefaultFluid;
                  } else if (j >= pSeaLevel - (7 + i)) {
                     blockstate1 = pDefaultBlock;
                  } else {
                     blockstate1 = pUnderwaterMaterial;
                  }

                  pChunk.setBlockState(blockpos$mutableblockpos, blockstate1, false);
               }

               flag = true;
            }
         }
      } else {
         BlockState blockstate3 = pUnderMaterial;
         int k = -1;

         for(int l = pHeight; l >= pMinSurfaceLevel; --l) {
            blockpos$mutableblockpos.set(pX, l, pZ);
            BlockState blockstate4 = pChunk.getBlockState(blockpos$mutableblockpos);
            if (blockstate4.isAir()) {
               k = -1;
            } else if (blockstate4.is(pDefaultBlock.getBlock())) {
               if (k == -1) {
                  k = i;
                  BlockState blockstate2;
                  if (l >= pSeaLevel + 2) {
                     blockstate2 = pTopMaterial;
                  } else if (l >= pSeaLevel - 1) {
                     blockstate3 = pUnderMaterial;
                     blockstate2 = pTopMaterial;
                  } else if (l >= pSeaLevel - 4) {
                     blockstate3 = pUnderMaterial;
                     blockstate2 = pUnderMaterial;
                  } else if (l >= pSeaLevel - (7 + i)) {
                     blockstate2 = blockstate3;
                  } else {
                     blockstate3 = pDefaultBlock;
                     blockstate2 = pUnderwaterMaterial;
                  }

                  pChunk.setBlockState(blockpos$mutableblockpos, blockstate2, false);
               } else if (k > 0) {
                  --k;
                  pChunk.setBlockState(blockpos$mutableblockpos, blockstate3, false);
                  if (k == 0 && blockstate3.is(Blocks.SAND) && i > 1) {
                     k = pRandom.nextInt(4) + Math.max(0, l - pSeaLevel);
                     blockstate3 = blockstate3.is(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState();
                  }
               }
            }
         }
      }

   }
}