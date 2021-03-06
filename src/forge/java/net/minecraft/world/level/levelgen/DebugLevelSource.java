package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class DebugLevelSource extends ChunkGenerator {
   public static final Codec<DebugLevelSource> CODEC = RegistryLookupCodec.create(Registry.BIOME_REGISTRY).xmap(DebugLevelSource::new, DebugLevelSource::biomes).stable().codec();
   private static final int BLOCK_MARGIN = 2;
   /** A list of all valid block states. */
   private static List<BlockState> ALL_BLOCKS = StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap((p_64147_) -> {
      return p_64147_.getStateDefinition().getPossibleStates().stream();
   }).collect(Collectors.toList());
   private static int GRID_WIDTH = Mth.ceil(Mth.sqrt((float)ALL_BLOCKS.size()));
   private static int GRID_HEIGHT = Mth.ceil((float)ALL_BLOCKS.size() / (float)GRID_WIDTH);
   protected static final BlockState AIR = Blocks.AIR.defaultBlockState();
   protected static final BlockState BARRIER = Blocks.BARRIER.defaultBlockState();
   public static final int HEIGHT = 70;
   public static final int BARRIER_HEIGHT = 60;
   private final Registry<Biome> biomes;

   public DebugLevelSource(Registry<Biome> p_64120_) {
      super(new FixedBiomeSource(p_64120_.getOrThrow(Biomes.PLAINS)), new StructureSettings(false));
      this.biomes = p_64120_;
   }

   public Registry<Biome> biomes() {
      return this.biomes;
   }

   protected Codec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public ChunkGenerator withSeed(long pSeed) {
      return this;
   }

   /**
    * Generate the SURFACE part of a chunk
    */
   public void buildSurfaceAndBedrock(WorldGenRegion pLevel, ChunkAccess pChunk) {
   }

   public void applyCarvers(long pSeed, BiomeManager pBiomeManager, ChunkAccess pChunk, GenerationStep.Carving pStep) {
   }

   public void applyBiomeDecoration(WorldGenRegion pLevel, StructureFeatureManager pStructureFeatureManager) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      ChunkPos chunkpos = pLevel.getCenter();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            int k = SectionPos.sectionToBlockCoord(chunkpos.x, i);
            int l = SectionPos.sectionToBlockCoord(chunkpos.z, j);
            pLevel.setBlock(blockpos$mutableblockpos.set(k, 60, l), BARRIER, 2);
            BlockState blockstate = getBlockStateFor(k, l);
            if (blockstate != null) {
               pLevel.setBlock(blockpos$mutableblockpos.set(k, 70, l), blockstate, 2);
            }
         }
      }

   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Executor pExecutor, StructureFeatureManager pStructureFeatureManager, ChunkAccess pChunk) {
      return CompletableFuture.completedFuture(pChunk);
   }

   public int getBaseHeight(int pX, int pZ, Heightmap.Types pType, LevelHeightAccessor pLevel) {
      return 0;
   }

   public NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor pLevel) {
      return new NoiseColumn(0, new BlockState[0]);
   }

   public static BlockState getBlockStateFor(int pChunkX, int pChunkZ) {
      BlockState blockstate = AIR;
      if (pChunkX > 0 && pChunkZ > 0 && pChunkX % 2 != 0 && pChunkZ % 2 != 0) {
         pChunkX = pChunkX / 2;
         pChunkZ = pChunkZ / 2;
         if (pChunkX <= GRID_WIDTH && pChunkZ <= GRID_HEIGHT) {
            int i = Mth.abs(pChunkX * GRID_WIDTH + pChunkZ);
            if (i < ALL_BLOCKS.size()) {
               blockstate = ALL_BLOCKS.get(i);
            }
         }
      }

      return blockstate;
   }
   
   public static void initValidStates() {
      ALL_BLOCKS = StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap(block -> block.getStateDefinition().getPossibleStates().stream()).collect(Collectors.toList());
      GRID_WIDTH = Mth.ceil(Mth.sqrt(ALL_BLOCKS.size()));
      GRID_HEIGHT = Mth.ceil((float) (ALL_BLOCKS.size() / GRID_WIDTH));
   }
}
