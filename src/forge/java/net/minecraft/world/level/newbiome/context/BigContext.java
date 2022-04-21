package net.minecraft.world.level.newbiome.context;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;

public interface BigContext<R extends Area> extends Context {
   void initRandom(long pX, long pZ);

   R createResult(PixelTransformer pTransformer);

   default R createResult(PixelTransformer pTransformer, R pArea) {
      return this.createResult(pTransformer);
   }

   default R createResult(PixelTransformer pTransformer, R pFirstArea, R pSecondArea) {
      return this.createResult(pTransformer);
   }

   /**
    * Chooses randomly between {@code first} and {@code second}.
    */
   default int random(int pFirst, int pSecond) {
      return this.nextRandom(2) == 0 ? pFirst : pSecond;
   }

   /**
    * Chooses randomly between four choices.
    */
   default int random(int pFirst, int pSecond, int pThird, int pFourth) {
      int i = this.nextRandom(4);
      if (i == 0) {
         return pFirst;
      } else if (i == 1) {
         return pSecond;
      } else {
         return i == 2 ? pThird : pFourth;
      }
   }
}