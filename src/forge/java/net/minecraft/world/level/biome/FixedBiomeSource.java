package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;

public class FixedBiomeSource extends BiomeSource {
   public static final Codec<FixedBiomeSource> CODEC = Biome.CODEC.fieldOf("biome").xmap(FixedBiomeSource::new, (p_48278_) -> {
      return p_48278_.biome;
   }).stable().codec();
   private final Supplier<Biome> biome;

   public FixedBiomeSource(Biome pBiome) {
      this(() -> {
         return pBiome;
      });
   }

   public FixedBiomeSource(Supplier<Biome> p_48257_) {
      super(ImmutableList.of(p_48257_.get()));
      this.biome = p_48257_;
   }

   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public BiomeSource withSeed(long pSeed) {
      return this;
   }

   /**
    * Gets the biome at the given quart positions.
    * Note that the coordinates passed into this method are 1/4 the scale of block coordinates. The noise biome is then
    * used by the {@link net.minecraft.world.level.biome.BiomeZoomer} to produce a biome for each unique position,
    * whilst only saving the biomes once per each 4x4x4 cube.
    */
   public Biome getNoiseBiome(int pX, int pY, int pZ) {
      return this.biome.get();
   }

   @Nullable
   public BlockPos findBiomeHorizontal(int pX, int pY, int pZ, int pRadius, int pIncrement, Predicate<Biome> pBiomes, Random pRandom, boolean pFindClosest) {
      if (pBiomes.test(this.biome.get())) {
         return pFindClosest ? new BlockPos(pX, pY, pZ) : new BlockPos(pX - pRadius + pRandom.nextInt(pRadius * 2 + 1), pY, pZ - pRadius + pRandom.nextInt(pRadius * 2 + 1));
      } else {
         return null;
      }
   }

   /**
    * @return The set of all biomes within a distance of {@code radius} blocks from the provided position.
    */
   public Set<Biome> getBiomesWithin(int pX, int pY, int pZ, int pRadius) {
      return Sets.newHashSet(this.biome.get());
   }
}