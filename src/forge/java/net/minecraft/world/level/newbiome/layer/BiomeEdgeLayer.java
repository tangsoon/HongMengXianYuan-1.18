package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum BiomeEdgeLayer implements CastleTransformer {
   INSTANCE;

   public int apply(Context pContext, int pNorth, int pWest, int pSouth, int pEast, int pCenter) {
      int[] aint = new int[1];
      if (!this.checkEdge(aint, pCenter) && !this.checkEdgeStrict(aint, pNorth, pWest, pSouth, pEast, pCenter, 38, 37) && !this.checkEdgeStrict(aint, pNorth, pWest, pSouth, pEast, pCenter, 39, 37) && !this.checkEdgeStrict(aint, pNorth, pWest, pSouth, pEast, pCenter, 32, 5)) {
         if (pCenter != 2 || pNorth != 12 && pWest != 12 && pEast != 12 && pSouth != 12) {
            if (pCenter == 6) {
               if (pNorth == 2 || pWest == 2 || pEast == 2 || pSouth == 2 || pNorth == 30 || pWest == 30 || pEast == 30 || pSouth == 30 || pNorth == 12 || pWest == 12 || pEast == 12 || pSouth == 12) {
                  return 1;
               }

               if (pNorth == 21 || pSouth == 21 || pWest == 21 || pEast == 21 || pNorth == 168 || pSouth == 168 || pWest == 168 || pEast == 168) {
                  return 23;
               }
            }

            return pCenter;
         } else {
            return 34;
         }
      } else {
         return aint[0];
      }
   }

   private boolean checkEdge(int[] pValue, int pEdge) {
      if (!Layers.isSame(pEdge, 3)) {
         return false;
      } else {
         pValue[0] = pEdge;
         return true;
      }
   }

   /**
    * Creates a border around a biome.
    */
   private boolean checkEdgeStrict(int[] pValue, int pNorth, int pEast, int pSouth, int pWest, int pCenter, int pCheck, int pEdge) {
      if (pCenter != pCheck) {
         return false;
      } else {
         if (Layers.isSame(pNorth, pCheck) && Layers.isSame(pEast, pCheck) && Layers.isSame(pWest, pCheck) && Layers.isSame(pSouth, pCheck)) {
            pValue[0] = pCenter;
         } else {
            pValue[0] = pEdge;
         }

         return true;
      }
   }
}