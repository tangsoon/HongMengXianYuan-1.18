package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class UnderwaterCaveWorldCarver extends CaveWorldCarver {
   public UnderwaterCaveWorldCarver(Codec<CaveCarverConfiguration> p_64932_) {
      super(p_64932_);
      this.replaceableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.PACKED_ICE);
   }

   protected boolean hasDisallowedLiquid(ChunkAccess pChunk, int pMinX, int pMaxX, int pMinY, int pMaxY, int pMinZ, int pMaxZ) {
      return false;
   }

   /**
    * Carves a single block, replacing it with the appropiate state if possible, and handles replacing exposed dirt with
    * grass.
    * @param pPos The position to carve at. The method does not mutate this position.
    * @param pCheckPos An additional mutable block position object to be used and modified by the method
    * @param pReachedSurface Set to true if the block carved was the surface, which is checked as being either grass or
    * mycelium
    */
   protected boolean carveBlock(CarvingContext pContext, CaveCarverConfiguration pConfig, ChunkAccess pChunk, Function<BlockPos, Biome> pBiomeAccessor, BitSet pCarvingMask, Random pRandom, BlockPos.MutableBlockPos pPos, BlockPos.MutableBlockPos pCheckPos, Aquifer pAquifer, MutableBoolean pReachedSurface) {
      return carveBlock(this, pChunk, pRandom, pPos, pCheckPos, pAquifer);
   }

   protected static boolean carveBlock(WorldCarver<?> pCarver, ChunkAccess pChunk, Random pRandom, BlockPos.MutableBlockPos pPos, BlockPos.MutableBlockPos pMutablePos, Aquifer pAquifer) {
      if (pAquifer.computeState(WorldCarver.STONE_SOURCE, pPos.getX(), pPos.getY(), pPos.getZ(), Double.NEGATIVE_INFINITY).isAir()) {
         return false;
      } else {
         BlockState blockstate = pChunk.getBlockState(pPos);
         if (!pCarver.canReplaceBlock(blockstate)) {
            return false;
         } else if (pPos.getY() == 10) {
            float f = pRandom.nextFloat();
            if ((double)f < 0.25D) {
               pChunk.setBlockState(pPos, Blocks.MAGMA_BLOCK.defaultBlockState(), false);
               pChunk.getBlockTicks().scheduleTick(pPos, Blocks.MAGMA_BLOCK, 0);
            } else {
               pChunk.setBlockState(pPos, Blocks.OBSIDIAN.defaultBlockState(), false);
            }

            return true;
         } else if (pPos.getY() < 10) {
            pChunk.setBlockState(pPos, Blocks.LAVA.defaultBlockState(), false);
            return false;
         } else {
            pChunk.setBlockState(pPos, WATER.createLegacyBlock(), false);
            int i = pChunk.getPos().x;
            int j = pChunk.getPos().z;

            for(Direction direction : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
               pMutablePos.setWithOffset(pPos, direction);
               if (SectionPos.blockToSectionCoord(pMutablePos.getX()) != i || SectionPos.blockToSectionCoord(pMutablePos.getZ()) != j || pChunk.getBlockState(pMutablePos).isAir()) {
                  pChunk.getLiquidTicks().scheduleTick(pPos, WATER.getType(), 0);
                  break;
               }
            }

            return true;
         }
      }
   }
}