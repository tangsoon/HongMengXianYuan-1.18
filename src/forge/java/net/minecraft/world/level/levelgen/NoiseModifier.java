package net.minecraft.world.level.levelgen;

@FunctionalInterface
public interface NoiseModifier {
   NoiseModifier PASSTHROUGH = (p_158629_, p_158630_, p_158631_, p_158632_) -> {
      return p_158629_;
   };

   /**
    * Modifies the passed in noise value, at the given coordinates.
    * 
    * Note: in most uses of this function, these coordinates are ordered (x, y, z). However, notably, {@link Cavifier}
    * expects them in the order (y, z, x) despite implementing this interface.
    */
   double modifyNoise(double pNoise, int pX, int pY, int pZ);
}