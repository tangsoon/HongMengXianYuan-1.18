package net.minecraft.world.level.levelgen;

import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class DepthBasedReplacingBaseStoneSource implements BaseStoneSource {
   private static final int ALWAYS_REPLACE_BELOW_Y = -8;
   private static final int NEVER_REPLACE_ABOVE_Y = 0;
   private final WorldgenRandom random;
   private final long seed;
   private final BlockState normalBlock;
   private final BlockState replacementBlock;
   private final NoiseGeneratorSettings settings;

   public DepthBasedReplacingBaseStoneSource(long pSeed, BlockState pNormalBlock, BlockState pReplacementBlock, NoiseGeneratorSettings pSettings) {
      this.random = new WorldgenRandom(pSeed);
      this.seed = pSeed;
      this.normalBlock = pNormalBlock;
      this.replacementBlock = pReplacementBlock;
      this.settings = pSettings;
   }

   public BlockState getBaseBlock(int pX, int pY, int pZ) {
      if (!this.settings.isDeepslateEnabled()) {
         return this.normalBlock;
      } else if (pY < -8) {
         return this.replacementBlock;
      } else if (pY > 0) {
         return this.normalBlock;
      } else {
         double d0 = Mth.map((double)pY, -8.0D, 0.0D, 1.0D, 0.0D);
         this.random.setBaseStoneSeed(this.seed, pX, pY, pZ);
         return (double)this.random.nextFloat() < d0 ? this.replacementBlock : this.normalBlock;
      }
   }
}