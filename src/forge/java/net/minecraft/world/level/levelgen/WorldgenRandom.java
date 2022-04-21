package net.minecraft.world.level.levelgen;

import java.util.Random;

public class WorldgenRandom extends Random implements RandomSource {
   private int count;

   public WorldgenRandom() {
   }

   public WorldgenRandom(long pSeed) {
      super(pSeed);
   }

   public int getCount() {
      return this.count;
   }

   public int next(int pBits) {
      ++this.count;
      return super.next(pBits);
   }

   /**
    * Seeds the current random for surface building and bedrock placement. Notably, this does not depend on the level
    * seed and only uses the chunk positions.
    * @return The seed.
    */
   public long setBaseChunkSeed(int pChunkX, int pChunkZ) {
      long i = (long)pChunkX * 341873128712L + (long)pChunkZ * 132897987541L;
      this.setSeed(i);
      return i;
   }

   /**
    * Seeds the current random for chunk decoration, including spawning mobs and for use in feature placement.
    * The coordinates correspond to the minimum block position within a given chunk.
    * @return The seed.
    */
   public long setDecorationSeed(long pLevelSeed, int pMinChunkBlockX, int pMinChunkBlockZ) {
      this.setSeed(pLevelSeed);
      long i = this.nextLong() | 1L;
      long j = this.nextLong() | 1L;
      long k = (long)pMinChunkBlockX * i + (long)pMinChunkBlockZ * j ^ pLevelSeed;
      this.setSeed(k);
      return k;
   }

   /**
    * Seeds the current random for placing features.
    * Each feature is seeded differently in order to seem more random. However it does not do a good job of this, and
    * issues can arise from the salt being small with features that have the same decoration step and are close together
    * in the feature lists.
    * @return The seed.
    * @param pDecorationSeed The seed computed by {@link #setDecorationSeed(long, int, int)}
    * @param pIndex The cumulative index of the generating feature within the biome's list of features.
    * @param pDecorationStep The ordinal of the {@link net.minecraft.world.level.levelgen.GenerationStep.Decoration} of
    * the generating feature.
    */
   public long setFeatureSeed(long pDecorationSeed, int pIndex, int pDecorationStep) {
      long i = pDecorationSeed + (long)pIndex + (long)(10000 * pDecorationStep);
      this.setSeed(i);
      return i;
   }

   /**
    * Seeds the current random for placing large features such as caves, strongholds, and mineshafts.
    * @return The seed.
    * @param pBaseSeed This is passed in as the level seed, or in some cases such as carvers, as an offset from the
    * level seed unique to each carver.
    */
   public long setLargeFeatureSeed(long pBaseSeed, int pChunkX, int pChunkZ) {
      this.setSeed(pBaseSeed);
      long i = this.nextLong();
      long j = this.nextLong();
      long k = (long)pChunkX * i ^ (long)pChunkZ * j ^ pBaseSeed;
      this.setSeed(k);
      return k;
   }

   /**
    * Seeds the current random for placing the base stone of an area (either stone or deepslate).
    * @return The seed.
    */
   public long setBaseStoneSeed(long pLevelSeed, int pX, int pY, int pZ) {
      this.setSeed(pLevelSeed);
      long i = this.nextLong();
      long j = this.nextLong();
      long k = this.nextLong();
      long l = (long)pX * i ^ (long)pY * j ^ (long)pZ * k ^ pLevelSeed;
      this.setSeed(l);
      return l;
   }

   /**
    * Seeds the current random for placing the starts of structure features.
    * The region coordinates are the region which the target chunk lies in. For example, witch hut regions are 32x32
    * chunks, so all chunks within that region would be seeded identically.
    * The size of the regions themselves are determined by the {@code spacing} of the structure settings.
    * @return The seed.
    * @param pSalt A salt unique to each structure.
    */
   public long setLargeFeatureWithSalt(long pLevelSeed, int pRegionX, int pRegionZ, int pSalt) {
      long i = (long)pRegionX * 341873128712L + (long)pRegionZ * 132897987541L + pLevelSeed + (long)pSalt;
      this.setSeed(i);
      return i;
   }

   /**
    * Creates a new {@code Random}, seeded for determining wether a chunk is a slime chunk or not.
    * @param pSalt For vanilla slimes this is always {@code 987234911L}
    */
   public static Random seedSlimeChunk(int pChunkX, int pChunkZ, long pLevelSeed, long pSalt) {
      return new Random(pLevelSeed + (long)(pChunkX * pChunkX * 4987142) + (long)(pChunkX * 5947611) + (long)(pChunkZ * pChunkZ) * 4392871L + (long)(pChunkZ * 389711) ^ pSalt);
   }
}