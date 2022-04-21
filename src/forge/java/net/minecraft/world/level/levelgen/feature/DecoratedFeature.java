package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class DecoratedFeature extends Feature<DecoratedFeatureConfiguration> {
   public DecoratedFeature(Codec<DecoratedFeatureConfiguration> p_65495_) {
      super(p_65495_);
   }

   /**
    * Places the given feature at the given location.
    * During world generation, features are provided with a 3x3 region of chunks, centered on the chunk being generated,
    * that they can safely generate into.
    * @param pContext A context object with a reference to the level and the position the feature is being placed at
    */
   public boolean place(FeaturePlaceContext<DecoratedFeatureConfiguration> pContext) {
      MutableBoolean mutableboolean = new MutableBoolean();
      WorldGenLevel worldgenlevel = pContext.level();
      DecoratedFeatureConfiguration decoratedfeatureconfiguration = pContext.config();
      ChunkGenerator chunkgenerator = pContext.chunkGenerator();
      Random random = pContext.random();
      BlockPos blockpos = pContext.origin();
      ConfiguredFeature<?, ?> configuredfeature = decoratedfeatureconfiguration.feature.get();
      decoratedfeatureconfiguration.decorator.getPositions(new DecorationContext(worldgenlevel, chunkgenerator), random, blockpos).forEach((p_159543_) -> {
         if (configuredfeature.place(worldgenlevel, chunkgenerator, random, p_159543_)) {
            mutableboolean.setTrue();
         }

      });
      return mutableboolean.isTrue();
   }

   public String toString() {
      return String.format("< %s [%s] >", this.getClass().getSimpleName(), Registry.FEATURE.getKey(this));
   }
}