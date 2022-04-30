package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;

public class NetherForestVegetationFeature extends Feature<BlockPileConfiguration> {
   public NetherForestVegetationFeature(Codec<BlockPileConfiguration> p_66361_) {
      super(p_66361_);
   }

   /**
    * Places the given feature at the given location.
    * During world generation, features are provided with a 3x3 region of chunks, centered on the chunk being generated,
    * that they can safely generate into.
    * @param pContext A context object with a reference to the level and the position the feature is being placed at
    */
   public boolean place(FeaturePlaceContext<BlockPileConfiguration> pContext) {
      return place(pContext.level(), pContext.random(), pContext.origin(), pContext.config(), 8, 4);
   }

   public static boolean place(LevelAccessor pLevel, Random pRandom, BlockPos pPos, BlockPileConfiguration pConfig, int pHorizontalRadius, int pVerticalRadius) {
      BlockState blockstate = pLevel.getBlockState(pPos.below());
      if (!blockstate.is(BlockTags.NYLIUM)) {
         return false;
      } else {
         int i = pPos.getY();
         if (i >= pLevel.getMinBuildHeight() + 1 && i + 1 < pLevel.getMaxBuildHeight()) {
            int j = 0;

            for(int k = 0; k < pHorizontalRadius * pHorizontalRadius; ++k) {
               BlockPos blockpos = pPos.offset(pRandom.nextInt(pHorizontalRadius) - pRandom.nextInt(pHorizontalRadius), pRandom.nextInt(pVerticalRadius) - pRandom.nextInt(pVerticalRadius), pRandom.nextInt(pHorizontalRadius) - pRandom.nextInt(pHorizontalRadius));
               BlockState blockstate1 = pConfig.stateProvider.getState(pRandom, blockpos);
               if (pLevel.isEmptyBlock(blockpos) && blockpos.getY() > pLevel.getMinBuildHeight() && blockstate1.canSurvive(pLevel, blockpos)) {
                  pLevel.setBlock(blockpos, blockstate1, 2);
                  ++j;
               }
            }

            return j > 0;
         } else {
            return false;
         }
      }
   }
}