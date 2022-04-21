package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class ConfiguredStructureFeature<FC extends FeatureConfiguration, F extends StructureFeature<FC>> {
   public static final Codec<ConfiguredStructureFeature<?, ?>> DIRECT_CODEC = Registry.STRUCTURE_FEATURE.dispatch((p_65410_) -> {
      return p_65410_.feature;
   }, StructureFeature::configuredStructureCodec);
   public static final Codec<Supplier<ConfiguredStructureFeature<?, ?>>> CODEC = RegistryFileCodec.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, DIRECT_CODEC);
   public static final Codec<List<Supplier<ConfiguredStructureFeature<?, ?>>>> LIST_CODEC = RegistryFileCodec.homogeneousList(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, DIRECT_CODEC);
   public final F feature;
   public final FC config;

   public ConfiguredStructureFeature(F pFeature, FC pConfig) {
      this.feature = pFeature;
      this.config = pConfig;
   }

   public StructureStart<?> generate(RegistryAccess pRegistry, ChunkGenerator pChunkGenerator, BiomeSource pBiomeSource, StructureManager pStructureManager, long pSeed, ChunkPos pChunkPos, Biome pBiome, int pReferences, StructureFeatureConfiguration pConfig, LevelHeightAccessor pLevel) {
      return this.feature.generate(pRegistry, pChunkGenerator, pBiomeSource, pStructureManager, pSeed, pChunkPos, pBiome, pReferences, new WorldgenRandom(), pConfig, this.config, pLevel);
   }
}