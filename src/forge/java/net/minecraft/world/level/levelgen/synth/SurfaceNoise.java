package net.minecraft.world.level.levelgen.synth;

/**
 * The noise sampler used during surface building.
 * This value obtained from {@link #getSurfaceNoiseValue(double, double, double, double)} is used as the {@code noise}
 * parameter of {@link net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder#apply}.
 * <p>
 * Mojang samples this noise rather strangely, by passing in (blockX, blockZ, 0.0625, localX).
 * Since the localX only ranges from [0, 16), it can lead to axis aligned artifacts across chunk borders visible in the
 * surface material.
 * @see <a href="https://bugs.mojang.com/browse/MC-199343">MC-199343</a>
 */
public interface SurfaceNoise {
   double getSurfaceNoiseValue(double pX, double pY, double pYScale, double pYMax);
}