package net.minecraft.world.level.levelgen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.minecraft.world.level.levelgen.synth.SurfaceNoise;

public class NoiseBasedChunkGenerator extends ChunkGenerator {
   public static final Codec<NoiseBasedChunkGenerator> CODEC = RecordCodecBuilder.create((p_64405_) -> {
      return p_64405_.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((p_158489_) -> {
         return p_158489_.biomeSource;
      }), Codec.LONG.fieldOf("seed").stable().forGetter((p_158487_) -> {
         return p_158487_.seed;
      }), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((p_158458_) -> {
         return p_158458_.settings;
      })).apply(p_64405_, p_64405_.stable(NoiseBasedChunkGenerator::new));
   });
   private static final BlockState AIR = Blocks.AIR.defaultBlockState();
   private static final BlockState[] EMPTY_COLUMN = new BlockState[0];
   protected final int cellHeight;
   protected final int cellWidth;
   protected final int cellCountX;
   protected final int cellCountY;
   protected final int cellCountZ;
   protected final SurfaceNoise surfaceNoise;
   protected final NormalNoise barrierNoise;
   protected final NormalNoise waterLevelNoise;
   protected final NormalNoise lavaNoise;
   protected final BlockState defaultBlock;
   protected final BlockState defaultFluid;
   protected final long seed;
   protected final Supplier<NoiseGeneratorSettings> settings;
   protected final int height;
   protected final NoiseSampler sampler;
   protected final BaseStoneSource baseStoneSource;
   protected final OreVeinifier oreVeinifier;
   protected final NoodleCavifier noodleCavifier;

   public NoiseBasedChunkGenerator(BiomeSource p_64337_, long p_64338_, Supplier<NoiseGeneratorSettings> p_64339_) {
      this(p_64337_, p_64337_, p_64338_, p_64339_);
   }

   private NoiseBasedChunkGenerator(BiomeSource pBiomeSource, BiomeSource pRuntimeBiomeSource, long pSeed, Supplier<NoiseGeneratorSettings> pSettings) {
      super(pBiomeSource, pRuntimeBiomeSource, pSettings.get().structureSettings(), pSeed);
      this.seed = pSeed;
      NoiseGeneratorSettings noisegeneratorsettings = pSettings.get();
      this.settings = pSettings;
      NoiseSettings noisesettings = noisegeneratorsettings.noiseSettings();
      this.height = noisesettings.height();
      this.cellHeight = QuartPos.toBlock(noisesettings.noiseSizeVertical());
      this.cellWidth = QuartPos.toBlock(noisesettings.noiseSizeHorizontal());
      this.defaultBlock = noisegeneratorsettings.getDefaultBlock();
      this.defaultFluid = noisegeneratorsettings.getDefaultFluid();
      this.cellCountX = 16 / this.cellWidth;
      this.cellCountY = noisesettings.height() / this.cellHeight;
      this.cellCountZ = 16 / this.cellWidth;
      WorldgenRandom worldgenrandom = new WorldgenRandom(pSeed);
      BlendedNoise blendednoise = new BlendedNoise(worldgenrandom);
      this.surfaceNoise = (SurfaceNoise)(noisesettings.useSimplexSurfaceNoise() ? new PerlinSimplexNoise(worldgenrandom, IntStream.rangeClosed(-3, 0)) : new PerlinNoise(worldgenrandom, IntStream.rangeClosed(-3, 0)));
      worldgenrandom.consumeCount(2620);
      PerlinNoise perlinnoise = new PerlinNoise(worldgenrandom, IntStream.rangeClosed(-15, 0));
      SimplexNoise simplexnoise;
      if (noisesettings.islandNoiseOverride()) {
         WorldgenRandom worldgenrandom1 = new WorldgenRandom(pSeed);
         worldgenrandom1.consumeCount(17292);
         simplexnoise = new SimplexNoise(worldgenrandom1);
      } else {
         simplexnoise = null;
      }

      this.barrierNoise = NormalNoise.create(new SimpleRandomSource(worldgenrandom.nextLong()), -3, 1.0D);
      this.waterLevelNoise = NormalNoise.create(new SimpleRandomSource(worldgenrandom.nextLong()), -3, 1.0D, 0.0D, 2.0D);
      this.lavaNoise = NormalNoise.create(new SimpleRandomSource(worldgenrandom.nextLong()), -1, 1.0D, 0.0D);
      NoiseModifier noisemodifier;
      if (noisegeneratorsettings.isNoiseCavesEnabled()) {
         noisemodifier = new Cavifier(worldgenrandom, noisesettings.minY() / this.cellHeight);
      } else {
         noisemodifier = NoiseModifier.PASSTHROUGH;
      }

      this.sampler = new NoiseSampler(pBiomeSource, this.cellWidth, this.cellHeight, this.cellCountY, noisesettings, blendednoise, simplexnoise, perlinnoise, noisemodifier);
      this.baseStoneSource = new DepthBasedReplacingBaseStoneSource(pSeed, this.defaultBlock, Blocks.DEEPSLATE.defaultBlockState(), noisegeneratorsettings);
      this.oreVeinifier = new OreVeinifier(pSeed, this.defaultBlock, this.cellWidth, this.cellHeight, noisegeneratorsettings.noiseSettings().minY());
      this.noodleCavifier = new NoodleCavifier(pSeed);
   }

   private boolean isAquifersEnabled() {
      return this.settings.get().isAquifersEnabled();
   }

   protected Codec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public ChunkGenerator withSeed(long pSeed) {
      return new NoiseBasedChunkGenerator(this.biomeSource.withSeed(pSeed), pSeed, this.settings);
   }

   public boolean stable(long pSeed, ResourceKey<NoiseGeneratorSettings> pSettingsKey) {
      return this.seed == pSeed && this.settings.get().stable(pSettingsKey);
   }

   private double[] makeAndFillNoiseColumn(int pCellX, int pCellZ, int pMinCellY, int pCellCountY) {
      double[] adouble = new double[pCellCountY + 1];
      this.fillNoiseColumn(adouble, pCellX, pCellZ, pMinCellY, pCellCountY);
      return adouble;
   }

   protected void fillNoiseColumn(double[] p_158467_, int p_158468_, int p_158469_, int p_158470_, int p_158471_) {
      NoiseSettings noisesettings = this.settings.get().noiseSettings();
      this.sampler.fillNoiseColumn(p_158467_, p_158468_, p_158469_, noisesettings, this.getSeaLevel(), p_158470_, p_158471_);
   }

   public int getBaseHeight(int pX, int pZ, Heightmap.Types pType, LevelHeightAccessor pLevel) {
      int i = Math.max(this.settings.get().noiseSettings().minY(), pLevel.getMinBuildHeight());
      int j = Math.min(this.settings.get().noiseSettings().minY() + this.settings.get().noiseSettings().height(), pLevel.getMaxBuildHeight());
      int k = Mth.intFloorDiv(i, this.cellHeight);
      int l = Mth.intFloorDiv(j - i, this.cellHeight);
      return l <= 0 ? pLevel.getMinBuildHeight() : this.iterateNoiseColumn(pX, pZ, (BlockState[])null, pType.isOpaque(), k, l).orElse(pLevel.getMinBuildHeight());
   }

   public NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor pLevel) {
      int i = Math.max(this.settings.get().noiseSettings().minY(), pLevel.getMinBuildHeight());
      int j = Math.min(this.settings.get().noiseSettings().minY() + this.settings.get().noiseSettings().height(), pLevel.getMaxBuildHeight());
      int k = Mth.intFloorDiv(i, this.cellHeight);
      int l = Mth.intFloorDiv(j - i, this.cellHeight);
      if (l <= 0) {
         return new NoiseColumn(i, EMPTY_COLUMN);
      } else {
         BlockState[] ablockstate = new BlockState[l * this.cellHeight];
         this.iterateNoiseColumn(pX, pZ, ablockstate, (Predicate<BlockState>)null, k, l);
         return new NoiseColumn(i, ablockstate);
      }
   }

   public BaseStoneSource getBaseStoneSource() {
      return this.baseStoneSource;
   }

   protected OptionalInt iterateNoiseColumn(int pX, int pZ, @Nullable BlockState[] pColumn, @Nullable Predicate<BlockState> pStoppingState, int pMinCellY, int pCellCountY) {
      int i = SectionPos.blockToSectionCoord(pX);
      int j = SectionPos.blockToSectionCoord(pZ);
      int k = Math.floorDiv(pX, this.cellWidth);
      int l = Math.floorDiv(pZ, this.cellWidth);
      int i1 = Math.floorMod(pX, this.cellWidth);
      int j1 = Math.floorMod(pZ, this.cellWidth);
      double d0 = (double)i1 / (double)this.cellWidth;
      double d1 = (double)j1 / (double)this.cellWidth;
      double[][] adouble = new double[][]{this.makeAndFillNoiseColumn(k, l, pMinCellY, pCellCountY), this.makeAndFillNoiseColumn(k, l + 1, pMinCellY, pCellCountY), this.makeAndFillNoiseColumn(k + 1, l, pMinCellY, pCellCountY), this.makeAndFillNoiseColumn(k + 1, l + 1, pMinCellY, pCellCountY)};
      Aquifer aquifer = this.getAquifer(pMinCellY, pCellCountY, new ChunkPos(i, j));

      for(int k1 = pCellCountY - 1; k1 >= 0; --k1) {
         double d2 = adouble[0][k1];
         double d3 = adouble[1][k1];
         double d4 = adouble[2][k1];
         double d5 = adouble[3][k1];
         double d6 = adouble[0][k1 + 1];
         double d7 = adouble[1][k1 + 1];
         double d8 = adouble[2][k1 + 1];
         double d9 = adouble[3][k1 + 1];

         for(int l1 = this.cellHeight - 1; l1 >= 0; --l1) {
            double d10 = (double)l1 / (double)this.cellHeight;
            double d11 = Mth.lerp3(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
            int i2 = k1 * this.cellHeight + l1;
            int j2 = i2 + pMinCellY * this.cellHeight;
            BlockState blockstate = this.updateNoiseAndGenerateBaseState(Beardifier.NO_BEARDS, aquifer, this.baseStoneSource, NoiseModifier.PASSTHROUGH, pX, j2, pZ, d11);
            if (pColumn != null) {
               pColumn[i2] = blockstate;
            }

            if (pStoppingState != null && pStoppingState.test(blockstate)) {
               return OptionalInt.of(j2 + 1);
            }
         }
      }

      return OptionalInt.empty();
   }

   protected Aquifer getAquifer(int pMinCellY, int pCellCountY, ChunkPos pChunkPos) {
      return !this.isAquifersEnabled() ? Aquifer.createDisabled(this.getSeaLevel(), this.defaultFluid) : Aquifer.create(pChunkPos, this.barrierNoise, this.waterLevelNoise, this.lavaNoise, this.settings.get(), this.sampler, pMinCellY * this.cellHeight, pCellCountY * this.cellHeight);
   }

   protected BlockState updateNoiseAndGenerateBaseState(Beardifier pBeardifier, Aquifer pAquifer, BaseStoneSource pBaseStoneSource, NoiseModifier pCaveNoiseModifier, int pX, int pY, int pZ, double pNoise) {
      double d0 = Mth.clamp(pNoise / 200.0D, -1.0D, 1.0D);
      d0 = d0 / 2.0D - d0 * d0 * d0 / 24.0D;
      d0 = pCaveNoiseModifier.modifyNoise(d0, pX, pY, pZ);
      d0 = d0 + pBeardifier.beardifyOrBury(pX, pY, pZ);
      return pAquifer.computeState(pBaseStoneSource, pX, pY, pZ, d0);
   }

   /**
    * Generate the SURFACE part of a chunk
    */
   public void buildSurfaceAndBedrock(WorldGenRegion pLevel, ChunkAccess pChunk) {
      ChunkPos chunkpos = pChunk.getPos();
      int i = chunkpos.x;
      int j = chunkpos.z;
      WorldgenRandom worldgenrandom = new WorldgenRandom();
      worldgenrandom.setBaseChunkSeed(i, j);
      ChunkPos chunkpos1 = pChunk.getPos();
      int k = chunkpos1.getMinBlockX();
      int l = chunkpos1.getMinBlockZ();
      double d0 = 0.0625D;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i1 = 0; i1 < 16; ++i1) {
         for(int j1 = 0; j1 < 16; ++j1) {
            int k1 = k + i1;
            int l1 = l + j1;
            int i2 = pChunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, i1, j1) + 1;
            double d1 = this.surfaceNoise.getSurfaceNoiseValue((double)k1 * 0.0625D, (double)l1 * 0.0625D, 0.0625D, (double)i1 * 0.0625D) * 15.0D;
            int j2 = this.settings.get().getMinSurfaceLevel();
            pLevel.getBiome(blockpos$mutableblockpos.set(k + i1, i2, l + j1)).buildSurfaceAt(worldgenrandom, pChunk, k1, l1, i2, d1, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), j2, pLevel.getSeed());
         }
      }

      this.setBedrock(pChunk, worldgenrandom);
   }

   protected void setBedrock(ChunkAccess pChunk, Random pRandom) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i = pChunk.getPos().getMinBlockX();
      int j = pChunk.getPos().getMinBlockZ();
      NoiseGeneratorSettings noisegeneratorsettings = this.settings.get();
      int k = noisegeneratorsettings.noiseSettings().minY();
      int l = k + noisegeneratorsettings.getBedrockFloorPosition();
      int i1 = this.height - 1 + k - noisegeneratorsettings.getBedrockRoofPosition();
      int j1 = 5;
      int k1 = pChunk.getMinBuildHeight();
      int l1 = pChunk.getMaxBuildHeight();
      boolean flag = i1 + 5 - 1 >= k1 && i1 < l1;
      boolean flag1 = l + 5 - 1 >= k1 && l < l1;
      if (flag || flag1) {
         for(BlockPos blockpos : BlockPos.betweenClosed(i, 0, j, i + 15, 0, j + 15)) {
            if (flag) {
               for(int i2 = 0; i2 < 5; ++i2) {
                  if (i2 <= pRandom.nextInt(5)) {
                     pChunk.setBlockState(blockpos$mutableblockpos.set(blockpos.getX(), i1 - i2, blockpos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                  }
               }
            }

            if (flag1) {
               for(int j2 = 4; j2 >= 0; --j2) {
                  if (j2 <= pRandom.nextInt(5)) {
                     pChunk.setBlockState(blockpos$mutableblockpos.set(blockpos.getX(), l + j2, blockpos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
                  }
               }
            }
         }

      }
   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Executor pExecutor, StructureFeatureManager pStructureFeatureManager, ChunkAccess pChunk) {
      NoiseSettings noisesettings = this.settings.get().noiseSettings();
      int i = Math.max(noisesettings.minY(), pChunk.getMinBuildHeight());
      int j = Math.min(noisesettings.minY() + noisesettings.height(), pChunk.getMaxBuildHeight());
      int k = Mth.intFloorDiv(i, this.cellHeight);
      int l = Mth.intFloorDiv(j - i, this.cellHeight);
      if (l <= 0) {
         return CompletableFuture.completedFuture(pChunk);
      } else {
         int i1 = pChunk.getSectionIndex(l * this.cellHeight - 1 + i);
         int j1 = pChunk.getSectionIndex(i);
         return CompletableFuture.supplyAsync(() -> {
            Set<LevelChunkSection> set = Sets.newHashSet();

            ChunkAccess chunkaccess;
            try {
               for(int k1 = i1; k1 >= j1; --k1) {
                  LevelChunkSection levelchunksection = pChunk.getOrCreateSection(k1);
                  levelchunksection.acquire();
                  set.add(levelchunksection);
               }

               chunkaccess = this.doFill(pStructureFeatureManager, pChunk, k, l);
            } finally {
               for(LevelChunkSection levelchunksection1 : set) {
                  levelchunksection1.release();
               }

            }

            return chunkaccess;
         }, Util.backgroundExecutor());
      }
   }

   protected ChunkAccess doFill(StructureFeatureManager pStructureManager, ChunkAccess pChunk, int pMinCellY, int pCellCountY) {
      Heightmap heightmap = pChunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap heightmap1 = pChunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
      ChunkPos chunkpos = pChunk.getPos();
      int i = chunkpos.getMinBlockX();
      int j = chunkpos.getMinBlockZ();
      Beardifier beardifier = new Beardifier(pStructureManager, pChunk);
      Aquifer aquifer = this.getAquifer(pMinCellY, pCellCountY, chunkpos);
      NoiseInterpolator noiseinterpolator = new NoiseInterpolator(this.cellCountX, pCellCountY, this.cellCountZ, chunkpos, pMinCellY, this::fillNoiseColumn);
      List<NoiseInterpolator> list = Lists.newArrayList(noiseinterpolator);
      Consumer<NoiseInterpolator> consumer = list::add;
      DoubleFunction<BaseStoneSource> doublefunction = this.createBaseStoneSource(pMinCellY, chunkpos, consumer);
      DoubleFunction<NoiseModifier> doublefunction1 = this.createCaveNoiseModifier(pMinCellY, chunkpos, consumer);
      list.forEach(NoiseInterpolator::initializeForFirstCellX);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int k = 0; k < this.cellCountX; ++k) {
         int l = k;
         list.forEach((p_158426_) -> {
            p_158426_.advanceCellX(l);
         });

         for(int i1 = 0; i1 < this.cellCountZ; ++i1) {
            LevelChunkSection levelchunksection = pChunk.getOrCreateSection(pChunk.getSectionsCount() - 1);

            for(int j1 = pCellCountY - 1; j1 >= 0; --j1) {
               int k1 = i1;
               int l1 = j1;
               list.forEach((p_158412_) -> {
                  p_158412_.selectCellYZ(l1, k1);
               });

               for(int i2 = this.cellHeight - 1; i2 >= 0; --i2) {
                  int j2 = (pMinCellY + j1) * this.cellHeight + i2;
                  int k2 = j2 & 15;
                  int l2 = pChunk.getSectionIndex(j2);
                  if (pChunk.getSectionIndex(levelchunksection.bottomBlockY()) != l2) {
                     levelchunksection = pChunk.getOrCreateSection(l2);
                  }

                  double d0 = (double)i2 / (double)this.cellHeight;
                  list.forEach((p_158476_) -> {
                     p_158476_.updateForY(d0);
                  });

                  for(int i3 = 0; i3 < this.cellWidth; ++i3) {
                     int j3 = i + k * this.cellWidth + i3;
                     int k3 = j3 & 15;
                     double d1 = (double)i3 / (double)this.cellWidth;
                     list.forEach((p_158390_) -> {
                        p_158390_.updateForX(d1);
                     });

                     for(int l3 = 0; l3 < this.cellWidth; ++l3) {
                        int i4 = j + i1 * this.cellWidth + l3;
                        int j4 = i4 & 15;
                        double d2 = (double)l3 / (double)this.cellWidth;
                        double d3 = noiseinterpolator.calculateValue(d2);
                        BlockState blockstate = this.updateNoiseAndGenerateBaseState(beardifier, aquifer, doublefunction.apply(d2), doublefunction1.apply(d2), j3, j2, i4, d3);
                        if (blockstate != AIR) {
                           if (blockstate.getLightEmission() != 0 && pChunk instanceof ProtoChunk) {
                              blockpos$mutableblockpos.set(j3, j2, i4);
                              ((ProtoChunk)pChunk).addLight(blockpos$mutableblockpos);
                           }

                           levelchunksection.setBlockState(k3, k2, j4, blockstate, false);
                           heightmap.update(k3, j2, j4, blockstate);
                           heightmap1.update(k3, j2, j4, blockstate);
                           if (aquifer.shouldScheduleFluidUpdate() && !blockstate.getFluidState().isEmpty()) {
                              blockpos$mutableblockpos.set(j3, j2, i4);
                              pChunk.getLiquidTicks().scheduleTick(blockpos$mutableblockpos, blockstate.getFluidState().getType(), 0);
                           }
                        }
                     }
                  }
               }
            }
         }

         list.forEach(NoiseInterpolator::swapSlices);
      }

      return pChunk;
   }

   protected DoubleFunction<NoiseModifier> createCaveNoiseModifier(int pMinCellY, ChunkPos pChunkPos, Consumer<NoiseInterpolator> pInterpolators) {
      if (!this.settings.get().isNoodleCavesEnabled()) {
         return (p_158473_) -> {
            return NoiseModifier.PASSTHROUGH;
         };
      } else {
         NoiseBasedChunkGenerator.NoodleCaveNoiseModifier noisebasedchunkgenerator$noodlecavenoisemodifier = new NoiseBasedChunkGenerator.NoodleCaveNoiseModifier(pChunkPos, pMinCellY);
         noisebasedchunkgenerator$noodlecavenoisemodifier.listInterpolators(pInterpolators);
         return noisebasedchunkgenerator$noodlecavenoisemodifier::prepare;
      }
   }

   protected DoubleFunction<BaseStoneSource> createBaseStoneSource(int pMinCellY, ChunkPos pChunkPos, Consumer<NoiseInterpolator> pInterpolators) {
      if (!this.settings.get().isOreVeinsEnabled()) {
         return (p_158387_) -> {
            return this.baseStoneSource;
         };
      } else {
         NoiseBasedChunkGenerator.OreVeinNoiseSource noisebasedchunkgenerator$oreveinnoisesource = new NoiseBasedChunkGenerator.OreVeinNoiseSource(pChunkPos, pMinCellY, this.seed + 1L);
         noisebasedchunkgenerator$oreveinnoisesource.listInterpolators(pInterpolators);
         BaseStoneSource basestonesource = (p_158450_, p_158451_, p_158452_) -> {
            BlockState blockstate = noisebasedchunkgenerator$oreveinnoisesource.getBaseBlock(p_158450_, p_158451_, p_158452_);
            return blockstate != this.defaultBlock ? blockstate : this.baseStoneSource.getBaseBlock(p_158450_, p_158451_, p_158452_);
         };
         return (p_158456_) -> {
            noisebasedchunkgenerator$oreveinnoisesource.prepare(p_158456_);
            return basestonesource;
         };
      }
   }

   protected Aquifer createAquifer(ChunkAccess pChunk) {
      ChunkPos chunkpos = pChunk.getPos();
      int i = Math.max(this.settings.get().noiseSettings().minY(), pChunk.getMinBuildHeight());
      int j = Mth.intFloorDiv(i, this.cellHeight);
      return this.getAquifer(j, this.cellCountY, chunkpos);
   }

   public int getGenDepth() {
      return this.height;
   }

   public int getSeaLevel() {
      return this.settings.get().seaLevel();
   }

   public int getMinY() {
      return this.settings.get().noiseSettings().minY();
   }

   public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Biome pBiome, StructureFeatureManager pStructureFeatureManager, MobCategory pCategory, BlockPos pPos) {
      WeightedRandomList<MobSpawnSettings.SpawnerData> spawns = net.minecraftforge.common.world.StructureSpawnManager.getStructureSpawns(pStructureFeatureManager, pCategory, pPos);
      if (spawns != null) return spawns;
      if (false) {//Forge: We handle these hardcoded cases above in StructureSpawnManager#getStructureSpawns, but allow for insideOnly to be changed and allow for creatures to be spawned in ones other than just the witch hut
      if (pStructureFeatureManager.getStructureAt(pPos, true, StructureFeature.SWAMP_HUT).isValid()) {
         if (pCategory == MobCategory.MONSTER) {
            return StructureFeature.SWAMP_HUT.getSpecialEnemies();
         }

         if (pCategory == MobCategory.CREATURE) {
            return StructureFeature.SWAMP_HUT.getSpecialAnimals();
         }
      }

      if (pCategory == MobCategory.MONSTER) {
         if (pStructureFeatureManager.getStructureAt(pPos, false, StructureFeature.PILLAGER_OUTPOST).isValid()) {
            return StructureFeature.PILLAGER_OUTPOST.getSpecialEnemies();
         }

         if (pStructureFeatureManager.getStructureAt(pPos, false, StructureFeature.OCEAN_MONUMENT).isValid()) {
            return StructureFeature.OCEAN_MONUMENT.getSpecialEnemies();
         }

         if (pStructureFeatureManager.getStructureAt(pPos, true, StructureFeature.NETHER_BRIDGE).isValid()) {
            return StructureFeature.NETHER_BRIDGE.getSpecialEnemies();
         }
      }
      }

      return pCategory == MobCategory.UNDERGROUND_WATER_CREATURE && pStructureFeatureManager.getStructureAt(pPos, false, StructureFeature.OCEAN_MONUMENT).isValid() ? StructureFeature.OCEAN_MONUMENT.getSpecialUndergroundWaterAnimals() : super.getMobsAt(pBiome, pStructureFeatureManager, pCategory, pPos);
   }

   public void spawnOriginalMobs(WorldGenRegion pLevel) {
      if (!this.settings.get().disableMobGeneration()) {
         ChunkPos chunkpos = pLevel.getCenter();
         Biome biome = pLevel.getBiome(chunkpos.getWorldPosition());
         WorldgenRandom worldgenrandom = new WorldgenRandom();
         worldgenrandom.setDecorationSeed(pLevel.getSeed(), chunkpos.getMinBlockX(), chunkpos.getMinBlockZ());
         NaturalSpawner.spawnMobsForChunkGeneration(pLevel, biome, chunkpos, worldgenrandom);
      }
   }

   public class NoodleCaveNoiseModifier implements NoiseModifier {
      private final NoiseInterpolator toggle;
      private final NoiseInterpolator thickness;
      private final NoiseInterpolator ridgeA;
      private final NoiseInterpolator ridgeB;
      private double factorZ;

      public NoodleCaveNoiseModifier(ChunkPos p_158501_, int p_158502_) {
         this.toggle = new NoiseInterpolator(NoiseBasedChunkGenerator.this.cellCountX, NoiseBasedChunkGenerator.this.cellCountY, NoiseBasedChunkGenerator.this.cellCountZ, p_158501_, p_158502_, NoiseBasedChunkGenerator.this.noodleCavifier::fillToggleNoiseColumn);
         this.thickness = new NoiseInterpolator(NoiseBasedChunkGenerator.this.cellCountX, NoiseBasedChunkGenerator.this.cellCountY, NoiseBasedChunkGenerator.this.cellCountZ, p_158501_, p_158502_, NoiseBasedChunkGenerator.this.noodleCavifier::fillThicknessNoiseColumn);
         this.ridgeA = new NoiseInterpolator(NoiseBasedChunkGenerator.this.cellCountX, NoiseBasedChunkGenerator.this.cellCountY, NoiseBasedChunkGenerator.this.cellCountZ, p_158501_, p_158502_, NoiseBasedChunkGenerator.this.noodleCavifier::fillRidgeANoiseColumn);
         this.ridgeB = new NoiseInterpolator(NoiseBasedChunkGenerator.this.cellCountX, NoiseBasedChunkGenerator.this.cellCountY, NoiseBasedChunkGenerator.this.cellCountZ, p_158501_, p_158502_, NoiseBasedChunkGenerator.this.noodleCavifier::fillRidgeBNoiseColumn);
      }

      public NoiseModifier prepare(double pFactorZ) {
         this.factorZ = pFactorZ;
         return this;
      }

      /**
       * Modifies the passed in noise value, at the given coordinates.
       * 
       * Note: in most uses of this function, these coordinates are ordered (x, y, z). However, notably, {@link
       * Cavifier} expects them in the order (y, z, x) despite implementing this interface.
       */
      public double modifyNoise(double pNoise, int pX, int pY, int pZ) {
         double d0 = this.toggle.calculateValue(this.factorZ);
         double d1 = this.thickness.calculateValue(this.factorZ);
         double d2 = this.ridgeA.calculateValue(this.factorZ);
         double d3 = this.ridgeB.calculateValue(this.factorZ);
         return NoiseBasedChunkGenerator.this.noodleCavifier.noodleCavify(pNoise, pX, pY, pZ, d0, d1, d2, d3, NoiseBasedChunkGenerator.this.getMinY());
      }

      public void listInterpolators(Consumer<NoiseInterpolator> pInterpolators) {
         pInterpolators.accept(this.toggle);
         pInterpolators.accept(this.thickness);
         pInterpolators.accept(this.ridgeA);
         pInterpolators.accept(this.ridgeB);
      }
   }

   public class OreVeinNoiseSource implements BaseStoneSource {
      private final NoiseInterpolator veininess;
      private final NoiseInterpolator veinA;
      private final NoiseInterpolator veinB;
      private double factorZ;
      private final long seed;
      private final WorldgenRandom random = new WorldgenRandom();

      public OreVeinNoiseSource(ChunkPos p_158521_, int p_158522_, long p_158523_) {
         this.veininess = new NoiseInterpolator(NoiseBasedChunkGenerator.this.cellCountX, NoiseBasedChunkGenerator.this.cellCountY, NoiseBasedChunkGenerator.this.cellCountZ, p_158521_, p_158522_, NoiseBasedChunkGenerator.this.oreVeinifier::fillVeininessNoiseColumn);
         this.veinA = new NoiseInterpolator(NoiseBasedChunkGenerator.this.cellCountX, NoiseBasedChunkGenerator.this.cellCountY, NoiseBasedChunkGenerator.this.cellCountZ, p_158521_, p_158522_, NoiseBasedChunkGenerator.this.oreVeinifier::fillNoiseColumnA);
         this.veinB = new NoiseInterpolator(NoiseBasedChunkGenerator.this.cellCountX, NoiseBasedChunkGenerator.this.cellCountY, NoiseBasedChunkGenerator.this.cellCountZ, p_158521_, p_158522_, NoiseBasedChunkGenerator.this.oreVeinifier::fillNoiseColumnB);
         this.seed = p_158523_;
      }

      public void listInterpolators(Consumer<NoiseInterpolator> pInterpolators) {
         pInterpolators.accept(this.veininess);
         pInterpolators.accept(this.veinA);
         pInterpolators.accept(this.veinB);
      }

      public void prepare(double pFactorZ) {
         this.factorZ = pFactorZ;
      }

      public BlockState getBaseBlock(int pX, int pY, int pZ) {
         double d0 = this.veininess.calculateValue(this.factorZ);
         double d1 = this.veinA.calculateValue(this.factorZ);
         double d2 = this.veinB.calculateValue(this.factorZ);
         this.random.setBaseStoneSeed(this.seed, pX, pY, pZ);
         return NoiseBasedChunkGenerator.this.oreVeinifier.oreVeinify(this.random, pX, pY, pZ, d0, d1, d2);
      }
   }
}
