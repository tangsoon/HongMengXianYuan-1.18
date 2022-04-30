package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ConfiguredSurfaceBuilder<SC extends SurfaceBuilderConfiguration> {
   public static final Codec<ConfiguredSurfaceBuilder<?>> DIRECT_CODEC = Registry.SURFACE_BUILDER.dispatch((p_74774_) -> {
      return p_74774_.surfaceBuilder;
   }, SurfaceBuilder::configuredCodec);
   public static final Codec<Supplier<ConfiguredSurfaceBuilder<?>>> CODEC = RegistryFileCodec.create(Registry.CONFIGURED_SURFACE_BUILDER_REGISTRY, DIRECT_CODEC);
   public final SurfaceBuilder<SC> surfaceBuilder;
   public final SC config;

   public ConfiguredSurfaceBuilder(SurfaceBuilder<SC> pSurfaceBuilder, SC pConfig) {
      this.surfaceBuilder = pSurfaceBuilder;
      this.config = pConfig;
   }

   /**
    * @see net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder#apply(Random, ChunkAccess, Biome, int, int,
    * int, double, BlockState, BlockState, int, int, long)
    */
   public void apply(Random pRandom, ChunkAccess pChunk, Biome pBiome, int pX, int pZ, int pHeight, double pNoise, BlockState pDefaultBlock, BlockState pDefaultFluid, int pSeaLevel, int pMinSurfaceLevel, long pSeed) {
      this.surfaceBuilder.apply(pRandom, pChunk, pBiome, pX, pZ, pHeight, pNoise, pDefaultBlock, pDefaultFluid, pSeaLevel, pMinSurfaceLevel, pSeed, this.config);
   }

   /**
    * @see net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder#initNoise(long)
    */
   public void initNoise(long pSeed) {
      this.surfaceBuilder.initNoise(pSeed);
   }

   public SC config() {
      return this.config;
   }
}