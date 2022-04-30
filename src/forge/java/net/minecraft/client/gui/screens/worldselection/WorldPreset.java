package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.CreateBuffetWorldScreen;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class WorldPreset {
   public static final WorldPreset NORMAL = new WorldPreset("default") {
      protected ChunkGenerator generator(Registry<Biome> p_101580_, Registry<NoiseGeneratorSettings> p_101581_, long p_101582_) {
         return new NoiseBasedChunkGenerator(new OverworldBiomeSource(p_101582_, false, false, p_101580_), p_101582_, () -> {
            return p_101581_.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
         });
      }
   };
   private static final WorldPreset FLAT = new WorldPreset("flat") {
      protected ChunkGenerator generator(Registry<Biome> p_101586_, Registry<NoiseGeneratorSettings> p_101587_, long p_101588_) {
         return new FlatLevelSource(FlatLevelGeneratorSettings.getDefault(p_101586_));
      }
   };
   private static final WorldPreset LARGE_BIOMES = new WorldPreset("large_biomes") {
      protected ChunkGenerator generator(Registry<Biome> p_101594_, Registry<NoiseGeneratorSettings> p_101595_, long p_101596_) {
         return new NoiseBasedChunkGenerator(new OverworldBiomeSource(p_101596_, false, true, p_101594_), p_101596_, () -> {
            return p_101595_.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
         });
      }
   };
   public static final WorldPreset AMPLIFIED = new WorldPreset("amplified") {
      protected ChunkGenerator generator(Registry<Biome> p_101602_, Registry<NoiseGeneratorSettings> p_101603_, long p_101604_) {
         return new NoiseBasedChunkGenerator(new OverworldBiomeSource(p_101604_, false, false, p_101602_), p_101604_, () -> {
            return p_101603_.getOrThrow(NoiseGeneratorSettings.AMPLIFIED);
         });
      }
   };
   private static final WorldPreset SINGLE_BIOME_SURFACE = new WorldPreset("single_biome_surface") {
      protected ChunkGenerator generator(Registry<Biome> p_101610_, Registry<NoiseGeneratorSettings> p_101611_, long p_101612_) {
         return new NoiseBasedChunkGenerator(new FixedBiomeSource(p_101610_.getOrThrow(Biomes.PLAINS)), p_101612_, () -> {
            return p_101611_.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
         });
      }
   };
   private static final WorldPreset SINGLE_BIOME_CAVES = new WorldPreset("single_biome_caves") {
      public WorldGenSettings create(RegistryAccess.RegistryHolder p_101622_, long p_101623_, boolean p_101624_, boolean p_101625_) {
         Registry<Biome> registry = p_101622_.registryOrThrow(Registry.BIOME_REGISTRY);
         Registry<DimensionType> registry1 = p_101622_.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
         Registry<NoiseGeneratorSettings> registry2 = p_101622_.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
         return new WorldGenSettings(p_101623_, p_101624_, p_101625_, WorldGenSettings.withOverworld(DimensionType.defaultDimensions(registry1, registry, registry2, p_101623_), () -> {
            return registry1.getOrThrow(DimensionType.OVERWORLD_CAVES_LOCATION);
         }, this.generator(registry, registry2, p_101623_)));
      }

      protected ChunkGenerator generator(Registry<Biome> p_101618_, Registry<NoiseGeneratorSettings> p_101619_, long p_101620_) {
         return new NoiseBasedChunkGenerator(new FixedBiomeSource(p_101618_.getOrThrow(Biomes.PLAINS)), p_101620_, () -> {
            return p_101619_.getOrThrow(NoiseGeneratorSettings.CAVES);
         });
      }
   };
   private static final WorldPreset SINGLE_BIOME_FLOATING_ISLANDS = new WorldPreset("single_biome_floating_islands") {
      protected ChunkGenerator generator(Registry<Biome> p_101633_, Registry<NoiseGeneratorSettings> p_101634_, long p_101635_) {
         return new NoiseBasedChunkGenerator(new FixedBiomeSource(p_101633_.getOrThrow(Biomes.PLAINS)), p_101635_, () -> {
            return p_101634_.getOrThrow(NoiseGeneratorSettings.FLOATING_ISLANDS);
         });
      }
   };
   private static final WorldPreset DEBUG = new WorldPreset("debug_all_block_states") {
      protected ChunkGenerator generator(Registry<Biome> p_101639_, Registry<NoiseGeneratorSettings> p_101640_, long p_101641_) {
         return new DebugLevelSource(p_101639_);
      }
   };
   protected static final List<WorldPreset> PRESETS = Lists.newArrayList(NORMAL, FLAT, LARGE_BIOMES, AMPLIFIED, SINGLE_BIOME_SURFACE, SINGLE_BIOME_CAVES, SINGLE_BIOME_FLOATING_ISLANDS, DEBUG);
   protected static final Map<Optional<WorldPreset>, WorldPreset.PresetEditor> EDITORS = ImmutableMap.of(Optional.of(FLAT), (p_101573_, p_101574_) -> {
      ChunkGenerator chunkgenerator = p_101574_.overworld();
      return new CreateFlatWorldScreen(p_101573_, (p_170300_) -> {
         p_101573_.worldGenSettingsComponent.updateSettings(new WorldGenSettings(p_101574_.seed(), p_101574_.generateFeatures(), p_101574_.generateBonusChest(), WorldGenSettings.withOverworld(p_101573_.worldGenSettingsComponent.registryHolder().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), p_101574_.dimensions(), new FlatLevelSource(p_170300_))));
      }, chunkgenerator instanceof FlatLevelSource ? ((FlatLevelSource)chunkgenerator).settings() : FlatLevelGeneratorSettings.getDefault(p_101573_.worldGenSettingsComponent.registryHolder().registryOrThrow(Registry.BIOME_REGISTRY)));
   }, Optional.of(SINGLE_BIOME_SURFACE), (p_101564_, p_101565_) -> {
      return new CreateBuffetWorldScreen(p_101564_, p_101564_.worldGenSettingsComponent.registryHolder(), (p_170310_) -> {
         p_101564_.worldGenSettingsComponent.updateSettings(fromBuffetSettings(p_101564_.worldGenSettingsComponent.registryHolder(), p_101565_, SINGLE_BIOME_SURFACE, p_170310_));
      }, parseBuffetSettings(p_101564_.worldGenSettingsComponent.registryHolder(), p_101565_));
   }, Optional.of(SINGLE_BIOME_CAVES), (p_101555_, p_101556_) -> {
      return new CreateBuffetWorldScreen(p_101555_, p_101555_.worldGenSettingsComponent.registryHolder(), (p_170306_) -> {
         p_101555_.worldGenSettingsComponent.updateSettings(fromBuffetSettings(p_101555_.worldGenSettingsComponent.registryHolder(), p_101556_, SINGLE_BIOME_CAVES, p_170306_));
      }, parseBuffetSettings(p_101555_.worldGenSettingsComponent.registryHolder(), p_101556_));
   }, Optional.of(SINGLE_BIOME_FLOATING_ISLANDS), (p_101527_, p_101528_) -> {
      return new CreateBuffetWorldScreen(p_101527_, p_101527_.worldGenSettingsComponent.registryHolder(), (p_170296_) -> {
         p_101527_.worldGenSettingsComponent.updateSettings(fromBuffetSettings(p_101527_.worldGenSettingsComponent.registryHolder(), p_101528_, SINGLE_BIOME_FLOATING_ISLANDS, p_170296_));
      }, parseBuffetSettings(p_101527_.worldGenSettingsComponent.registryHolder(), p_101528_));
   });
   private final Component description;

   WorldPreset(String pDescription) {
      this.description = new TranslatableComponent("generator." + pDescription);
   }
   public WorldPreset(Component displayName) {
      this.description = displayName;
   }

   private static WorldGenSettings fromBuffetSettings(RegistryAccess pRegistryAccess, WorldGenSettings pSettings, WorldPreset pPreset, Biome pBiome) {
      BiomeSource biomesource = new FixedBiomeSource(pBiome);
      Registry<DimensionType> registry = pRegistryAccess.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      Registry<NoiseGeneratorSettings> registry1 = pRegistryAccess.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      Supplier<NoiseGeneratorSettings> supplier;
      if (pPreset == SINGLE_BIOME_CAVES) {
         supplier = () -> {
            return registry1.getOrThrow(NoiseGeneratorSettings.CAVES);
         };
      } else if (pPreset == SINGLE_BIOME_FLOATING_ISLANDS) {
         supplier = () -> {
            return registry1.getOrThrow(NoiseGeneratorSettings.FLOATING_ISLANDS);
         };
      } else {
         supplier = () -> {
            return registry1.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
         };
      }

      return new WorldGenSettings(pSettings.seed(), pSettings.generateFeatures(), pSettings.generateBonusChest(), WorldGenSettings.withOverworld(registry, pSettings.dimensions(), new NoiseBasedChunkGenerator(biomesource, pSettings.seed(), supplier)));
   }

   private static Biome parseBuffetSettings(RegistryAccess pRegistryAccess, WorldGenSettings pSettings) {
      return pSettings.overworld().getBiomeSource().possibleBiomes().stream().findFirst().orElse(pRegistryAccess.registryOrThrow(Registry.BIOME_REGISTRY).getOrThrow(Biomes.PLAINS));
   }

   public static Optional<WorldPreset> of(WorldGenSettings pSettings) {
      ChunkGenerator chunkgenerator = pSettings.overworld();
      if (chunkgenerator instanceof FlatLevelSource) {
         return Optional.of(FLAT);
      } else {
         return chunkgenerator instanceof DebugLevelSource ? Optional.of(DEBUG) : Optional.empty();
      }
   }

   public Component description() {
      return this.description;
   }

   public WorldGenSettings create(RegistryAccess.RegistryHolder pRegistryHolder, long pSeed, boolean pGenerateFeatures, boolean pGenerateBonusChest) {
      Registry<Biome> registry = pRegistryHolder.registryOrThrow(Registry.BIOME_REGISTRY);
      Registry<DimensionType> registry1 = pRegistryHolder.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      Registry<NoiseGeneratorSettings> registry2 = pRegistryHolder.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      return new WorldGenSettings(pSeed, pGenerateFeatures, pGenerateBonusChest, WorldGenSettings.withOverworld(registry1, DimensionType.defaultDimensions(registry1, registry, registry2, pSeed), this.generator(registry, registry2, pSeed)));
   }

   protected abstract ChunkGenerator generator(Registry<Biome> pBiomeRegistry, Registry<NoiseGeneratorSettings> pSettingsRegistry, long pSeed);

   public static boolean isVisibleByDefault(WorldPreset pPreset) {
      return pPreset != DEBUG;
   }

   @OnlyIn(Dist.CLIENT)
   public interface PresetEditor {
      Screen createEditScreen(CreateWorldScreen pCreateWorldScreen, WorldGenSettings pSettings);
   }

   // Forge start
   // For internal use only, automatically called for all ForgeWorldTypes. Register your ForgeWorldType in the forge registry!
   public static void registerGenerator(WorldPreset gen) { PRESETS.add(gen); }
}
