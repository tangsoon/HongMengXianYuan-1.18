package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.Column;

public class CaveSurfaceDecorator extends FeatureDecorator<CaveDecoratorConfiguration> {
   public CaveSurfaceDecorator(Codec<CaveDecoratorConfiguration> p_162117_) {
      super(p_162117_);
   }

   /**
    * Applies this decorator to the given position, and returns a stream of (potentially multiple) possible decorated
    * positions.
    */
   public Stream<BlockPos> getPositions(DecorationContext pContext, Random pRandom, CaveDecoratorConfiguration pConfig, BlockPos pPos) {
      Optional<Column> optional = Column.scan(pContext.getLevel(), pPos, pConfig.floorToCeilingSearchRange, BlockBehaviour.BlockStateBase::isAir, (p_162119_) -> {
         return p_162119_.getMaterial().isSolid();
      });
      if (!optional.isPresent()) {
         return Stream.of();
      } else {
         OptionalInt optionalint = pConfig.surface == CaveSurface.CEILING ? optional.get().getCeiling() : optional.get().getFloor();
         return !optionalint.isPresent() ? Stream.of() : Stream.of(pPos.atY(optionalint.getAsInt() - pConfig.surface.getY()));
      }
   }
}