package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public abstract class RepeatingDecorator<DC extends DecoratorConfiguration> extends FeatureDecorator<DC> {
   public RepeatingDecorator(Codec<DC> p_162290_) {
      super(p_162290_);
   }

   protected abstract int count(Random pRandom, DC pConfig, BlockPos pPos);

   /**
    * Applies this decorator to the given position, and returns a stream of (potentially multiple) possible decorated
    * positions.
    */
   public Stream<BlockPos> getPositions(DecorationContext pContext, Random pRandom, DC pConfig, BlockPos pPos) {
      return IntStream.range(0, this.count(pRandom, pConfig, pPos)).mapToObj((p_162298_) -> {
         return pPos;
      });
   }
}