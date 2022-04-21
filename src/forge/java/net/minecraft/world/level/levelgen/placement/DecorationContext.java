package net.minecraft.world.level.levelgen.placement;

import java.util.BitSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class DecorationContext extends WorldGenerationContext {
   private final WorldGenLevel level;

   public DecorationContext(WorldGenLevel pLevel, ChunkGenerator pGenerator) {
      super(pGenerator, pLevel);
      this.level = pLevel;
   }

   public int getHeight(Heightmap.Types pHeightmap, int pX, int pZ) {
      return this.level.getHeight(pHeightmap, pX, pZ);
   }

   public BitSet getCarvingMask(ChunkPos pChunkPos, GenerationStep.Carving pStep) {
      return ((ProtoChunk)this.level.getChunk(pChunkPos.x, pChunkPos.z)).getOrCreateCarvingMask(pStep);
   }

   public BlockState getBlockState(BlockPos pPos) {
      return this.level.getBlockState(pPos);
   }

   public int getMinBuildHeight() {
      return this.level.getMinBuildHeight();
   }

   public WorldGenLevel getLevel() {
      return this.level;
   }
}