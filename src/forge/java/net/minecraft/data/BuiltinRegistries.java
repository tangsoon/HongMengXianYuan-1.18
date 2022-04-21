package net.minecraft.data;

import com.google.common.collect.Maps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.Features;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.data.worldgen.SurfaceBuilders;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BuiltinRegistries {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final Map<ResourceLocation, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
   private static final WritableRegistry<WritableRegistry<?>> WRITABLE_REGISTRY = new MappedRegistry<>(ResourceKey.createRegistryKey(new ResourceLocation("root")), Lifecycle.experimental());
   public static final Registry<? extends Registry<?>> REGISTRY = WRITABLE_REGISTRY;
   public static final Registry<ConfiguredSurfaceBuilder<?>> CONFIGURED_SURFACE_BUILDER = registerSimple(Registry.CONFIGURED_SURFACE_BUILDER_REGISTRY, () -> {
      return SurfaceBuilders.NOPE;
   });
   public static final Registry<ConfiguredWorldCarver<?>> CONFIGURED_CARVER = registerSimple(Registry.CONFIGURED_CARVER_REGISTRY, () -> {
      return Carvers.CAVE;
   });
   public static final Registry<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE = registerSimple(Registry.CONFIGURED_FEATURE_REGISTRY, () -> {
      return Features.OAK;
   });
   public static final Registry<ConfiguredStructureFeature<?, ?>> CONFIGURED_STRUCTURE_FEATURE = registerSimple(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, () -> {
      return StructureFeatures.MINESHAFT;
   });
   public static final Registry<StructureProcessorList> PROCESSOR_LIST = registerSimple(Registry.PROCESSOR_LIST_REGISTRY, () -> {
      return ProcessorLists.ZOMBIE_PLAINS;
   });
   public static final Registry<StructureTemplatePool> TEMPLATE_POOL = registerSimple(Registry.TEMPLATE_POOL_REGISTRY, Pools::bootstrap);
   @Deprecated public static final Registry<Biome> BIOME = forge(Registry.BIOME_REGISTRY, () -> {
      return Biomes.PLAINS;
   });
   public static final Registry<NoiseGeneratorSettings> NOISE_GENERATOR_SETTINGS = registerSimple(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, NoiseGeneratorSettings::bootstrap);

   private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> pRegistryKey, Supplier<T> pDefaultSupplier) {
      return registerSimple(pRegistryKey, Lifecycle.stable(), pDefaultSupplier);
   }

   private static <T extends net.minecraftforge.registries.IForgeRegistryEntry<T>> Registry<T> forge(ResourceKey<? extends Registry<T>> key, Supplier<T> def) {
      return internalRegister(key, net.minecraftforge.registries.GameData.getWrapper(key, Lifecycle.stable()), def, Lifecycle.stable());
   }

   /**
    * Creates a new simple registry and registers it
    */
   private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> pRegistryKey, Lifecycle pLifecycle, Supplier<T> pDefaultSupplier) {
      return internalRegister(pRegistryKey, new MappedRegistry<>(pRegistryKey, pLifecycle), pDefaultSupplier, pLifecycle);
   }

   /**
    * Registers the passed registry
    */
   private static <T, R extends WritableRegistry<T>> R internalRegister(ResourceKey<? extends Registry<T>> pRegistryKey, R pRegistry, Supplier<T> pDefaultSupplier, Lifecycle pLifecycle) {
      ResourceLocation resourcelocation = pRegistryKey.location();
      LOADERS.put(resourcelocation, pDefaultSupplier);
      WritableRegistry<R> writableregistry = (WritableRegistry<R>)WRITABLE_REGISTRY;
      return (R)writableregistry.register((ResourceKey)pRegistryKey, pRegistry, pLifecycle);
   }

   /**
    * Creates a new simple registry and registers it
    */
   public static <T> T register(Registry<? super T> pRegistry, String pId, T pValue) {
      return register(pRegistry, new ResourceLocation(pId), pValue);
   }

   /**
    * Registers the given value to the registry
    */
   public static <V, T extends V> T register(Registry<V> pRegistry, ResourceLocation pId, T pValue) {
      return ((WritableRegistry<V>)pRegistry).register(ResourceKey.create(pRegistry.key(), pId), pValue, Lifecycle.stable());
   }

   /**
    * Registers the given value to the registry
    */
   public static <V, T extends V> T registerMapping(Registry<V> pRegistry, int pIndex, ResourceKey<V> pRegistryKey, T pValue) {
      return ((WritableRegistry<V>)pRegistry).registerMapping(pIndex, pRegistryKey, pValue, Lifecycle.stable());
   }

   /**
    * Dummy method to ensure all static variables are loaded before Registry loads registries.
    */
   public static void bootstrap() {
   }

   static {
      LOADERS.forEach((p_123897_, p_123898_) -> {
         if (p_123898_.get() == null) {
            LOGGER.error("Unable to bootstrap registry '{}'", (Object)p_123897_);
         }

      });
      Registry.checkRegistry(WRITABLE_REGISTRY);
   }
}
