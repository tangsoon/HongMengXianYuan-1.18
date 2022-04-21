package net.minecraft.world.level.levelgen;

import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.ChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public interface Decoratable<R> {
   R decorated(ConfiguredDecorator<?> pDecorator);

   default R rarity(int pRarity) {
      return this.decorated(FeatureDecorator.CHANCE.configured(new ChanceDecoratorConfiguration(pRarity)));
   }

   default R count(IntProvider pCount) {
      return this.decorated(FeatureDecorator.COUNT.configured(new CountConfiguration(pCount)));
   }

   default R count(int pCount) {
      return this.count(ConstantInt.of(pCount));
   }

   default R countRandom(int pCount) {
      return this.count(UniformInt.of(0, pCount));
   }

   default R rangeUniform(VerticalAnchor pMinInclusive, VerticalAnchor pMaxInclusive) {
      return this.range(new RangeDecoratorConfiguration(UniformHeight.of(pMinInclusive, pMaxInclusive)));
   }

   default R rangeTriangle(VerticalAnchor pMinInclusive, VerticalAnchor pMaxInclusive) {
      return this.range(new RangeDecoratorConfiguration(TrapezoidHeight.of(pMinInclusive, pMaxInclusive)));
   }

   default R range(RangeDecoratorConfiguration pConfig) {
      return this.decorated(FeatureDecorator.RANGE.configured(pConfig));
   }

   default R squared() {
      return this.decorated(FeatureDecorator.SQUARE.configured(NoneDecoratorConfiguration.INSTANCE));
   }
}