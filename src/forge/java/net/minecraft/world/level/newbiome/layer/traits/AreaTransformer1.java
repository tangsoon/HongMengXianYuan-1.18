package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.BigContext;

/**
 * A transformer which applies to a previous area and modifies it.
 */
public interface AreaTransformer1 extends DimensionTransformer {
   default <R extends Area> AreaFactory<R> run(BigContext<R> pContext, AreaFactory<R> pArea) {
      return () -> {
         R r = pArea.make();
         return pContext.createResult((p_164647_, p_164648_) -> {
            pContext.initRandom((long)p_164647_, (long)p_164648_);
            return this.applyPixel(pContext, r, p_164647_, p_164648_);
         }, r);
      };
   }

   int applyPixel(BigContext<?> pContext, Area pArea, int pX, int pY);
}