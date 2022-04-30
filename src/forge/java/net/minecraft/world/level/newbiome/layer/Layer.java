package net.minecraft.world.level.newbiome.layer;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.area.LazyArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Layer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final LazyArea area;

   public Layer(AreaFactory<LazyArea> pArea) {
      this.area = pArea.make();
   }

   public Biome get(Registry<Biome> pBiomeRegistry, int pX, int pZ) {
      int i = this.area.get(pX, pZ);
      ResourceKey<Biome> resourcekey = Biomes.byId(i);
      if (resourcekey == null) {
         throw new IllegalStateException("Unknown biome id emitted by layers: " + i);
      } else {
         Biome biome = pBiomeRegistry.get(resourcekey);
         if (biome == null) {
            Util.logAndPauseIfInIde("Unknown biome id: " + i);
            return pBiomeRegistry.get(Biomes.byId(0));
         } else {
            return biome;
         }
      }
   }
}