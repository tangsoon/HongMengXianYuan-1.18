package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum RiverLayer implements CastleTransformer {
   INSTANCE;

   public int apply(Context pContext, int pNorth, int pWest, int pSouth, int pEast, int pCenter) {
      int i = riverFilter(pCenter);
      return i == riverFilter(pEast) && i == riverFilter(pNorth) && i == riverFilter(pWest) && i == riverFilter(pSouth) ? -1 : 7;
   }

   private static int riverFilter(int pValue) {
      return pValue >= 2 ? 2 + (pValue & 1) : pValue;
   }
}