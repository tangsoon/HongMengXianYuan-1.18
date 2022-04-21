package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class GiantTreeTaigaSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
   public GiantTreeTaigaSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> p_74901_) {
      super(p_74901_);
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
   public void apply(Random p_163990_, ChunkAccess p_163991_, Biome p_163992_, int p_163993_, int p_163994_, int p_163995_, double p_163996_, BlockState p_163997_, BlockState p_163998_, int p_163999_, int p_164000_, long p_164001_, SurfaceBuilderBaseConfiguration p_164002_) {
      if (p_163996_ > 1.75D) {
         SurfaceBuilder.DEFAULT.apply(p_163990_, p_163991_, p_163992_, p_163993_, p_163994_, p_163995_, p_163996_, p_163997_, p_163998_, p_163999_, p_164000_, p_164001_, SurfaceBuilder.CONFIG_COARSE_DIRT);
      } else if (p_163996_ > -0.95D) {
         SurfaceBuilder.DEFAULT.apply(p_163990_, p_163991_, p_163992_, p_163993_, p_163994_, p_163995_, p_163996_, p_163997_, p_163998_, p_163999_, p_164000_, p_164001_, SurfaceBuilder.CONFIG_PODZOL);
      } else {
         SurfaceBuilder.DEFAULT.apply(p_163990_, p_163991_, p_163992_, p_163993_, p_163994_, p_163995_, p_163996_, p_163997_, p_163998_, p_163999_, p_164000_, p_164001_, SurfaceBuilder.CONFIG_GRASS);
      }

   }
}