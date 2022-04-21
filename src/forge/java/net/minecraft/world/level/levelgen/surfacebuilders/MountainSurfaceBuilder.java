package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class MountainSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
   public MountainSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> p_74957_) {
      super(p_74957_);
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
   public void apply(Random p_164046_, ChunkAccess p_164047_, Biome p_164048_, int p_164049_, int p_164050_, int p_164051_, double p_164052_, BlockState p_164053_, BlockState p_164054_, int p_164055_, int p_164056_, long p_164057_, SurfaceBuilderBaseConfiguration p_164058_) {
      if (p_164052_ > 1.0D) {
         SurfaceBuilder.DEFAULT.apply(p_164046_, p_164047_, p_164048_, p_164049_, p_164050_, p_164051_, p_164052_, p_164053_, p_164054_, p_164055_, p_164056_, p_164057_, SurfaceBuilder.CONFIG_STONE);
      } else {
         SurfaceBuilder.DEFAULT.apply(p_164046_, p_164047_, p_164048_, p_164049_, p_164050_, p_164051_, p_164052_, p_164053_, p_164054_, p_164055_, p_164056_, p_164057_, SurfaceBuilder.CONFIG_GRASS);
      }

   }
}