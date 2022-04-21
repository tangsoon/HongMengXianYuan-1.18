package net.minecraft.world.level.levelgen;

/**
 * A basic interface for random number generation. This mirrors the same methods in {@link java.util.Random}, however it
 * does not make any guarantee that these are thread-safe, unlike {@code Random}.
 * The notable difference is that {@link #setSeed(long)} is not {@code synchronized} and should not be accessed from
 * multiple threads.
 * The documentation for each individual method can be assumed to be otherwise the same as the identically named method
 * in {@link java.util.Random}.
 * @see java.util.Random
 * @see net.minecraft.world.level.levelgen.SimpleRandomSource
 */
public interface RandomSource {
   void setSeed(long pSeed);

   int nextInt();

   int nextInt(int pBound);

   long nextLong();

   boolean nextBoolean();

   float nextFloat();

   double nextDouble();

   double nextGaussian();

   default void consumeCount(int pCount) {
      for(int i = 0; i < pCount; ++i) {
         this.nextInt();
      }

   }
}