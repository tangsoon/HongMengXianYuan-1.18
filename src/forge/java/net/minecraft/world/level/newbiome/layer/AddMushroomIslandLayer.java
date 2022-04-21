package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.BishopTransformer;

public enum AddMushroomIslandLayer implements BishopTransformer {
   INSTANCE;

   public int apply(Context pContext, int pX, int pSouthEast, int pNorthEast, int pNorthWest, int pCenter) {
      return Layers.isShallowOcean(pCenter) && Layers.isShallowOcean(pNorthWest) && Layers.isShallowOcean(pX) && Layers.isShallowOcean(pNorthEast) && Layers.isShallowOcean(pSouthEast) && pContext.nextRandom(100) == 0 ? 14 : pCenter;
   }
}