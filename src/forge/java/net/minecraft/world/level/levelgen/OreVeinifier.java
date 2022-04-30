package net.minecraft.world.level.levelgen;

import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class OreVeinifier {
   private static final float RARITY = 1.0F;
   private static final float RIDGE_NOISE_FREQUENCY = 4.0F;
   private static final float THICKNESS = 0.08F;
   private static final float VEININESS_THRESHOLD = 0.5F;
   private static final double VEININESS_FREQUENCY = 1.5D;
   private static final int EDGE_ROUNDOFF_BEGIN = 20;
   private static final double MAX_EDGE_ROUNDOFF = 0.2D;
   private static final float VEIN_SOLIDNESS = 0.7F;
   private static final float MIN_RICHNESS = 0.1F;
   private static final float MAX_RICHNESS = 0.3F;
   private static final float MAX_RICHNESS_THRESHOLD = 0.6F;
   private static final float CHANCE_OF_RAW_ORE_BLOCK = 0.02F;
   private static final float SKIP_ORE_IF_GAP_NOISE_IS_BELOW = -0.3F;
   private final int veinMaxY;
   private final int veinMinY;
   private final BlockState normalBlock;
   private final NormalNoise veininessNoiseSource;
   private final NormalNoise veinANoiseSource;
   private final NormalNoise veinBNoiseSource;
   private final NormalNoise gapNoise;
   private final int cellWidth;
   private final int cellHeight;

   public OreVeinifier(long pSeed, BlockState pNormalBlock, int pCellWidth, int pCellHeight, int pMinY) {
      Random random = new Random(pSeed);
      this.normalBlock = pNormalBlock;
      this.veininessNoiseSource = NormalNoise.create(new SimpleRandomSource(random.nextLong()), -8, 1.0D);
      this.veinANoiseSource = NormalNoise.create(new SimpleRandomSource(random.nextLong()), -7, 1.0D);
      this.veinBNoiseSource = NormalNoise.create(new SimpleRandomSource(random.nextLong()), -7, 1.0D);
      this.gapNoise = NormalNoise.create(new SimpleRandomSource(0L), -5, 1.0D);
      this.cellWidth = pCellWidth;
      this.cellHeight = pCellHeight;
      this.veinMaxY = Stream.of(OreVeinifier.VeinType.values()).mapToInt((p_158842_) -> {
         return p_158842_.maxY;
      }).max().orElse(pMinY);
      this.veinMinY = Stream.of(OreVeinifier.VeinType.values()).mapToInt((p_158818_) -> {
         return p_158818_.minY;
      }).min().orElse(pMinY);
   }

   public void fillVeininessNoiseColumn(double[] pNoiseValues, int pCellX, int pCellZ, int pMinCellY, int pCellCountY) {
      this.fillNoiseColumn(pNoiseValues, pCellX, pCellZ, this.veininessNoiseSource, 1.5D, pMinCellY, pCellCountY);
   }

   public void fillNoiseColumnA(double[] pNoiseValues, int pCellX, int pCellZ, int pMinCellY, int pCellCountY) {
      this.fillNoiseColumn(pNoiseValues, pCellX, pCellZ, this.veinANoiseSource, 4.0D, pMinCellY, pCellCountY);
   }

   public void fillNoiseColumnB(double[] pNoiseValues, int pCellX, int pCellZ, int pMinCellY, int pCellCountY) {
      this.fillNoiseColumn(pNoiseValues, pCellX, pCellZ, this.veinBNoiseSource, 4.0D, pMinCellY, pCellCountY);
   }

   public void fillNoiseColumn(double[] pNoiseValues, int pCellX, int pCellZ, NormalNoise pNoise, double pFrequency, int pMinCellY, int pCellCountY) {
      for(int i = 0; i < pCellCountY; ++i) {
         int j = i + pMinCellY;
         int k = pCellX * this.cellWidth;
         int l = j * this.cellHeight;
         int i1 = pCellZ * this.cellWidth;
         double d0;
         if (l >= this.veinMinY && l <= this.veinMaxY) {
            d0 = pNoise.getValue((double)k * pFrequency, (double)l * pFrequency, (double)i1 * pFrequency);
         } else {
            d0 = 0.0D;
         }

         pNoiseValues[i] = d0;
      }

   }

   public BlockState oreVeinify(RandomSource pRandom, int pX, int pY, int pZ, double pVeininess, double pVeinA, double pVeinB) {
      BlockState blockstate = this.normalBlock;
      OreVeinifier.VeinType oreveinifier$veintype = this.getVeinType(pVeininess, pY);
      if (oreveinifier$veintype == null) {
         return blockstate;
      } else if (pRandom.nextFloat() > 0.7F) {
         return blockstate;
      } else if (this.isVein(pVeinA, pVeinB)) {
         double d0 = Mth.clampedMap(Math.abs(pVeininess), 0.5D, (double)0.6F, (double)0.1F, (double)0.3F);
         if ((double)pRandom.nextFloat() < d0 && this.gapNoise.getValue((double)pX, (double)pY, (double)pZ) > (double)-0.3F) {
            return pRandom.nextFloat() < 0.02F ? oreveinifier$veintype.rawOreBlock : oreveinifier$veintype.ore;
         } else {
            return oreveinifier$veintype.filler;
         }
      } else {
         return blockstate;
      }
   }

   private boolean isVein(double pVeinA, double pVeinB) {
      double d0 = Math.abs(1.0D * pVeinA) - (double)0.08F;
      double d1 = Math.abs(1.0D * pVeinB) - (double)0.08F;
      return Math.max(d0, d1) < 0.0D;
   }

   @Nullable
   private OreVeinifier.VeinType getVeinType(double pVeininess, int pY) {
      OreVeinifier.VeinType oreveinifier$veintype = pVeininess > 0.0D ? OreVeinifier.VeinType.COPPER : OreVeinifier.VeinType.IRON;
      int i = oreveinifier$veintype.maxY - pY;
      int j = pY - oreveinifier$veintype.minY;
      if (j >= 0 && i >= 0) {
         int k = Math.min(i, j);
         double d0 = Mth.clampedMap((double)k, 0.0D, 20.0D, -0.2D, 0.0D);
         return Math.abs(pVeininess) + d0 < 0.5D ? null : oreveinifier$veintype;
      } else {
         return null;
      }
   }

   static enum VeinType {
      COPPER(Blocks.COPPER_ORE.defaultBlockState(), Blocks.RAW_COPPER_BLOCK.defaultBlockState(), Blocks.GRANITE.defaultBlockState(), 0, 50),
      IRON(Blocks.DEEPSLATE_IRON_ORE.defaultBlockState(), Blocks.RAW_IRON_BLOCK.defaultBlockState(), Blocks.TUFF.defaultBlockState(), -60, -8);

      final BlockState ore;
      final BlockState rawOreBlock;
      final BlockState filler;
      final int minY;
      final int maxY;

      private VeinType(BlockState p_158867_, BlockState p_158868_, BlockState p_158869_, int p_158870_, int p_158871_) {
         this.ore = p_158867_;
         this.rawOreBlock = p_158868_;
         this.filler = p_158869_;
         this.minY = p_158870_;
         this.maxY = p_158871_;
      }
   }
}