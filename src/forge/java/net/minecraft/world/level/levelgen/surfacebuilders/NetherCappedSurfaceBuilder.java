package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Codec;
import java.util.Comparator;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public abstract class NetherCappedSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
   private long seed;
   private ImmutableMap<BlockState, PerlinNoise> floorNoises = ImmutableMap.of();
   private ImmutableMap<BlockState, PerlinNoise> ceilingNoises = ImmutableMap.of();
   private PerlinNoise patchNoise;

   public NetherCappedSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> p_74989_) {
      super(p_74989_);
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
      int i = pSeaLevel + 1;
      int j = pX & 15;
      int k = pZ & 15;
      int l = (int)(pNoise / 3.0D + 3.0D + pRandom.nextDouble() * 0.25D);
      int i1 = (int)(pNoise / 3.0D + 3.0D + pRandom.nextDouble() * 0.25D);
      double d0 = 0.03125D;
      boolean flag = this.patchNoise.getValue((double)pX * 0.03125D, 109.0D, (double)pZ * 0.03125D) * 75.0D + pRandom.nextDouble() > 0.0D;
      BlockState blockstate = this.ceilingNoises.entrySet().stream().max(Comparator.comparing((p_75030_) -> {
         return p_75030_.getValue().getValue((double)pX, (double)pSeaLevel, (double)pZ);
      })).get().getKey();
      BlockState blockstate1 = this.floorNoises.entrySet().stream().max(Comparator.comparing((p_74994_) -> {
         return p_74994_.getValue().getValue((double)pX, (double)pSeaLevel, (double)pZ);
      })).get().getKey();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      BlockState blockstate2 = pChunk.getBlockState(blockpos$mutableblockpos.set(j, 128, k));

      for(int j1 = 127; j1 >= pMinSurfaceLevel; --j1) {
         blockpos$mutableblockpos.set(j, j1, k);
         BlockState blockstate3 = pChunk.getBlockState(blockpos$mutableblockpos);
         if (blockstate2.is(pDefaultBlock.getBlock()) && (blockstate3.isAir() || blockstate3 == pDefaultFluid)) {
            for(int k1 = 0; k1 < l; ++k1) {
               blockpos$mutableblockpos.move(Direction.UP);
               if (!pChunk.getBlockState(blockpos$mutableblockpos).is(pDefaultBlock.getBlock())) {
                  break;
               }

               pChunk.setBlockState(blockpos$mutableblockpos, blockstate, false);
            }

            blockpos$mutableblockpos.set(j, j1, k);
         }

         if ((blockstate2.isAir() || blockstate2 == pDefaultFluid) && blockstate3.is(pDefaultBlock.getBlock())) {
            for(int l1 = 0; l1 < i1 && pChunk.getBlockState(blockpos$mutableblockpos).is(pDefaultBlock.getBlock()); ++l1) {
               if (flag && j1 >= i - 4 && j1 <= i + 1) {
                  pChunk.setBlockState(blockpos$mutableblockpos, this.getPatchBlockState(), false);
               } else {
                  pChunk.setBlockState(blockpos$mutableblockpos, blockstate1, false);
               }

               blockpos$mutableblockpos.move(Direction.DOWN);
            }
         }

         blockstate2 = blockstate3;
      }

   }

   /**
    * Initialize this surface builder with the current world seed.
    * This is called prior to {@link #apply}. In general, most subclasses cache the world seed and only re-initialize if
    * the cached seed is different from the provided seed, for performance.
    */
   public void initNoise(long pSeed) {
      if (this.seed != pSeed || this.patchNoise == null || this.floorNoises.isEmpty() || this.ceilingNoises.isEmpty()) {
         this.floorNoises = initPerlinNoises(this.getFloorBlockStates(), pSeed);
         this.ceilingNoises = initPerlinNoises(this.getCeilingBlockStates(), pSeed + (long)this.floorNoises.size());
         this.patchNoise = new PerlinNoise(new WorldgenRandom(pSeed + (long)this.floorNoises.size() + (long)this.ceilingNoises.size()), ImmutableList.of(0));
      }

      this.seed = pSeed;
   }

   private static ImmutableMap<BlockState, PerlinNoise> initPerlinNoises(ImmutableList<BlockState> pStates, long pSeed) {
      Builder<BlockState, PerlinNoise> builder = new Builder<>();

      for(BlockState blockstate : pStates) {
         builder.put(blockstate, new PerlinNoise(new WorldgenRandom(pSeed), ImmutableList.of(-4)));
         ++pSeed;
      }

      return builder.build();
   }

   protected abstract ImmutableList<BlockState> getFloorBlockStates();

   protected abstract ImmutableList<BlockState> getCeilingBlockStates();

   protected abstract BlockState getPatchBlockState();
}