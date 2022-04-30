package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class FlatLevelSource extends ChunkGenerator {
   public static final Codec<FlatLevelSource> CODEC = FlatLevelGeneratorSettings.CODEC.fieldOf("settings").xmap(FlatLevelSource::new, FlatLevelSource::settings).codec();
   private final FlatLevelGeneratorSettings settings;

   public FlatLevelSource(FlatLevelGeneratorSettings p_64168_) {
      super(new FixedBiomeSource(p_64168_.getBiomeFromSettings()), new FixedBiomeSource(p_64168_.getBiome()), p_64168_.structureSettings(), 0L);
      this.settings = p_64168_;
   }

   protected Codec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public ChunkGenerator withSeed(long pSeed) {
      return this;
   }

   public FlatLevelGeneratorSettings settings() {
      return this.settings;
   }

   /**
    * Generate the SURFACE part of a chunk
    */
   public void buildSurfaceAndBedrock(WorldGenRegion pLevel, ChunkAccess pChunk) {
   }

   public int getSpawnHeight(LevelHeightAccessor pLevel) {
      return pLevel.getMinBuildHeight() + Math.min(pLevel.getHeight(), this.settings.getLayers().size());
   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Executor pExecutor, StructureFeatureManager pStructureFeatureManager, ChunkAccess pChunk) {
      List<BlockState> list = this.settings.getLayers();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      Heightmap heightmap = pChunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap heightmap1 = pChunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

      for(int i = 0; i < Math.min(pChunk.getHeight(), list.size()); ++i) {
         BlockState blockstate = list.get(i);
         if (blockstate != null) {
            int j = pChunk.getMinBuildHeight() + i;

            for(int k = 0; k < 16; ++k) {
               for(int l = 0; l < 16; ++l) {
                  pChunk.setBlockState(blockpos$mutableblockpos.set(k, j, l), blockstate, false);
                  heightmap.update(k, j, l, blockstate);
                  heightmap1.update(k, j, l, blockstate);
               }
            }
         }
      }

      return CompletableFuture.completedFuture(pChunk);
   }

   public int getBaseHeight(int pX, int pZ, Heightmap.Types pType, LevelHeightAccessor pLevel) {
      List<BlockState> list = this.settings.getLayers();

      for(int i = Math.min(list.size(), pLevel.getMaxBuildHeight()) - 1; i >= 0; --i) {
         BlockState blockstate = list.get(i);
         if (blockstate != null && pType.isOpaque().test(blockstate)) {
            return pLevel.getMinBuildHeight() + i + 1;
         }
      }

      return pLevel.getMinBuildHeight();
   }

   public NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor pLevel) {
      return new NoiseColumn(pLevel.getMinBuildHeight(), this.settings.getLayers().stream().limit((long)pLevel.getHeight()).map((p_64189_) -> {
         return p_64189_ == null ? Blocks.AIR.defaultBlockState() : p_64189_;
      }).toArray((p_64171_) -> {
         return new BlockState[p_64171_];
      }));
   }
}