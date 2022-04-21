package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.BishopTransformer;

public enum AddIslandLayer implements BishopTransformer {
   INSTANCE;

   public int apply(Context pContext, int pX, int pSouthEast, int pNorthEast, int pNorthWest, int pCenter) {
      if (!Layers.isShallowOcean(pCenter) || Layers.isShallowOcean(pNorthWest) && Layers.isShallowOcean(pNorthEast) && Layers.isShallowOcean(pX) && Layers.isShallowOcean(pSouthEast)) {
         if (!Layers.isShallowOcean(pCenter) && (Layers.isShallowOcean(pNorthWest) || Layers.isShallowOcean(pX) || Layers.isShallowOcean(pNorthEast) || Layers.isShallowOcean(pSouthEast)) && pContext.nextRandom(5) == 0) {
            if (Layers.isShallowOcean(pNorthWest)) {
               return pCenter == 4 ? 4 : pNorthWest;
            }

            if (Layers.isShallowOcean(pX)) {
               return pCenter == 4 ? 4 : pX;
            }

            if (Layers.isShallowOcean(pNorthEast)) {
               return pCenter == 4 ? 4 : pNorthEast;
            }

            if (Layers.isShallowOcean(pSouthEast)) {
               return pCenter == 4 ? 4 : pSouthEast;
            }
         }

         return pCenter;
      } else {
         int i = 1;
         int j = 1;
         if (!Layers.isShallowOcean(pNorthWest) && pContext.nextRandom(i++) == 0) {
            j = pNorthWest;
         }

         if (!Layers.isShallowOcean(pNorthEast) && pContext.nextRandom(i++) == 0) {
            j = pNorthEast;
         }

         if (!Layers.isShallowOcean(pX) && pContext.nextRandom(i++) == 0) {
            j = pX;
         }

         if (!Layers.isShallowOcean(pSouthEast) && pContext.nextRandom(i++) == 0) {
            j = pSouthEast;
         }

         if (pContext.nextRandom(3) == 0) {
            return j;
         } else {
            return j == 4 ? 4 : pCenter;
         }
      }
   }
}