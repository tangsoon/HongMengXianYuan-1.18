package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ShatteredSavanaSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
   public ShatteredSavanaSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> p_75130_) {
      super(p_75130_);
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
   public void apply(Random p_164186_, ChunkAccess p_164187_, Biome p_164188_, int p_164189_, int p_164190_, int p_164191_, double p_164192_, BlockState p_164193_, BlockState p_164194_, int p_164195_, int p_164196_, long p_164197_, SurfaceBuilderBaseConfiguration p_164198_) {
      if (p_164192_ > 1.75D) {
         SurfaceBuilder.DEFAULT.apply(p_164186_, p_164187_, p_164188_, p_164189_, p_164190_, p_164191_, p_164192_, p_164193_, p_164194_, p_164195_, p_164196_, p_164197_, SurfaceBuilder.CONFIG_STONE);
      } else if (p_164192_ > -0.5D) {
         SurfaceBuilder.DEFAULT.apply(p_164186_, p_164187_, p_164188_, p_164189_, p_164190_, p_164191_, p_164192_, p_164193_, p_164194_, p_164195_, p_164196_, p_164197_, SurfaceBuilder.CONFIG_COARSE_DIRT);
      } else {
         SurfaceBuilder.DEFAULT.apply(p_164186_, p_164187_, p_164188_, p_164189_, p_164190_, p_164191_, p_164192_, p_164193_, p_164194_, p_164195_, p_164196_, p_164197_, SurfaceBuilder.CONFIG_GRASS);
      }

   }
}