package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.OceanMonumentPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class OceanMonumentFeature extends StructureFeature<NoneFeatureConfiguration> {
   private static final WeightedRandomList<MobSpawnSettings.SpawnerData> MONUMENT_ENEMIES = WeightedRandomList.create(new MobSpawnSettings.SpawnerData(EntityType.GUARDIAN, 1, 2, 4));

   public OceanMonumentFeature(Codec<NoneFeatureConfiguration> p_66472_) {
      super(p_66472_);
   }

   protected boolean linearSeparation() {
      return false;
   }

   protected boolean isFeatureChunk(ChunkGenerator pGenerator, BiomeSource pBiomeSource, long pSeed, WorldgenRandom pRandom, ChunkPos pChunkPos, Biome pBiome, ChunkPos pPotentialPos, NoneFeatureConfiguration pConfig, LevelHeightAccessor pLevel) {
      int i = pChunkPos.getBlockX(9);
      int j = pChunkPos.getBlockZ(9);

      for(Biome biome : pBiomeSource.getBiomesWithin(i, pGenerator.getSeaLevel(), j, 16)) {
         if (!biome.getGenerationSettings().isValidStart(this)) {
            return false;
         }
      }

      for(Biome biome1 : pBiomeSource.getBiomesWithin(i, pGenerator.getSeaLevel(), j, 29)) {
         if (biome1.getBiomeCategory() != Biome.BiomeCategory.OCEAN && biome1.getBiomeCategory() != Biome.BiomeCategory.RIVER) {
            return false;
         }
      }

      return true;
   }

   public StructureFeature.StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
      return OceanMonumentFeature.OceanMonumentStart::new;
   }

   @Override
   public java.util.List<MobSpawnSettings.SpawnerData> getDefaultSpawnList(net.minecraft.world.entity.MobCategory category) {
      if (category == net.minecraft.world.entity.MobCategory.MONSTER)
         return MONUMENT_ENEMIES.unwrap();
      return java.util.Collections.emptyList();
   }

   public static class OceanMonumentStart extends StructureStart<NoneFeatureConfiguration> {
      private boolean isCreated;

      public OceanMonumentStart(StructureFeature<NoneFeatureConfiguration> p_160147_, ChunkPos p_160148_, int p_160149_, long p_160150_) {
         super(p_160147_, p_160148_, p_160149_, p_160150_);
      }

      public void generatePieces(RegistryAccess pRegistryAccess, ChunkGenerator pChunkGenerator, StructureManager pStructureManager, ChunkPos pChunkPos, Biome pBiome, NoneFeatureConfiguration pConfig, LevelHeightAccessor pLevel) {
         this.generatePieces(pChunkPos);
      }

      private void generatePieces(ChunkPos pChunkPos) {
         int i = pChunkPos.getMinBlockX() - 29;
         int j = pChunkPos.getMinBlockZ() - 29;
         Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(this.random);
         this.addPiece(new OceanMonumentPieces.MonumentBuilding(this.random, i, j, direction));
         this.isCreated = true;
      }

      public void placeInChunk(WorldGenLevel pLevel, StructureFeatureManager pStructureManager, ChunkGenerator pChunkGenerator, Random pRandom, BoundingBox pBox, ChunkPos pChunkPos) {
         if (!this.isCreated) {
            this.pieces.clear();
            this.generatePieces(this.getChunkPos());
         }

         super.placeInChunk(pLevel, pStructureManager, pChunkGenerator, pRandom, pBox, pChunkPos);
      }
   }
}
