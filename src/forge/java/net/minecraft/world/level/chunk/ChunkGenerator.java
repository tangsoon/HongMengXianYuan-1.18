package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.BaseStoneSource;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.SingleBaseStoneSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StrongholdConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public abstract class ChunkGenerator {
   public static final Codec<ChunkGenerator> CODEC = Registry.CHUNK_GENERATOR.dispatchStable(ChunkGenerator::codec, Function.identity());
   protected final BiomeSource biomeSource;
   protected final BiomeSource runtimeBiomeSource;
   private final StructureSettings settings;
   private final long strongholdSeed;
   private final List<ChunkPos> strongholdPositions = Lists.newArrayList();
   private final BaseStoneSource defaultBaseStoneSource;

   public ChunkGenerator(BiomeSource pBiomeSource, StructureSettings pSettings) {
      this(pBiomeSource, pBiomeSource, pSettings, 0L);
   }

   public ChunkGenerator(BiomeSource pBiomeSource, BiomeSource pRuntimeBiomeSource, StructureSettings pSettings, long pStrongholdSeed) {
      this.biomeSource = pBiomeSource;
      this.runtimeBiomeSource = pRuntimeBiomeSource;
      this.settings = pSettings;
      this.strongholdSeed = pStrongholdSeed;
      this.defaultBaseStoneSource = new SingleBaseStoneSource(Blocks.STONE.defaultBlockState());
   }

   private void generateStrongholds() {
      if (this.strongholdPositions.isEmpty()) {
         StrongholdConfiguration strongholdconfiguration = this.settings.stronghold();
         if (strongholdconfiguration != null && strongholdconfiguration.count() != 0) {
            List<Biome> list = Lists.newArrayList();

            for(Biome biome : this.biomeSource.possibleBiomes()) {
               if (biome.getGenerationSettings().isValidStart(StructureFeature.STRONGHOLD)) {
                  list.add(biome);
               }
            }

            int k1 = strongholdconfiguration.distance();
            int l1 = strongholdconfiguration.count();
            int i = strongholdconfiguration.spread();
            Random random = new Random();
            random.setSeed(this.strongholdSeed);
            double d0 = random.nextDouble() * Math.PI * 2.0D;
            int j = 0;
            int k = 0;

            for(int l = 0; l < l1; ++l) {
               double d1 = (double)(4 * k1 + k1 * k * 6) + (random.nextDouble() - 0.5D) * (double)k1 * 2.5D;
               int i1 = (int)Math.round(Math.cos(d0) * d1);
               int j1 = (int)Math.round(Math.sin(d0) * d1);
               BlockPos blockpos = this.biomeSource.findBiomeHorizontal(SectionPos.sectionToBlockCoord(i1, 8), 0, SectionPos.sectionToBlockCoord(j1, 8), 112, list::contains, random);
               if (blockpos != null) {
                  i1 = SectionPos.blockToSectionCoord(blockpos.getX());
                  j1 = SectionPos.blockToSectionCoord(blockpos.getZ());
               }

               this.strongholdPositions.add(new ChunkPos(i1, j1));
               d0 += (Math.PI * 2D) / (double)i;
               ++j;
               if (j == i) {
                  ++k;
                  j = 0;
                  i = i + 2 * i / (k + 1);
                  i = Math.min(i, l1 - l);
                  d0 += random.nextDouble() * Math.PI * 2.0D;
               }
            }

         }
      }
   }

   protected abstract Codec<? extends ChunkGenerator> codec();

   public abstract ChunkGenerator withSeed(long pSeed);

   public void createBiomes(Registry<Biome> pBiomes, ChunkAccess pChunk) {
      ChunkPos chunkpos = pChunk.getPos();
      ((ProtoChunk)pChunk).setBiomes(new ChunkBiomeContainer(pBiomes, pChunk, chunkpos, this.runtimeBiomeSource));
   }

   public void applyCarvers(long pSeed, BiomeManager pBiomeManager, ChunkAccess pChunk, GenerationStep.Carving pStep) {
      BiomeManager biomemanager = pBiomeManager.withDifferentSource(this.biomeSource);
      WorldgenRandom worldgenrandom = new WorldgenRandom();
      int i = 8;
      ChunkPos chunkpos = pChunk.getPos();
      CarvingContext carvingcontext = new CarvingContext(this, pChunk);
      Aquifer aquifer = this.createAquifer(pChunk);
      BitSet bitset = ((ProtoChunk)pChunk).getOrCreateCarvingMask(pStep);

      for(int j = -8; j <= 8; ++j) {
         for(int k = -8; k <= 8; ++k) {
            ChunkPos chunkpos1 = new ChunkPos(chunkpos.x + j, chunkpos.z + k);
            BiomeGenerationSettings biomegenerationsettings = this.biomeSource.getNoiseBiome(QuartPos.fromBlock(chunkpos1.getMinBlockX()), 0, QuartPos.fromBlock(chunkpos1.getMinBlockZ())).getGenerationSettings();
            List<Supplier<ConfiguredWorldCarver<?>>> list = biomegenerationsettings.getCarvers(pStep);
            ListIterator<Supplier<ConfiguredWorldCarver<?>>> listiterator = list.listIterator();

            while(listiterator.hasNext()) {
               int l = listiterator.nextIndex();
               ConfiguredWorldCarver<?> configuredworldcarver = listiterator.next().get();
               worldgenrandom.setLargeFeatureSeed(pSeed + (long)l, chunkpos1.x, chunkpos1.z);
               if (configuredworldcarver.isStartChunk(worldgenrandom)) {
                  configuredworldcarver.carve(carvingcontext, pChunk, biomemanager::getBiome, worldgenrandom, aquifer, chunkpos1, bitset);
               }
            }
         }
      }

   }

   protected Aquifer createAquifer(ChunkAccess pChunk) {
      return Aquifer.createDisabled(this.getSeaLevel(), Blocks.WATER.defaultBlockState());
   }

   @Nullable
   public BlockPos findNearestMapFeature(ServerLevel pLevel, StructureFeature<?> pStructure, BlockPos pPos, int pSearchRadius, boolean pSkipKnownStructures) {
      if (!this.biomeSource.canGenerateStructure(pStructure)) {
         return null;
      } else if (pStructure == StructureFeature.STRONGHOLD) {
         this.generateStrongholds();
         BlockPos blockpos = null;
         double d0 = Double.MAX_VALUE;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(ChunkPos chunkpos : this.strongholdPositions) {
            blockpos$mutableblockpos.set(SectionPos.sectionToBlockCoord(chunkpos.x, 8), 32, SectionPos.sectionToBlockCoord(chunkpos.z, 8));
            double d1 = blockpos$mutableblockpos.distSqr(pPos);
            if (blockpos == null) {
               blockpos = new BlockPos(blockpos$mutableblockpos);
               d0 = d1;
            } else if (d1 < d0) {
               blockpos = new BlockPos(blockpos$mutableblockpos);
               d0 = d1;
            }
         }

         return blockpos;
      } else {
         StructureFeatureConfiguration structurefeatureconfiguration = this.settings.getConfig(pStructure);
         return structurefeatureconfiguration == null ? null : pStructure.getNearestGeneratedFeature(pLevel, pLevel.structureFeatureManager(), pPos, pSearchRadius, pSkipKnownStructures, pLevel.getSeed(), structurefeatureconfiguration);
      }
   }

   public void applyBiomeDecoration(WorldGenRegion pLevel, StructureFeatureManager pStructureFeatureManager) {
      ChunkPos chunkpos = pLevel.getCenter();
      int i = chunkpos.getMinBlockX();
      int j = chunkpos.getMinBlockZ();
      BlockPos blockpos = new BlockPos(i, pLevel.getMinBuildHeight(), j);
      Biome biome = this.biomeSource.getPrimaryBiome(chunkpos);
      WorldgenRandom worldgenrandom = new WorldgenRandom();
      long k = worldgenrandom.setDecorationSeed(pLevel.getSeed(), i, j);

      try {
         biome.generate(pStructureFeatureManager, this, pLevel, k, worldgenrandom, blockpos);
      } catch (Exception exception) {
         CrashReport crashreport = CrashReport.forThrowable(exception, "Biome decoration");
         crashreport.addCategory("Generation").setDetail("CenterX", chunkpos.x).setDetail("CenterZ", chunkpos.z).setDetail("Seed", k).setDetail("Biome", biome);
         throw new ReportedException(crashreport);
      }
   }

   /**
    * Generate the SURFACE part of a chunk
    */
   public abstract void buildSurfaceAndBedrock(WorldGenRegion pLevel, ChunkAccess pChunk);

   public void spawnOriginalMobs(WorldGenRegion pLevel) {
   }

   public StructureSettings getSettings() {
      return this.settings;
   }

   public int getSpawnHeight(LevelHeightAccessor pLevel) {
      return 64;
   }

   public BiomeSource getBiomeSource() {
      return this.runtimeBiomeSource;
   }

   public int getGenDepth() {
      return 256;
   }

   public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Biome pBiome, StructureFeatureManager pStructureFeatureManager, MobCategory pCategory, BlockPos pPos) {
      return pBiome.getMobSettings().getMobs(pCategory);
   }

   public void createStructures(RegistryAccess pRegistryAccess, StructureFeatureManager pStructureFeatureManager, ChunkAccess pChunk, StructureManager pStructureManager, long pSeed) {
      Biome biome = this.biomeSource.getPrimaryBiome(pChunk.getPos());
      this.createStructure(StructureFeatures.STRONGHOLD, pRegistryAccess, pStructureFeatureManager, pChunk, pStructureManager, pSeed, biome);

      for(Supplier<ConfiguredStructureFeature<?, ?>> supplier : biome.getGenerationSettings().structures()) {
         this.createStructure(supplier.get(), pRegistryAccess, pStructureFeatureManager, pChunk, pStructureManager, pSeed, biome);
      }

   }

   private void createStructure(ConfiguredStructureFeature<?, ?> pFeature, RegistryAccess pRegistryAccess, StructureFeatureManager pStructureFeatureManager, ChunkAccess pChunk, StructureManager pStructureManager, long pSeed, Biome pBiome) {
      ChunkPos chunkpos = pChunk.getPos();
      SectionPos sectionpos = SectionPos.bottomOf(pChunk);
      StructureStart<?> structurestart = pStructureFeatureManager.getStartForFeature(sectionpos, pFeature.feature, pChunk);
      int i = structurestart != null ? structurestart.getReferences() : 0;
      StructureFeatureConfiguration structurefeatureconfiguration = this.settings.getConfig(pFeature.feature);
      if (structurefeatureconfiguration != null) {
         StructureStart<?> structurestart1 = pFeature.generate(pRegistryAccess, this, this.biomeSource, pStructureManager, pSeed, chunkpos, pBiome, i, structurefeatureconfiguration, pChunk);
         pStructureFeatureManager.setStartForFeature(sectionpos, pFeature.feature, structurestart1, pChunk);
      }

   }

   public void createReferences(WorldGenLevel pLevel, StructureFeatureManager pStructureFeatureManager, ChunkAccess pChunk) {
      int i = 8;
      ChunkPos chunkpos = pChunk.getPos();
      int j = chunkpos.x;
      int k = chunkpos.z;
      int l = chunkpos.getMinBlockX();
      int i1 = chunkpos.getMinBlockZ();
      SectionPos sectionpos = SectionPos.bottomOf(pChunk);

      for(int j1 = j - 8; j1 <= j + 8; ++j1) {
         for(int k1 = k - 8; k1 <= k + 8; ++k1) {
            long l1 = ChunkPos.asLong(j1, k1);

            for(StructureStart<?> structurestart : pLevel.getChunk(j1, k1).getAllStarts().values()) {
               try {
                  if (structurestart.isValid() && structurestart.getBoundingBox().intersects(l, i1, l + 15, i1 + 15)) {
                     pStructureFeatureManager.addReferenceForFeature(sectionpos, structurestart.getFeature(), l1, pChunk);
                     DebugPackets.sendStructurePacket(pLevel, structurestart);
                  }
               } catch (Exception exception) {
                  CrashReport crashreport = CrashReport.forThrowable(exception, "Generating structure reference");
                  CrashReportCategory crashreportcategory = crashreport.addCategory("Structure");
                  crashreportcategory.setDetail("Id", () -> {
                     return Registry.STRUCTURE_FEATURE.getKey(structurestart.getFeature()).toString();
                  });
                  crashreportcategory.setDetail("Name", () -> {
                     return structurestart.getFeature().getFeatureName();
                  });
                  crashreportcategory.setDetail("Class", () -> {
                     return structurestart.getFeature().getClass().getCanonicalName();
                  });
                  throw new ReportedException(crashreport);
               }
            }
         }
      }

   }

   public abstract CompletableFuture<ChunkAccess> fillFromNoise(Executor pExecutor, StructureFeatureManager pStructureFeatureManager, ChunkAccess pChunk);

   public int getSeaLevel() {
      return 63;
   }

   public int getMinY() {
      return 0;
   }

   public abstract int getBaseHeight(int pX, int pZ, Heightmap.Types pType, LevelHeightAccessor pLevel);

   public abstract NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor pLevel);

   public int getFirstFreeHeight(int pX, int pZ, Heightmap.Types pType, LevelHeightAccessor pLevel) {
      return this.getBaseHeight(pX, pZ, pType, pLevel);
   }

   public int getFirstOccupiedHeight(int pX, int pZ, Heightmap.Types pType, LevelHeightAccessor pLevel) {
      return this.getBaseHeight(pX, pZ, pType, pLevel) - 1;
   }

   public boolean hasStronghold(ChunkPos pPos) {
      this.generateStrongholds();
      return this.strongholdPositions.contains(pPos);
   }

   public BaseStoneSource getBaseStoneSource() {
      return this.defaultBaseStoneSource;
   }

   static {
      Registry.register(Registry.CHUNK_GENERATOR, "noise", NoiseBasedChunkGenerator.CODEC);
      Registry.register(Registry.CHUNK_GENERATOR, "flat", FlatLevelSource.CODEC);
      Registry.register(Registry.CHUNK_GENERATOR, "debug", DebugLevelSource.CODEC);
   }
}