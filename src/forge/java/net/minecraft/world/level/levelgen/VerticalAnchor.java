package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * Represents a resolvable height value, or y coordinate, based on the world minimum and maximum height.
 * Can take one of the following three forms:
 * <ul>
 * <li>An absolute y value ({@link Absolute}).</li>
 * <li>A height above the lowest valid y value in the level ({@link AboveBottom}).</li>
 * <li>A height below the highest valid y value in the level ({@link BelowTop}).</li>
 * </ul>
 */
public abstract class VerticalAnchor {
   public static final Codec<VerticalAnchor> CODEC = ExtraCodecs.xor(VerticalAnchor.Absolute.CODEC, ExtraCodecs.xor(VerticalAnchor.AboveBottom.CODEC, VerticalAnchor.BelowTop.CODEC)).xmap(VerticalAnchor::merge, VerticalAnchor::split);
   private static final VerticalAnchor BOTTOM = aboveBottom(0);
   private static final VerticalAnchor TOP = belowTop(0);
   private final int value;

   protected VerticalAnchor(int pValue) {
      this.value = pValue;
   }

   public static VerticalAnchor absolute(int pValue) {
      return new VerticalAnchor.Absolute(pValue);
   }

   public static VerticalAnchor aboveBottom(int pValue) {
      return new VerticalAnchor.AboveBottom(pValue);
   }

   public static VerticalAnchor belowTop(int pValue) {
      return new VerticalAnchor.BelowTop(pValue);
   }

   public static VerticalAnchor bottom() {
      return BOTTOM;
   }

   public static VerticalAnchor top() {
      return TOP;
   }

   private static VerticalAnchor merge(Either<VerticalAnchor.Absolute, Either<VerticalAnchor.AboveBottom, VerticalAnchor.BelowTop>> p_158925_) {
      return p_158925_.map(Function.identity(), (p_158933_) -> {
         return p_158933_.map(Function.identity(), Function.identity());
      });
   }

   private static Either<VerticalAnchor.Absolute, Either<VerticalAnchor.AboveBottom, VerticalAnchor.BelowTop>> split(VerticalAnchor p_158927_) {
      return p_158927_ instanceof VerticalAnchor.Absolute ? Either.left((VerticalAnchor.Absolute)p_158927_) : Either.right(p_158927_ instanceof VerticalAnchor.AboveBottom ? Either.left((VerticalAnchor.AboveBottom)p_158927_) : Either.right((VerticalAnchor.BelowTop)p_158927_));
   }

   protected int value() {
      return this.value;
   }

   public abstract int resolveY(WorldGenerationContext pContext);

   static final class AboveBottom extends VerticalAnchor {
      public static final Codec<VerticalAnchor.AboveBottom> CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("above_bottom").xmap(VerticalAnchor.AboveBottom::new, VerticalAnchor::value).codec();

      protected AboveBottom(int p_158940_) {
         super(p_158940_);
      }

      public int resolveY(WorldGenerationContext p_158942_) {
         return p_158942_.getMinGenY() + this.value();
      }

      public String toString() {
         return this.value() + " above bottom";
      }
   }

   static final class Absolute extends VerticalAnchor {
      public static final Codec<VerticalAnchor.Absolute> CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("absolute").xmap(VerticalAnchor.Absolute::new, VerticalAnchor::value).codec();

      protected Absolute(int p_158947_) {
         super(p_158947_);
      }

      public int resolveY(WorldGenerationContext p_158949_) {
         return this.value();
      }

      public String toString() {
         return this.value() + " absolute";
      }
   }

   static final class BelowTop extends VerticalAnchor {
      public static final Codec<VerticalAnchor.BelowTop> CODEC = Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("below_top").xmap(VerticalAnchor.BelowTop::new, VerticalAnchor::value).codec();

      protected BelowTop(int p_158954_) {
         super(p_158954_);
      }

      public int resolveY(WorldGenerationContext p_158956_) {
         return p_158956_.getGenDepth() - 1 + p_158956_.getMinGenY() - this.value();
      }

      public String toString() {
         return this.value() + " below top";
      }
   }
}