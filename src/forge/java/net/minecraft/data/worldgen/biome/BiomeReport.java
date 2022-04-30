package net.minecraft.data.worldgen.biome;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeReport implements DataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator generator;

   public BiomeReport(DataGenerator pGenerator) {
      this.generator = pGenerator;
   }

   /**
    * Performs this provider's action.
    */
   public void run(HashCache pCache) {
      Path path = this.generator.getOutputFolder();

      for(Entry<ResourceKey<Biome>, Biome> entry : BuiltinRegistries.BIOME.entrySet()) {
         Path path1 = createPath(path, entry.getKey().location());
         Biome biome = entry.getValue();
         Function<Supplier<Biome>, DataResult<JsonElement>> function = JsonOps.INSTANCE.withEncoder(Biome.CODEC);

         try {
            Optional<JsonElement> optional = function.apply(() -> {
               return biome;
            }).result();
            if (optional.isPresent()) {
               DataProvider.save(GSON, pCache, optional.get(), path1);
            } else {
               LOGGER.error("Couldn't serialize biome {}", (Object)path1);
            }
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't save biome {}", path1, ioexception);
         }
      }

   }

   private static Path createPath(Path pPath, ResourceLocation pBiomeLocation) {
      return pPath.resolve("reports/biomes/" + pBiomeLocation.getPath() + ".json");
   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public String getName() {
      return "Biomes";
   }
}