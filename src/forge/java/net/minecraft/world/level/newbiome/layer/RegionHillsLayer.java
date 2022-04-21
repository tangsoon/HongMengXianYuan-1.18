package net.minecraft.world.level.newbiome.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer2;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset1Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum RegionHillsLayer implements AreaTransformer2, DimensionOffset1Transformer {
   INSTANCE;

   private static final Logger LOGGER = LogManager.getLogger();
   private static final Int2IntMap MUTATIONS = Util.make(new Int2IntOpenHashMap(), (p_76844_) -> {
      p_76844_.put(1, 129);
      p_76844_.put(2, 130);
      p_76844_.put(3, 131);
      p_76844_.put(4, 132);
      p_76844_.put(5, 133);
      p_76844_.put(6, 134);
      p_76844_.put(12, 140);
      p_76844_.put(21, 149);
      p_76844_.put(23, 151);
      p_76844_.put(27, 155);
      p_76844_.put(28, 156);
      p_76844_.put(29, 157);
      p_76844_.put(30, 158);
      p_76844_.put(32, 160);
      p_76844_.put(33, 161);
      p_76844_.put(34, 162);
      p_76844_.put(35, 163);
      p_76844_.put(36, 164);
      p_76844_.put(37, 165);
      p_76844_.put(38, 166);
      p_76844_.put(39, 167);
   });

   public int applyPixel(Context pContext, Area pFirstArea, Area pSecondArea, int pX, int pY) {
      int i = pFirstArea.get(this.getParentX(pX + 1), this.getParentY(pY + 1));
      int j = pSecondArea.get(this.getParentX(pX + 1), this.getParentY(pY + 1));
      if (i > 255) {
         LOGGER.debug("old! {}", (int)i);
      }

      int k = (j - 2) % 29;
      if (!Layers.isShallowOcean(i) && j >= 2 && k == 1) {
         return MUTATIONS.getOrDefault(i, i);
      } else {
         if (pContext.nextRandom(3) == 0 || k == 0) {
            int l = i;
            if (i == 2) {
               l = 17;
            } else if (i == 4) {
               l = 18;
            } else if (i == 27) {
               l = 28;
            } else if (i == 29) {
               l = 1;
            } else if (i == 5) {
               l = 19;
            } else if (i == 32) {
               l = 33;
            } else if (i == 30) {
               l = 31;
            } else if (i == 1) {
               l = pContext.nextRandom(3) == 0 ? 18 : 4;
            } else if (i == 12) {
               l = 13;
            } else if (i == 21) {
               l = 22;
            } else if (i == 168) {
               l = 169;
            } else if (i == 0) {
               l = 24;
            } else if (i == 45) {
               l = 48;
            } else if (i == 46) {
               l = 49;
            } else if (i == 10) {
               l = 50;
            } else if (i == 3) {
               l = 34;
            } else if (i == 35) {
               l = 36;
            } else if (Layers.isSame(i, 38)) {
               l = 37;
            } else if ((i == 24 || i == 48 || i == 49 || i == 50) && pContext.nextRandom(3) == 0) {
               l = pContext.nextRandom(2) == 0 ? 1 : 4;
            }

            if (k == 0 && l != i) {
               l = MUTATIONS.getOrDefault(l, i);
            }

            if (l != i) {
               int i1 = 0;
               if (Layers.isSame(pFirstArea.get(this.getParentX(pX + 1), this.getParentY(pY + 0)), i)) {
                  ++i1;
               }

               if (Layers.isSame(pFirstArea.get(this.getParentX(pX + 2), this.getParentY(pY + 1)), i)) {
                  ++i1;
               }

               if (Layers.isSame(pFirstArea.get(this.getParentX(pX + 0), this.getParentY(pY + 1)), i)) {
                  ++i1;
               }

               if (Layers.isSame(pFirstArea.get(this.getParentX(pX + 1), this.getParentY(pY + 2)), i)) {
                  ++i1;
               }

               if (i1 >= 3) {
                  return l;
               }
            }
         }

         return i;
      }
   }
}