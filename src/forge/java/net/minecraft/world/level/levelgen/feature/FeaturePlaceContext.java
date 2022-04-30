package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class FeaturePlaceContext<FC extends FeatureConfiguration> {
   private final WorldGenLevel level;
   private final ChunkGenerator chunkGenerator;
   private final Random random;
   private final BlockPos origin;
   private final FC config;

   public FeaturePlaceContext(WorldGenLevel pLevel, ChunkGenerator pChunkGenerator, Random pRandom, BlockPos pOrigin, FC pConfig) {
      this.level = pLevel;
      this.chunkGenerator = pChunkGenerator;
      this.random = pRandom;
      this.origin = pOrigin;
      this.config = pConfig;
   }

   public WorldGenLevel level() {
      return this.level;
   }

   public ChunkGenerator chunkGenerator() {
      return this.chunkGenerator;
   }

   public Random random() {
      return this.random;
   }

   public BlockPos origin() {
      return this.origin;
   }

   public FC config() {
      return this.config;
   }
}