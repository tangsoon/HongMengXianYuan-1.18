package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.SmallDripstoneConfiguration;

public class SmallDripstoneFeature extends Feature<SmallDripstoneConfiguration> {
   public SmallDripstoneFeature(Codec<SmallDripstoneConfiguration> p_160345_) {
      super(p_160345_);
   }

   /**
    * Places the given feature at the given location.
    * During world generation, features are provided with a 3x3 region of chunks, centered on the chunk being generated,
    * that they can safely generate into.
    * @param pContext A context object with a reference to the level and the position the feature is being placed at
    */
   public boolean place(FeaturePlaceContext<SmallDripstoneConfiguration> pContext) {
      WorldGenLevel worldgenlevel = pContext.level();
      BlockPos blockpos = pContext.origin();
      Random random = pContext.random();
      SmallDripstoneConfiguration smalldripstoneconfiguration = pContext.config();
      if (!DripstoneUtils.isEmptyOrWater(worldgenlevel, blockpos)) {
         return false;
      } else {
         int i = Mth.randomBetweenInclusive(random, 1, smalldripstoneconfiguration.maxPlacements);
         boolean flag = false;

         for(int j = 0; j < i; ++j) {
            BlockPos blockpos1 = randomOffset(random, blockpos, smalldripstoneconfiguration);
            if (searchAndTryToPlaceDripstone(worldgenlevel, random, blockpos1, smalldripstoneconfiguration)) {
               flag = true;
            }
         }

         return flag;
      }
   }

   private static boolean searchAndTryToPlaceDripstone(WorldGenLevel pLevel, Random pRandom, BlockPos pPos, SmallDripstoneConfiguration pConfig) {
      Direction direction = Direction.getRandom(pRandom);
      Direction direction1 = pRandom.nextBoolean() ? Direction.UP : Direction.DOWN;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = pPos.mutable();

      for(int i = 0; i < pConfig.emptySpaceSearchRadius; ++i) {
         if (!DripstoneUtils.isEmptyOrWater(pLevel, blockpos$mutableblockpos)) {
            return false;
         }

         if (tryToPlaceDripstone(pLevel, pRandom, blockpos$mutableblockpos, direction1, pConfig)) {
            return true;
         }

         if (tryToPlaceDripstone(pLevel, pRandom, blockpos$mutableblockpos, direction1.getOpposite(), pConfig)) {
            return true;
         }

         blockpos$mutableblockpos.move(direction);
      }

      return false;
   }

   private static boolean tryToPlaceDripstone(WorldGenLevel pLevel, Random pRandom, BlockPos pPos, Direction pDirection, SmallDripstoneConfiguration pConfig) {
      if (!DripstoneUtils.isEmptyOrWater(pLevel, pPos)) {
         return false;
      } else {
         BlockPos blockpos = pPos.relative(pDirection.getOpposite());
         BlockState blockstate = pLevel.getBlockState(blockpos);
         if (!DripstoneUtils.isDripstoneBase(blockstate)) {
            return false;
         } else {
            createPatchOfDripstoneBlocks(pLevel, pRandom, blockpos);
            int i = pRandom.nextFloat() < pConfig.chanceOfTallerDripstone && DripstoneUtils.isEmptyOrWater(pLevel, pPos.relative(pDirection)) ? 2 : 1;
            DripstoneUtils.growPointedDripstone(pLevel, pPos, pDirection, i, false);
            return true;
         }
      }
   }

   private static void createPatchOfDripstoneBlocks(WorldGenLevel pLevel, Random pRandom, BlockPos pPos) {
      DripstoneUtils.placeDripstoneBlockIfPossible(pLevel, pPos);

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (!(pRandom.nextFloat() < 0.3F)) {
            BlockPos blockpos = pPos.relative(direction);
            DripstoneUtils.placeDripstoneBlockIfPossible(pLevel, blockpos);
            if (!pRandom.nextBoolean()) {
               BlockPos blockpos1 = blockpos.relative(Direction.getRandom(pRandom));
               DripstoneUtils.placeDripstoneBlockIfPossible(pLevel, blockpos1);
               if (!pRandom.nextBoolean()) {
                  BlockPos blockpos2 = blockpos1.relative(Direction.getRandom(pRandom));
                  DripstoneUtils.placeDripstoneBlockIfPossible(pLevel, blockpos2);
               }
            }
         }
      }

   }

   private static BlockPos randomOffset(Random pRandom, BlockPos pPos, SmallDripstoneConfiguration pConfig) {
      return pPos.offset(Mth.randomBetweenInclusive(pRandom, -pConfig.maxOffsetFromOrigin, pConfig.maxOffsetFromOrigin), Mth.randomBetweenInclusive(pRandom, -pConfig.maxOffsetFromOrigin, pConfig.maxOffsetFromOrigin), Mth.randomBetweenInclusive(pRandom, -pConfig.maxOffsetFromOrigin, pConfig.maxOffsetFromOrigin));
   }
}