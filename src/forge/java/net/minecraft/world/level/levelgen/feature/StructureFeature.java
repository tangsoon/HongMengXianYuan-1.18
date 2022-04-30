package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.NetherFossilFeature;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureFeature<C extends FeatureConfiguration> extends net.minecraftforge.registries.ForgeRegistryEntry<StructureFeature<?>> implements net.minecraftforge.common.extensions.IForgeStructureFeature {
   public static final BiMap<String, StructureFeature<?>> STRUCTURES_REGISTRY = HashBiMap.create();
   private static final Map<StructureFeature<?>, GenerationStep.Decoration> STEP = Maps.newHashMap();
   private static final Logger LOGGER = LogManager.getLogger();
   public static final StructureFeature<JigsawConfiguration> PILLAGER_OUTPOST = register("Pillager_Outpost", new PillagerOutpostFeature(JigsawConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<MineshaftConfiguration> MINESHAFT = register("Mineshaft", new MineshaftFeature(MineshaftConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
   public static final StructureFeature<NoneFeatureConfiguration> WOODLAND_MANSION = register("Mansion", new WoodlandMansionFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<NoneFeatureConfiguration> JUNGLE_TEMPLE = register("Jungle_Pyramid", new JunglePyramidFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<NoneFeatureConfiguration> DESERT_PYRAMID = register("Desert_Pyramid", new DesertPyramidFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<NoneFeatureConfiguration> IGLOO = register("Igloo", new IglooFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<RuinedPortalConfiguration> RUINED_PORTAL = register("Ruined_Portal", new RuinedPortalFeature(RuinedPortalConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<ShipwreckConfiguration> SHIPWRECK = register("Shipwreck", new ShipwreckFeature(ShipwreckConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final SwamplandHutFeature SWAMP_HUT = register("Swamp_Hut", new SwamplandHutFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<NoneFeatureConfiguration> STRONGHOLD = register("Stronghold", new StrongholdFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.STRONGHOLDS);
   public static final StructureFeature<NoneFeatureConfiguration> OCEAN_MONUMENT = register("Monument", new OceanMonumentFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<OceanRuinConfiguration> OCEAN_RUIN = register("Ocean_Ruin", new OceanRuinFeature(OceanRuinConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<NoneFeatureConfiguration> NETHER_BRIDGE = register("Fortress", new NetherFortressFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_DECORATION);
   public static final StructureFeature<NoneFeatureConfiguration> END_CITY = register("EndCity", new EndCityFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<ProbabilityFeatureConfiguration> BURIED_TREASURE = register("Buried_Treasure", new BuriedTreasureFeature(ProbabilityFeatureConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
   public static final StructureFeature<JigsawConfiguration> VILLAGE = register("Village", new VillageFeature(JigsawConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static final StructureFeature<RangeDecoratorConfiguration> NETHER_FOSSIL = register("Nether_Fossil", new NetherFossilFeature(RangeDecoratorConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_DECORATION);
   public static final StructureFeature<JigsawConfiguration> BASTION_REMNANT = register("Bastion_Remnant", new BastionFeature(JigsawConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
   public static List<StructureFeature<?>> NOISE_AFFECTING_FEATURES = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE, NETHER_FOSSIL, STRONGHOLD);
   private static final ResourceLocation JIGSAW_RENAME = new ResourceLocation("jigsaw");
   private static final Map<ResourceLocation, ResourceLocation> RENAMES = ImmutableMap.<ResourceLocation, ResourceLocation>builder().put(new ResourceLocation("nvi"), JIGSAW_RENAME).put(new ResourceLocation("pcp"), JIGSAW_RENAME).put(new ResourceLocation("bastionremnant"), JIGSAW_RENAME).put(new ResourceLocation("runtime"), JIGSAW_RENAME).build();
   public static final int MAX_STRUCTURE_RANGE = 8;
   private final Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> configuredStructureCodec;

   private static <F extends StructureFeature<?>> F register(String pName, F pStructure, GenerationStep.Decoration pDecorationStage) {
      STRUCTURES_REGISTRY.put(pName.toLowerCase(Locale.ROOT), pStructure);
      STEP.put(pStructure, pDecorationStage);
      return Registry.register(Registry.STRUCTURE_FEATURE, pName.toLowerCase(Locale.ROOT), pStructure);
   }

   public StructureFeature(Codec<C> pCodec) {
      this.configuredStructureCodec = pCodec.fieldOf("config").xmap((p_67094_) -> {
         return new ConfiguredStructureFeature<>(this, p_67094_);
      }, (p_67064_) -> {
         return p_67064_.config;
      }).codec();
   }

   public GenerationStep.Decoration step() {
      return STEP.get(this);
   }

   public static void bootstrap() {
   }

   @Nullable
   public static StructureStart<?> loadStaticStart(ServerLevel pLevel, CompoundTag pTag, long pSeed) {
      String s = pTag.getString("id");
      if ("INVALID".equals(s)) {
         return StructureStart.INVALID_START;
      } else {
         StructureFeature<?> structurefeature = Registry.STRUCTURE_FEATURE.get(new ResourceLocation(s.toLowerCase(Locale.ROOT)));
         if (structurefeature == null) {
            LOGGER.error("Unknown feature id: {}", (Object)s);
            return null;
         } else {
            ChunkPos chunkpos = new ChunkPos(pTag.getInt("ChunkX"), pTag.getInt("ChunkZ"));
            int i = pTag.getInt("references");
            ListTag listtag = pTag.getList("Children", 10);

            try {
               StructureStart<?> structurestart = structurefeature.createStart(chunkpos, i, pSeed);

               for(int j = 0; j < listtag.size(); ++j) {
                  CompoundTag compoundtag = listtag.getCompound(j);
                  String s1 = compoundtag.getString("id").toLowerCase(Locale.ROOT);
                  ResourceLocation resourcelocation = new ResourceLocation(s1);
                  ResourceLocation resourcelocation1 = RENAMES.getOrDefault(resourcelocation, resourcelocation);
                  StructurePieceType structurepiecetype = Registry.STRUCTURE_PIECE.get(resourcelocation1);
                  if (structurepiecetype == null) {
                     LOGGER.error("Unknown structure piece id: {}", (Object)resourcelocation1);
                  } else {
                     try {
                        StructurePiece structurepiece = structurepiecetype.load(pLevel, compoundtag);
                        structurestart.addPiece(structurepiece);
                     } catch (Exception exception) {
                        LOGGER.error("Exception loading structure piece with id {}", resourcelocation1, exception);
                     }
                  }
               }

               return structurestart;
            } catch (Exception exception1) {
               LOGGER.error("Failed Start with id {}", s, exception1);
               return null;
            }
         }
      }
   }

   public Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> configuredStructureCodec() {
      return this.configuredStructureCodec;
   }

   public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configured(C pConfig) {
      return new ConfiguredStructureFeature<>(this, pConfig);
   }

   @Nullable
   public BlockPos getNearestGeneratedFeature(LevelReader pLevel, StructureFeatureManager pStructureManager, BlockPos pPos, int pSearchRadius, boolean pSkipKnownStructures, long pSeed, StructureFeatureConfiguration pConfig) {
      int i = pConfig.spacing();
      int j = SectionPos.blockToSectionCoord(pPos.getX());
      int k = SectionPos.blockToSectionCoord(pPos.getZ());
      int l = 0;

      for(WorldgenRandom worldgenrandom = new WorldgenRandom(); l <= pSearchRadius; ++l) {
         for(int i1 = -l; i1 <= l; ++i1) {
            boolean flag = i1 == -l || i1 == l;

            for(int j1 = -l; j1 <= l; ++j1) {
               boolean flag1 = j1 == -l || j1 == l;
               if (flag || flag1) {
                  int k1 = j + i * i1;
                  int l1 = k + i * j1;
                  ChunkPos chunkpos = this.getPotentialFeatureChunk(pConfig, pSeed, worldgenrandom, k1, l1);
                  boolean flag2 = pLevel.getBiomeManager().getPrimaryBiomeAtChunk(chunkpos).getGenerationSettings().isValidStart(this);
                  if (flag2) {
                     ChunkAccess chunkaccess = pLevel.getChunk(chunkpos.x, chunkpos.z, ChunkStatus.STRUCTURE_STARTS);
                     StructureStart<?> structurestart = pStructureManager.getStartForFeature(SectionPos.bottomOf(chunkaccess), this, chunkaccess);
                     if (structurestart != null && structurestart.isValid()) {
                        if (pSkipKnownStructures && structurestart.canBeReferenced()) {
                           structurestart.addReference();
                           return structurestart.getLocatePos();
                        }

                        if (!pSkipKnownStructures) {
                           return structurestart.getLocatePos();
                        }
                     }
                  }

                  if (l == 0) {
                     break;
                  }
               }
            }

            if (l == 0) {
               break;
            }
         }
      }

      return null;
   }

   protected boolean linearSeparation() {
      return true;
   }

   public final ChunkPos getPotentialFeatureChunk(StructureFeatureConfiguration pSeparationSettings, long pSeed, WorldgenRandom pRandom, int pX, int pZ) {
      int i = pSeparationSettings.spacing();
      int j = pSeparationSettings.separation();
      int k = Math.floorDiv(pX, i);
      int l = Math.floorDiv(pZ, i);
      pRandom.setLargeFeatureWithSalt(pSeed, k, l, pSeparationSettings.salt());
      int i1;
      int j1;
      if (this.linearSeparation()) {
         i1 = pRandom.nextInt(i - j);
         j1 = pRandom.nextInt(i - j);
      } else {
         i1 = (pRandom.nextInt(i - j) + pRandom.nextInt(i - j)) / 2;
         j1 = (pRandom.nextInt(i - j) + pRandom.nextInt(i - j)) / 2;
      }

      return new ChunkPos(k * i + i1, l * i + j1);
   }

   protected boolean isFeatureChunk(ChunkGenerator pGenerator, BiomeSource pBiomeSource, long pSeed, WorldgenRandom pRandom, ChunkPos pChunkPos, Biome pBiome, ChunkPos pPotentialPos, C pConfig, LevelHeightAccessor pLevel) {
      return true;
   }

   private StructureStart<C> createStart(ChunkPos pChunkPos, int pReferences, long pSeed) {
      return this.getStartFactory().create(this, pChunkPos, pReferences, pSeed);
   }

   public StructureStart<?> generate(RegistryAccess pRegistryAccess, ChunkGenerator pChunkGenerator, BiomeSource pBiomeSource, StructureManager pStructureManager, long pSeed, ChunkPos pChunkPos, Biome pBiome, int pReferences, WorldgenRandom pRandom, StructureFeatureConfiguration pStructureConfiguration, C pFeatureConfiguration, LevelHeightAccessor pLevel) {
      ChunkPos chunkpos = this.getPotentialFeatureChunk(pStructureConfiguration, pSeed, pRandom, pChunkPos.x, pChunkPos.z);
      if (pChunkPos.x == chunkpos.x && pChunkPos.z == chunkpos.z && this.isFeatureChunk(pChunkGenerator, pBiomeSource, pSeed, pRandom, pChunkPos, pBiome, chunkpos, pFeatureConfiguration, pLevel)) {
         StructureStart<C> structurestart = this.createStart(pChunkPos, pReferences, pSeed);
         structurestart.generatePieces(pRegistryAccess, pChunkGenerator, pStructureManager, pChunkPos, pBiome, pFeatureConfiguration, pLevel);
         if (structurestart.isValid()) {
            return structurestart;
         }
      }

      return StructureStart.INVALID_START;
   }

   public abstract StructureFeature.StructureStartFactory<C> getStartFactory();

   public String getFeatureName() {
      return STRUCTURES_REGISTRY.inverse().get(this);
   }

   public WeightedRandomList<MobSpawnSettings.SpawnerData> getSpecialEnemies() {
      return getSpawnList(net.minecraft.world.entity.MobCategory.MONSTER);
   }

   public WeightedRandomList<MobSpawnSettings.SpawnerData> getSpecialAnimals() {
      return getSpawnList(net.minecraft.world.entity.MobCategory.CREATURE);
   }

   public WeightedRandomList<MobSpawnSettings.SpawnerData> getSpecialUndergroundWaterAnimals() {
      return getSpawnList(net.minecraft.world.entity.MobCategory.UNDERGROUND_WATER_CREATURE);
   }

   @Override
   public final WeightedRandomList<MobSpawnSettings.SpawnerData> getSpawnList(net.minecraft.world.entity.MobCategory classification) {
      return net.minecraftforge.common.world.StructureSpawnManager.getSpawnList(this, classification);
   }

   public interface StructureStartFactory<C extends FeatureConfiguration> {
      StructureStart<C> create(StructureFeature<C> pStructure, ChunkPos pChunkPos, int pReferences, long pSeed);
   }
}
