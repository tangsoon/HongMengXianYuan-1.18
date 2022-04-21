package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.Context;

/**
 * A transformer which merges two different areas.
 */
public interface AreaTransformer2 extends DimensionTransformer {
   default <R extends Area> AreaFactory<R> run(BigContext<R> pContext, AreaFactory<R> pFirstArea, AreaFactory<R> pSecondArea) {
      return () -> {
         R r = pFirstArea.make();
         R r1 = pSecondArea.make();
         return pContext.createResult((p_164653_, p_164654_) -> {
            pContext.initRandom((long)p_164653_, (long)p_164654_);
            return this.applyPixel(pContext, r, r1, p_164653_, p_164654_);
         }, r, r1);
      };
   }

   int applyPixel(Context pContext, Area pFirstArea, Area pSecondArea, int pX, int pY);
}