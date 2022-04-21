package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

public class DefaultFlowerFeature extends AbstractFlowerFeature<RandomPatchConfiguration> {
   public DefaultFlowerFeature(Codec<RandomPatchConfiguration> p_65517_) {
      super(p_65517_);
   }

   public boolean isValid(LevelAccessor pLevel, BlockPos pPos, RandomPatchConfiguration pConfig) {
      return !pConfig.blacklist.contains(pLevel.getBlockState(pPos));
   }

   public int getCount(RandomPatchConfiguration pConfig) {
      return pConfig.tries;
   }

   public BlockPos getPos(Random pRandom, BlockPos pPos, RandomPatchConfiguration pConfig) {
      return pPos.offset(pRandom.nextInt(pConfig.xspread) - pRandom.nextInt(pConfig.xspread), pRandom.nextInt(pConfig.yspread) - pRandom.nextInt(pConfig.yspread), pRandom.nextInt(pConfig.zspread) - pRandom.nextInt(pConfig.zspread));
   }

   public BlockState getRandomFlower(Random pRandom, BlockPos pPos, RandomPatchConfiguration pConfgi) {
      return pConfgi.stateProvider.getState(pRandom, pPos);
   }
}