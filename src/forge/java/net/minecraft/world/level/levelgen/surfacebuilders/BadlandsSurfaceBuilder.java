package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public class BadlandsSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
   protected static final int MAX_CLAY_DEPTH = 15;
   private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
   private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
   private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();
   private static final BlockState YELLOW_TERRACOTTA = Blocks.YELLOW_TERRACOTTA.defaultBlockState();
   private static final BlockState BROWN_TERRACOTTA = Blocks.BROWN_TERRACOTTA.defaultBlockState();
   private static final BlockState RED_TERRACOTTA = Blocks.RED_TERRACOTTA.defaultBlockState();
   private static final BlockState LIGHT_GRAY_TERRACOTTA = Blocks.LIGHT_GRAY_TERRACOTTA.defaultBlockState();
   protected BlockState[] clayBands;
   protected long seed;
   protected PerlinSimplexNoise pillarNoise;
   protected PerlinSimplexNoise pillarRoofNoise;
   protected PerlinSimplexNoise clayBandsOffsetNoise;

   public BadlandsSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> p_74716_) {
      super(p_74716_);
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
      int i = pX & 15;
      int j = pZ & 15;
      BlockState blockstate = WHITE_TERRACOTTA;
      SurfaceBuilderConfiguration surfacebuilderconfiguration = pBiome.getGenerationSettings().getSurfaceBuilderConfig();
      BlockState blockstate1 = surfacebuilderconfiguration.getUnderMaterial();
      BlockState blockstate2 = surfacebuilderconfiguration.getTopMaterial();
      BlockState blockstate3 = blockstate1;
      int k = (int)(pNoise / 3.0D + 3.0D + pRandom.nextDouble() * 0.25D);
      boolean flag = Math.cos(pNoise / 3.0D * Math.PI) > 0.0D;
      int l = -1;
      boolean flag1 = false;
      int i1 = 0;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int j1 = pHeight; j1 >= pMinSurfaceLevel; --j1) {
         if (i1 < 15) {
            blockpos$mutableblockpos.set(i, j1, j);
            BlockState blockstate4 = pChunk.getBlockState(blockpos$mutableblockpos);
            if (blockstate4.isAir()) {
               l = -1;
            } else if (blockstate4.is(pDefaultBlock.getBlock())) {
               if (l == -1) {
                  flag1 = false;
                  if (k <= 0) {
                     blockstate = Blocks.AIR.defaultBlockState();
                     blockstate3 = pDefaultBlock;
                  } else if (j1 >= pSeaLevel - 4 && j1 <= pSeaLevel + 1) {
                     blockstate = WHITE_TERRACOTTA;
                     blockstate3 = blockstate1;
                  }

                  if (j1 < pSeaLevel && (blockstate == null || blockstate.isAir())) {
                     blockstate = pDefaultFluid;
                  }

                  l = k + Math.max(0, j1 - pSeaLevel);
                  if (j1 >= pSeaLevel - 1) {
                     if (j1 > pSeaLevel + 3 + k) {
                        BlockState blockstate5;
                        if (j1 >= 64 && j1 <= 127) {
                           if (flag) {
                              blockstate5 = TERRACOTTA;
                           } else {
                              blockstate5 = this.getBand(pX, j1, pZ);
                           }
                        } else {
                           blockstate5 = ORANGE_TERRACOTTA;
                        }

                        pChunk.setBlockState(blockpos$mutableblockpos, blockstate5, false);
                     } else {
                        pChunk.setBlockState(blockpos$mutableblockpos, blockstate2, false);
                        flag1 = true;
                     }
                  } else {
                     pChunk.setBlockState(blockpos$mutableblockpos, blockstate3, false);
                     if (blockstate3.is(Blocks.WHITE_TERRACOTTA) || blockstate3.is(Blocks.ORANGE_TERRACOTTA) || blockstate3.is(Blocks.MAGENTA_TERRACOTTA) || blockstate3.is(Blocks.LIGHT_BLUE_TERRACOTTA) || blockstate3.is(Blocks.YELLOW_TERRACOTTA) || blockstate3.is(Blocks.LIME_TERRACOTTA) || blockstate3.is(Blocks.PINK_TERRACOTTA) || blockstate3.is(Blocks.GRAY_TERRACOTTA) || blockstate3.is(Blocks.LIGHT_GRAY_TERRACOTTA) || blockstate3.is(Blocks.CYAN_TERRACOTTA) || blockstate3.is(Blocks.PURPLE_TERRACOTTA) || blockstate3.is(Blocks.BLUE_TERRACOTTA) || blockstate3.is(Blocks.BROWN_TERRACOTTA) || blockstate3.is(Blocks.GREEN_TERRACOTTA) || blockstate3.is(Blocks.RED_TERRACOTTA) || blockstate3.is(Blocks.BLACK_TERRACOTTA)) {
                        pChunk.setBlockState(blockpos$mutableblockpos, ORANGE_TERRACOTTA, false);
                     }
                  }
               } else if (l > 0) {
                  --l;
                  if (flag1) {
                     pChunk.setBlockState(blockpos$mutableblockpos, ORANGE_TERRACOTTA, false);
                  } else {
                     pChunk.setBlockState(blockpos$mutableblockpos, this.getBand(pX, j1, pZ), false);
                  }
               }

               ++i1;
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
      if (this.seed != pSeed || this.clayBands == null) {
         this.generateBands(pSeed);
      }

      if (this.seed != pSeed || this.pillarNoise == null || this.pillarRoofNoise == null) {
         WorldgenRandom worldgenrandom = new WorldgenRandom(pSeed);
         this.pillarNoise = new PerlinSimplexNoise(worldgenrandom, IntStream.rangeClosed(-3, 0));
         this.pillarRoofNoise = new PerlinSimplexNoise(worldgenrandom, ImmutableList.of(0));
      }

      this.seed = pSeed;
   }

   /**
    * Generates an array of block states representing the colored clay bands in Badlands biomes.
    * The bands are then sampled via {@link #getBand(int, int, int)}, which additionally offsets the bands a little
    * vertically based on the local x and z position.
    */
   protected void generateBands(long pSeed) {
      this.clayBands = new BlockState[64];
      Arrays.fill(this.clayBands, TERRACOTTA);
      WorldgenRandom worldgenrandom = new WorldgenRandom(pSeed);
      this.clayBandsOffsetNoise = new PerlinSimplexNoise(worldgenrandom, ImmutableList.of(0));

      for(int l1 = 0; l1 < 64; ++l1) {
         l1 += worldgenrandom.nextInt(5) + 1;
         if (l1 < 64) {
            this.clayBands[l1] = ORANGE_TERRACOTTA;
         }
      }

      int i2 = worldgenrandom.nextInt(4) + 2;

      for(int i = 0; i < i2; ++i) {
         int j = worldgenrandom.nextInt(3) + 1;
         int k = worldgenrandom.nextInt(64);

         for(int l = 0; k + l < 64 && l < j; ++l) {
            this.clayBands[k + l] = YELLOW_TERRACOTTA;
         }
      }

      int j2 = worldgenrandom.nextInt(4) + 2;

      for(int k2 = 0; k2 < j2; ++k2) {
         int i3 = worldgenrandom.nextInt(3) + 2;
         int l3 = worldgenrandom.nextInt(64);

         for(int i1 = 0; l3 + i1 < 64 && i1 < i3; ++i1) {
            this.clayBands[l3 + i1] = BROWN_TERRACOTTA;
         }
      }

      int l2 = worldgenrandom.nextInt(4) + 2;

      for(int j3 = 0; j3 < l2; ++j3) {
         int i4 = worldgenrandom.nextInt(3) + 1;
         int k4 = worldgenrandom.nextInt(64);

         for(int j1 = 0; k4 + j1 < 64 && j1 < i4; ++j1) {
            this.clayBands[k4 + j1] = RED_TERRACOTTA;
         }
      }

      int k3 = worldgenrandom.nextInt(3) + 3;
      int j4 = 0;

      for(int l4 = 0; l4 < k3; ++l4) {
         int i5 = 1;
         j4 += worldgenrandom.nextInt(16) + 4;

         for(int k1 = 0; j4 + k1 < 64 && k1 < 1; ++k1) {
            this.clayBands[j4 + k1] = WHITE_TERRACOTTA;
            if (j4 + k1 > 1 && worldgenrandom.nextBoolean()) {
               this.clayBands[j4 + k1 - 1] = LIGHT_GRAY_TERRACOTTA;
            }

            if (j4 + k1 < 63 && worldgenrandom.nextBoolean()) {
               this.clayBands[j4 + k1 + 1] = LIGHT_GRAY_TERRACOTTA;
            }
         }
      }

   }

   /**
    * Samples the clay band at the given position.
    */
   protected BlockState getBand(int pX, int pY, int pZ) {
      int i = (int)Math.round(this.clayBandsOffsetNoise.getValue((double)pX / 512.0D, (double)pZ / 512.0D, false) * 2.0D);
      return this.clayBands[(pY + i + 64) % 64];
   }
}