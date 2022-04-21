package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public abstract class VerticalDecorator<DC extends DecoratorConfiguration> extends FeatureDecorator<DC> {
   public VerticalDecorator(Codec<DC> p_162323_) {
      super(p_162323_);
   }

   protected abstract int y(DecorationContext pContext, Random pRandom, DC pConfig, int pY);

   /**
    * Applies this decorator to the given position, and returns a stream of (potentially multiple) possible decorated
    * positions.
    */
   public final Stream<BlockPos> getPositions(DecorationContext pContext, Random pRandom, DC pConfig, BlockPos pPos) {
      return Stream.of(new BlockPos(pPos.getX(), this.y(pContext, pRandom, pConfig, pPos.getY()), pPos.getZ()));
   }
}