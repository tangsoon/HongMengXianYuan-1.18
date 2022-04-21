package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class TwistingVinesFeature extends Feature<NoneFeatureConfiguration> {
   public TwistingVinesFeature(Codec<NoneFeatureConfiguration> p_67292_) {
      super(p_67292_);
   }

   /**
    * Places the given feature at the given location.
    * During world generation, features are provided with a 3x3 region of chunks, centered on the chunk being generated,
    * that they can safely generate into.
    * @param pContext A context object with a reference to the level and the position the feature is being placed at
    */
   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
      return place(pContext.level(), pContext.random(), pContext.origin(), 8, 4, 8);
   }

   public static boolean place(LevelAccessor pLevel, Random pRandom, BlockPos pPos, int pVerticalOffset, int pHorizontalOffset, int pHeight) {
      if (isInvalidPlacementLocation(pLevel, pPos)) {
         return false;
      } else {
         placeTwistingVines(pLevel, pRandom, pPos, pVerticalOffset, pHorizontalOffset, pHeight);
         return true;
      }
   }

   private static void placeTwistingVines(LevelAccessor pLevel, Random pRandom, BlockPos pPos, int pVerticalOffset, int pHorizontalOffset, int pHeight) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i = 0; i < pVerticalOffset * pVerticalOffset; ++i) {
         blockpos$mutableblockpos.set(pPos).move(Mth.nextInt(pRandom, -pVerticalOffset, pVerticalOffset), Mth.nextInt(pRandom, -pHorizontalOffset, pHorizontalOffset), Mth.nextInt(pRandom, -pVerticalOffset, pVerticalOffset));
         if (findFirstAirBlockAboveGround(pLevel, blockpos$mutableblockpos) && !isInvalidPlacementLocation(pLevel, blockpos$mutableblockpos)) {
            int j = Mth.nextInt(pRandom, 1, pHeight);
            if (pRandom.nextInt(6) == 0) {
               j *= 2;
            }

            if (pRandom.nextInt(5) == 0) {
               j = 1;
            }

            int k = 17;
            int l = 25;
            placeWeepingVinesColumn(pLevel, pRandom, blockpos$mutableblockpos, j, 17, 25);
         }
      }

   }

   private static boolean findFirstAirBlockAboveGround(LevelAccessor pLevel, BlockPos.MutableBlockPos pPos) {
      do {
         pPos.move(0, -1, 0);
         if (pLevel.isOutsideBuildHeight(pPos)) {
            return false;
         }
      } while(pLevel.getBlockState(pPos).isAir());

      pPos.move(0, 1, 0);
      return true;
   }

   public static void placeWeepingVinesColumn(LevelAccessor p_67300_, Random p_67301_, BlockPos.MutableBlockPos p_67302_, int p_67303_, int p_67304_, int p_67305_) {
      for(int i = 1; i <= p_67303_; ++i) {
         if (p_67300_.isEmptyBlock(p_67302_)) {
            if (i == p_67303_ || !p_67300_.isEmptyBlock(p_67302_.above())) {
               p_67300_.setBlock(p_67302_, Blocks.TWISTING_VINES.defaultBlockState().setValue(GrowingPlantHeadBlock.AGE, Integer.valueOf(Mth.nextInt(p_67301_, p_67304_, p_67305_))), 2);
               break;
            }

            p_67300_.setBlock(p_67302_, Blocks.TWISTING_VINES_PLANT.defaultBlockState(), 2);
         }

         p_67302_.move(Direction.UP);
      }

   }

   private static boolean isInvalidPlacementLocation(LevelAccessor pLevel, BlockPos pPos) {
      if (!pLevel.isEmptyBlock(pPos)) {
         return true;
      } else {
         BlockState blockstate = pLevel.getBlockState(pPos.below());
         return !blockstate.is(Blocks.NETHERRACK) && !blockstate.is(Blocks.WARPED_NYLIUM) && !blockstate.is(Blocks.WARPED_WART_BLOCK);
      }
   }
}