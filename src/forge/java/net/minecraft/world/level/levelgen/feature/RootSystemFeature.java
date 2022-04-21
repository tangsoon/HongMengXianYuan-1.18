package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration;

public class RootSystemFeature extends Feature<RootSystemConfiguration> {
   public RootSystemFeature(Codec<RootSystemConfiguration> p_160218_) {
      super(p_160218_);
   }

   /**
    * Places the given feature at the given location.
    * During world generation, features are provided with a 3x3 region of chunks, centered on the chunk being generated,
    * that they can safely generate into.
    * @param pContext A context object with a reference to the level and the position the feature is being placed at
    */
   public boolean place(FeaturePlaceContext<RootSystemConfiguration> pContext) {
      WorldGenLevel worldgenlevel = pContext.level();
      BlockPos blockpos = pContext.origin();
      if (!worldgenlevel.getBlockState(blockpos).isAir()) {
         return false;
      } else {
         Random random = pContext.random();
         BlockPos blockpos1 = pContext.origin();
         RootSystemConfiguration rootsystemconfiguration = pContext.config();
         BlockPos.MutableBlockPos blockpos$mutableblockpos = blockpos1.mutable();
         if (this.placeDirtAndTree(worldgenlevel, pContext.chunkGenerator(), rootsystemconfiguration, random, blockpos$mutableblockpos, blockpos1)) {
            this.placeRoots(worldgenlevel, rootsystemconfiguration, random, blockpos1, blockpos$mutableblockpos);
         }

         return true;
      }
   }

   private boolean spaceForTree(WorldGenLevel pLevel, RootSystemConfiguration pConfig, BlockPos pPos) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = pPos.mutable();

      for(int i = 1; i <= pConfig.requiredVerticalSpaceForTree; ++i) {
         blockpos$mutableblockpos.move(Direction.UP);
         BlockState blockstate = pLevel.getBlockState(blockpos$mutableblockpos);
         if (!isAllowedTreeSpace(blockstate, i, pConfig.allowedVerticalWaterForTree)) {
            return false;
         }
      }

      return true;
   }

   private static boolean isAllowedTreeSpace(BlockState pState, int pY, int pAllowedVerticalWater) {
      return pState.isAir() || pY <= pAllowedVerticalWater && pState.getFluidState().is(FluidTags.WATER);
   }

   private boolean placeDirtAndTree(WorldGenLevel pLevel, ChunkGenerator pGenerator, RootSystemConfiguration pConfig, Random pRandom, BlockPos.MutableBlockPos pMutablePos, BlockPos pBasePos) {
      int i = pBasePos.getX();
      int j = pBasePos.getZ();

      for(int k = 0; k < pConfig.rootColumnMaxHeight; ++k) {
         pMutablePos.move(Direction.UP);
         if (TreeFeature.validTreePos(pLevel, pMutablePos)) {
            if (this.spaceForTree(pLevel, pConfig, pMutablePos)) {
               BlockPos blockpos = pMutablePos.below();
               if (pLevel.getFluidState(blockpos).is(FluidTags.LAVA) || !pLevel.getBlockState(blockpos).getMaterial().isSolid()) {
                  return false;
               }

               if (this.tryPlaceAzaleaTree(pLevel, pGenerator, pConfig, pRandom, pMutablePos)) {
                  return true;
               }
            }
         } else {
            this.placeRootedDirt(pLevel, pConfig, pRandom, i, j, pMutablePos);
         }
      }

      return false;
   }

   private boolean tryPlaceAzaleaTree(WorldGenLevel pLevel, ChunkGenerator pChunkGenerator, RootSystemConfiguration pConfig, Random pRandom, BlockPos pPos) {
      return pConfig.treeFeature.get().place(pLevel, pChunkGenerator, pRandom, pPos);
   }

   private void placeRootedDirt(WorldGenLevel p_160240_, RootSystemConfiguration p_160241_, Random p_160242_, int p_160243_, int p_160244_, BlockPos.MutableBlockPos p_160245_) {
      int i = p_160241_.rootRadius;
      Tag<Block> tag = BlockTags.getAllTags().getTag(p_160241_.rootReplaceable);
      Predicate<BlockState> predicate = tag == null ? (p_160259_) -> {
         return true;
      } : (p_160221_) -> {
         return p_160221_.is(tag);
      };

      for(int j = 0; j < p_160241_.rootPlacementAttempts; ++j) {
         p_160245_.setWithOffset(p_160245_, p_160242_.nextInt(i) - p_160242_.nextInt(i), 0, p_160242_.nextInt(i) - p_160242_.nextInt(i));
         if (predicate.test(p_160240_.getBlockState(p_160245_))) {
            p_160240_.setBlock(p_160245_, p_160241_.rootStateProvider.getState(p_160242_, p_160245_), 2);
         }

         p_160245_.setX(p_160243_);
         p_160245_.setZ(p_160244_);
      }

   }

   private void placeRoots(WorldGenLevel pLevel, RootSystemConfiguration pConfig, Random pRandom, BlockPos pBasePos, BlockPos.MutableBlockPos pMutablePos) {
      int i = pConfig.hangingRootRadius;
      int j = pConfig.hangingRootsVerticalSpan;

      for(int k = 0; k < pConfig.hangingRootPlacementAttempts; ++k) {
         pMutablePos.setWithOffset(pBasePos, pRandom.nextInt(i) - pRandom.nextInt(i), pRandom.nextInt(j) - pRandom.nextInt(j), pRandom.nextInt(i) - pRandom.nextInt(i));
         if (pLevel.isEmptyBlock(pMutablePos)) {
            BlockState blockstate = pConfig.hangingRootStateProvider.getState(pRandom, pMutablePos);
            if (blockstate.canSurvive(pLevel, pMutablePos) && pLevel.getBlockState(pMutablePos.above()).isFaceSturdy(pLevel, pMutablePos, Direction.DOWN)) {
               pLevel.setBlock(pMutablePos, blockstate, 2);
            }
         }
      }

   }
}