package by.ts.hmxy.world.level.biome;

import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.level.levelgen.placement.FrequencyWithExtraChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoiseDependantDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.data.worldgen.Features;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Registry;
import java.util.Map;
import java.util.HashMap;
import com.google.common.collect.ImmutableList;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.world.level.levelgen.feature.TestBiomeFruitDecorator;
import by.ts.hmxy.world.level.levelgen.feature.TestBiomeLeaveDecorator;
import by.ts.hmxy.world.level.levelgen.feature.TestBiomeTrunkDecorator;

public class TestBiomeBiome {
	private static final ConfiguredSurfaceBuilder<?> SURFACE_BUILDER = SurfaceBuilder.DEFAULT
			.configured(new SurfaceBuilderBaseConfiguration(Blocks.MYCELIUM.defaultBlockState(),
					Blocks.DIRT.defaultBlockState(), Blocks.DIRT.defaultBlockState()));

	public static Biome createBiome() {
		BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder().fogColor(-13261).waterColor(-16737895)
				.waterFogColor(-10066330).skyColor(-13261).foliageColorOverride(-3342592).grassColorOverride(-39322)
				.ambientLoopSound(new SoundEvent(new ResourceLocation("ambient.cave")))
				.ambientParticle(new AmbientParticleSettings(ParticleTypes.EXPLOSION, 0.005f)).build();
		BiomeGenerationSettings.Builder biomeGenerationSettings = new BiomeGenerationSettings.Builder()
				.surfaceBuilder(SURFACE_BUILDER);
		biomeGenerationSettings.addStructureStart(StructureFeatures.STRONGHOLD);
		biomeGenerationSettings.addStructureStart(StructureFeatures.END_CITY);
		biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, register("trees", Feature.TREE
				.configured((new TreeConfiguration.TreeConfigurationBuilder(
						new SimpleStateProvider(Blocks.POLISHED_GRANITE.defaultBlockState()),
						new StraightTrunkPlacer(7, 2, 0),
						new SimpleStateProvider(Blocks.POLISHED_DIORITE.defaultBlockState()),
						new SimpleStateProvider(Blocks.OAK_SAPLING.defaultBlockState()),
						new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
						new TwoLayersFeatureSize(1, 0, 1)))
								.decorators(ImmutableList.of(TestBiomeLeaveDecorator.INSTANCE,
										TestBiomeTrunkDecorator.INSTANCE, TestBiomeFruitDecorator.INSTANCE))
								.build())
				.decorated(Features.Decorators.HEIGHTMAP_SQUARE).decorated(FeatureDecorator.COUNT_EXTRA
						.configured(new FrequencyWithExtraChanceDecoratorConfiguration(1, 0.1F, 1)))));
		biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
				register("grass", Feature.RANDOM_PATCH.configured(Features.Configs.DEFAULT_GRASS_CONFIG)
						.decorated(Features.Decorators.HEIGHTMAP_DOUBLE_SQUARE).decorated(FeatureDecorator.COUNT_NOISE
								.configured(new NoiseDependantDecoratorConfiguration(-0.8D, 5, 4)))));
		biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
				register("flower",
						Feature.FLOWER.configured(Features.Configs.DEFAULT_FLOWER_CONFIG)
								.decorated(Features.Decorators.ADD_32).decorated(Features.Decorators.HEIGHTMAP_SQUARE)
								.count(4)));
		BiomeDefaultFeatures.addDefaultOres(biomeGenerationSettings);
		MobSpawnSettings.Builder mobSpawnInfo = new MobSpawnSettings.Builder().setPlayerCanSpawn();
		mobSpawnInfo.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 20, 4, 4));
		return new Biome.BiomeBuilder().precipitation(Biome.Precipitation.RAIN)
				.biomeCategory(Biome.BiomeCategory.PLAINS).depth(0.1f).scale(0.2f).temperature(0.5f).downfall(0.5f)
				.specialEffects(effects).mobSpawnSettings(mobSpawnInfo.build())
				.generationSettings(biomeGenerationSettings.build()).build();
	}

	public static void init() {
		Registry.register(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER,
				new ResourceLocation(HmxyMod.MOD_ID, "test_biome"), SURFACE_BUILDER);
		CONFIGURED_FEATURES.forEach((resourceLocation, configuredFeature) -> Registry
				.register(BuiltinRegistries.CONFIGURED_FEATURE, resourceLocation, configuredFeature));
		BiomeDictionary.addTypes(
				ResourceKey.create(Registry.BIOME_REGISTRY, BuiltinRegistries.BIOME.getKey(HmxyModBiomes.TEST_BIOME)),
				BiomeDictionary.Type.END, BiomeDictionary.Type.MESA);
		BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(
				ResourceKey.create(Registry.BIOME_REGISTRY, BuiltinRegistries.BIOME.getKey(HmxyModBiomes.TEST_BIOME)),
				10));
	}

	private static final Map<ResourceLocation, ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = new HashMap<>();

	private static ConfiguredFeature<?, ?> register(String name, ConfiguredFeature<?, ?> configuredFeature) {
		CONFIGURED_FEATURES.put(new ResourceLocation(HmxyMod.MOD_ID, name + "_test_biome"), configuredFeature);
		return configuredFeature;
	}
}
