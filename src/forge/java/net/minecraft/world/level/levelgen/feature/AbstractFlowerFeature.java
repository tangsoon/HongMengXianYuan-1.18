package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public abstract class AbstractFlowerFeature<U extends FeatureConfiguration> extends Feature<U> {
   public AbstractFlowerFeature(Codec<U> p_65075_) {
      super(p_65075_);
   }

   /**
    * Places the given feature at the given location.
    * During world generation, features are provided with a 3x3 region of chunks, centered on the chunk being generated,
    * that they can safely generate into.
    * @param pContext A context object with a reference to the level and the position the feature is being placed at
    */
   public boolean place(FeaturePlaceContext<U> pContext) {
      Random random = pContext.random();
      BlockPos blockpos = pContext.origin();
      WorldGenLevel worldgenlevel = pContext.level();
      U u = pContext.config();
      BlockState blockstate = this.getRandomFlower(random, blockpos, u);
      int i = 0;

      for(int j = 0; j < this.getCount(u); ++j) {
         BlockPos blockpos1 = this.getPos(random, blockpos, u);
         if (worldgenlevel.isEmptyBlock(blockpos1) && blockstate.canSurvive(worldgenlevel, blockpos1) && this.isValid(worldgenlevel, blockpos1, u)) {
            worldgenlevel.setBlock(blockpos1, blockstate, 2);
            ++i;
         }
      }

      return i > 0;
   }

   public abstract boolean isValid(LevelAccessor pLevel, BlockPos pPos, U pConfig);

   public abstract int getCount(U pConfig);

   public abstract BlockPos getPos(Random pRandom, BlockPos pPos, U pConfig);

   public abstract BlockState getRandomFlower(Random pRandom, BlockPos pPos, U pConfig);
}