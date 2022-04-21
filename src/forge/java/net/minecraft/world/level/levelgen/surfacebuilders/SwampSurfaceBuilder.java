package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class SwampSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
   public SwampSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> p_75262_) {
      super(p_75262_);
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
   public void apply(Random p_164233_, ChunkAccess p_164234_, Biome p_164235_, int p_164236_, int p_164237_, int p_164238_, double p_164239_, BlockState p_164240_, BlockState p_164241_, int p_164242_, int p_164243_, long p_164244_, SurfaceBuilderBaseConfiguration p_164245_) {
      double d0 = Biome.BIOME_INFO_NOISE.getValue((double)p_164236_ * 0.25D, (double)p_164237_ * 0.25D, false);
      if (d0 > 0.0D) {
         int i = p_164236_ & 15;
         int j = p_164237_ & 15;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k = p_164238_; k >= p_164243_; --k) {
            blockpos$mutableblockpos.set(i, k, j);
            if (!p_164234_.getBlockState(blockpos$mutableblockpos).isAir()) {
               if (k == 62 && !p_164234_.getBlockState(blockpos$mutableblockpos).is(p_164241_.getBlock())) {
                  p_164234_.setBlockState(blockpos$mutableblockpos, p_164241_, false);
               }
               break;
            }
         }
      }

      SurfaceBuilder.DEFAULT.apply(p_164233_, p_164234_, p_164235_, p_164236_, p_164237_, p_164238_, p_164239_, p_164240_, p_164241_, p_164242_, p_164243_, p_164244_, p_164245_);
   }
}