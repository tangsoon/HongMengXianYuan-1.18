package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.NoiseAffectingStructureStart;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class JigsawFeature extends StructureFeature<JigsawConfiguration> {
   final int startY;
   final boolean doExpansionHack;
   final boolean projectStartToHeightmap;

   public JigsawFeature(Codec<JigsawConfiguration> pCodec, int pStartY, boolean pDoExpansionHack, boolean pProjectStartToHeightmap) {
      super(pCodec);
      this.startY = pStartY;
      this.doExpansionHack = pDoExpansionHack;
      this.projectStartToHeightmap = pProjectStartToHeightmap;
   }

   public StructureFeature.StructureStartFactory<JigsawConfiguration> getStartFactory() {
      return (p_159909_, p_159910_, p_159911_, p_159912_) -> {
         return new JigsawFeature.FeatureStart(this, p_159910_, p_159911_, p_159912_);
      };
   }

   public static class FeatureStart extends NoiseAffectingStructureStart<JigsawConfiguration> {
      private final JigsawFeature feature;

      public FeatureStart(JigsawFeature pFeature, ChunkPos pChunkPos, int pReferences, long pSeed) {
         super(pFeature, pChunkPos, pReferences, pSeed);
         this.feature = pFeature;
      }

      public void generatePieces(RegistryAccess pRegistryAccess, ChunkGenerator pChunkGenerator, StructureManager pStructureManager, ChunkPos pChunkPos, Biome pBiome, JigsawConfiguration pConfig, LevelHeightAccessor pLevel) {
         BlockPos blockpos = new BlockPos(pChunkPos.getMinBlockX(), this.feature.startY, pChunkPos.getMinBlockZ());
         Pools.bootstrap();
         JigsawPlacement.addPieces(pRegistryAccess, pConfig, PoolElementStructurePiece::new, pChunkGenerator, pStructureManager, blockpos, this, this.random, this.feature.doExpansionHack, this.feature.projectStartToHeightmap, pLevel);
      }
   }
}