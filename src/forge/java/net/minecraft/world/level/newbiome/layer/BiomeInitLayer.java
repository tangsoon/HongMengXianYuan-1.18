package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.C0Transformer;

public class BiomeInitLayer implements C0Transformer {
   private static final int[] LEGACY_WARM_BIOMES = new int[]{2, 4, 3, 6, 1, 5};
   private static final int[] WARM_BIOMES = new int[]{2, 2, 2, 35, 35, 1};
   private static final int[] MEDIUM_BIOMES = new int[]{4, 29, 3, 1, 27, 6};
   private static final int[] COLD_BIOMES = new int[]{4, 3, 5, 1};
   private static final int[] ICE_BIOMES = new int[]{12, 12, 12, 30};
   private int[] warmBiomes = WARM_BIOMES;
   private final boolean legacyDesert;
   private java.util.List<net.minecraftforge.common.BiomeManager.BiomeEntry>[] biomes = new java.util.ArrayList[net.minecraftforge.common.BiomeManager.BiomeType.values().length];

   public BiomeInitLayer(boolean pUseLegacyWarmBiomes) {
      this.legacyDesert = pUseLegacyWarmBiomes;
      for (net.minecraftforge.common.BiomeManager.BiomeType type : net.minecraftforge.common.BiomeManager.BiomeType.values())
         biomes[type.ordinal()] = new java.util.ArrayList<>(net.minecraftforge.common.BiomeManager.getBiomes(type));
   }

   public int apply(Context pContext, int pValue) {
      int i = (pValue & 3840) >> 8;
      pValue = pValue & -3841;
      if (!Layers.isOcean(pValue) && pValue != 14) {
         switch(pValue) {
         case 1:
            if (i > 0) {
               return pContext.nextRandom(3) == 0 ? 39 : 38;
            }

            return getBiomeId(net.minecraftforge.common.BiomeManager.BiomeType.DESERT, pContext);
         case 2:
            if (i > 0) {
               return 21;
            }

            return getBiomeId(net.minecraftforge.common.BiomeManager.BiomeType.WARM, pContext);
         case 3:
            if (i > 0) {
               return 32;
            }

            return getBiomeId(net.minecraftforge.common.BiomeManager.BiomeType.COOL, pContext);
         case 4:
            return getBiomeId(net.minecraftforge.common.BiomeManager.BiomeType.ICY, pContext);
         default:
            return 14;
         }
      } else {
         return pValue;
      }
   }

   private int getBiomeId(net.minecraftforge.common.BiomeManager.BiomeType type, Context context) {
      return net.minecraft.data.BuiltinRegistries.BIOME.getId(
         net.minecraft.data.BuiltinRegistries.BIOME.get(getBiome(type, context)));
   }
   protected net.minecraft.resources.ResourceKey<net.minecraft.world.level.biome.Biome> getBiome(net.minecraftforge.common.BiomeManager.BiomeType type, Context context) {
      if (type == net.minecraftforge.common.BiomeManager.BiomeType.DESERT && this.legacyDesert)
         type = net.minecraftforge.common.BiomeManager.BiomeType.DESERT_LEGACY;
      java.util.List<net.minecraftforge.common.BiomeManager.BiomeEntry> biomeList = biomes[type.ordinal()];
      int totalWeight = net.minecraft.util.WeighedRandom.getTotalWeight(biomeList);
      int weight = net.minecraftforge.common.BiomeManager.isTypeListModded(type) ? context.nextRandom(totalWeight) : context.nextRandom(totalWeight / 10) * 10;
      return net.minecraft.util.WeighedRandom.getWeightedItem(biomeList, weight).get().getKey();
   }
}
