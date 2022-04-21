package net.minecraft.resources;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.DataResult.PartialResult;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A combination of a {@link DelegatingOps}, to do raw data reading, a {@link net.minecraft.core.RegistryAccess}, to
 * populate, and a {@link RegistryReadOps.ResourceAccess} which represents a organized set of encoded registry data, to
 * decode from.
 * In that sense, this ops wraps an entire encoded view of a registry, and it's companion decoding functionality, rather
 * than being a general purpose ops.
 */
public class RegistryReadOps<T> extends DelegatingOps<T> {
   static final Logger LOGGER = LogManager.getLogger();
   private static final String JSON = ".json";
   private final RegistryReadOps.ResourceAccess resources;
   public final RegistryAccess registryAccess;
   private final Map<ResourceKey<? extends Registry<?>>, RegistryReadOps.ReadCache<?>> readCache;
   private final RegistryReadOps<JsonElement> jsonOps;

   public static <T> RegistryReadOps<T> createAndLoad(DynamicOps<T> pDelegate, ResourceManager pResourceManager, RegistryAccess pRegistryAccess) {
      return createAndLoad(pDelegate, RegistryReadOps.ResourceAccess.forResourceManager(pResourceManager), pRegistryAccess);
   }

   /**
    * Creates a new {@link RegistryReadOps} with the provided resources as input. Then, loads the entirety of the
    * resources into the provided {@code registryAccess}.
    * The {@link RegistryReadOps} is returned but can be discarded after, as all resources will have been loaded into
    * the {@code registryAccess}.
    */
   public static <T> RegistryReadOps<T> createAndLoad(DynamicOps<T> pDelegate, RegistryReadOps.ResourceAccess pResources, RegistryAccess pRegistryAccess) {
      RegistryReadOps<T> registryreadops = new RegistryReadOps<>(pDelegate, pResources, pRegistryAccess, Maps.newIdentityHashMap());
      RegistryAccess.load(pRegistryAccess, registryreadops);
      return registryreadops;
   }

   public static <T> RegistryReadOps<T> create(DynamicOps<T> pDelegate, ResourceManager pResourceManager, RegistryAccess pRegistryAccess) {
      return create(pDelegate, RegistryReadOps.ResourceAccess.forResourceManager(pResourceManager), pRegistryAccess);
   }

   public static <T> RegistryReadOps<T> create(DynamicOps<T> pDelegate, RegistryReadOps.ResourceAccess pResources, RegistryAccess pRegistryAccess) {
      return new RegistryReadOps<>(pDelegate, pResources, pRegistryAccess, Maps.newIdentityHashMap());
   }

   private RegistryReadOps(DynamicOps<T> pDelegate, RegistryReadOps.ResourceAccess pResources, RegistryAccess pRegistryAccess, IdentityHashMap<ResourceKey<? extends Registry<?>>, RegistryReadOps.ReadCache<?>> pReadCache) {
      super(pDelegate);
      this.resources = pResources;
      this.registryAccess = pRegistryAccess;
      this.readCache = pReadCache;
      this.jsonOps = pDelegate == JsonOps.INSTANCE ? (RegistryReadOps<JsonElement>)this : new RegistryReadOps<>(JsonOps.INSTANCE, pResources, pRegistryAccess, pReadCache);
   }

   /**
    * Decodes a single element from the registry {@code registryKey}.
    * The {@code registryAccess} must own the registry, as it will be modified.
    * If inline definitions are allowed, the element may be encoded as raw data, which will be read using the {@code
    * elementCodec} and returned without mutating the internal {@code registryAccess}.
    * In all other cases, this will read the registry element's id as a {@link
    * net.minecraft.resources.ResourceLocation}. A supplier to the registry element will be returned, which accesses the
    * underlying registry, or an error, if the registry element was unable to be decoded here or in previous calls.
    * @param pInput The input encoded registry element, either a id or an inline definition.
    * @param pRegistryKey The registry the element might belong to.
    * @param pElementCodec The codec to decode individual elements from the registry, if inline definitions are
    * supported, or, to decode the element from the resource access.
    * @param pAllowInline If inline definitions that are not present in any registry are allowed here.
    */
   protected <E> DataResult<Pair<Supplier<E>, T>> decodeElement(T pInput, ResourceKey<? extends Registry<E>> pRegistryKey, Codec<E> pElementCodec, boolean pAllowInline) {
      Optional<WritableRegistry<E>> optional = this.registryAccess.ownedRegistry(pRegistryKey);
      if (!optional.isPresent()) {
         return DataResult.error("Unknown registry: " + pRegistryKey);
      } else {
         WritableRegistry<E> writableregistry = optional.get();
         DataResult<Pair<ResourceLocation, T>> dataresult = ResourceLocation.CODEC.decode(this.delegate, pInput);
         if (!dataresult.result().isPresent()) {
            return !pAllowInline ? DataResult.error("Inline definitions not allowed here") : pElementCodec.decode(this, pInput).map((p_135647_) -> {
               return p_135647_.mapFirst((p_179881_) -> {
                  return () -> {
                     return p_179881_;
                  };
               });
            });
         } else {
            Pair<ResourceLocation, T> pair = dataresult.result().get();
            ResourceLocation resourcelocation = pair.getFirst();
            return this.readAndRegisterElement(pRegistryKey, writableregistry, pElementCodec, resourcelocation).map((p_135650_) -> {
               return Pair.of(p_135650_, pair.getSecond());
            });
         }
      }
   }

   /**
    * Decodes all elements of a given registry as per the semantics of {@link #decodeElement(Object, ResourceKey, Codec,
    * boolean)}.
    * Lists resources internally from the resource access, and requires that they be be json files prefixed with the
    * registry name.
    * If so, individual elements are read and registered into the registry, accumulating errors in the returned result.
    * The partial result will contain all successfully decoded and registered elements.
    * @param pRegistry The (empty) registry to decode elements into.
    * @param pRegistryKey The key of the registry.
    * @param pElementCodec A codec used to decode individual registry elements from the resource access.
    */
   public <E> DataResult<MappedRegistry<E>> decodeElements(MappedRegistry<E> pRegistry, ResourceKey<? extends Registry<E>> pRegistryKey, Codec<E> pElementCodec) {
      Collection<ResourceLocation> collection = this.resources.listResources(pRegistryKey);
      DataResult<MappedRegistry<E>> dataresult = DataResult.success(pRegistry, Lifecycle.stable());
      String s = pRegistryKey.location().getPath() + "/";

      for(ResourceLocation resourcelocation : collection) {
         String s1 = resourcelocation.getPath();
         if (!s1.endsWith(".json")) {
            LOGGER.warn("Skipping resource {} since it is not a json file", (Object)resourcelocation);
         } else if (!s1.startsWith(s)) {
            LOGGER.warn("Skipping resource {} since it does not have a registry name prefix", (Object)resourcelocation);
         } else {
            String s2 = s1.substring(s.length(), s1.length() - ".json".length());
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s2);
            dataresult = dataresult.flatMap((p_135688_) -> {
               return this.readAndRegisterElement(pRegistryKey, p_135688_, pElementCodec, resourcelocation1).map((p_179876_) -> {
                  return p_135688_;
               });
            });
         }
      }

      return dataresult.setPartial(pRegistry);
   }

   private <E> DataResult<Supplier<E>> readAndRegisterElement(ResourceKey<? extends Registry<E>> pRegistryKey, final WritableRegistry<E> pRegistry, Codec<E> pElementCodec, ResourceLocation pId) {
      final ResourceKey<E> resourcekey = ResourceKey.create(pRegistryKey, pId);
      RegistryReadOps.ReadCache<E> readcache = this.readCache(pRegistryKey);
      DataResult<Supplier<E>> dataresult = readcache.values.get(resourcekey);
      if (dataresult != null) {
         return dataresult;
      } else {
         Supplier<E> supplier = Suppliers.memoize(() -> {
            E e = pRegistry.get(resourcekey);
            if (e == null) {
               throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + resourcekey);
            } else {
               return e;
            }
         });
         readcache.values.put(resourcekey, DataResult.success(supplier));
         Optional<DataResult<Pair<E, OptionalInt>>> optional = this.resources.parseElement(this.jsonOps, pRegistryKey, resourcekey, pElementCodec);
         DataResult<Supplier<E>> dataresult1;
         if (!optional.isPresent()) {
            dataresult1 = DataResult.success(new Supplier<E>() {
               public E get() {
                  return pRegistry.get(resourcekey);
               }

               public String toString() {
                  return resourcekey.toString();
               }
            }, Lifecycle.stable());
         } else {
            DataResult<Pair<E, OptionalInt>> dataresult2 = optional.get();
            Optional<Pair<E, OptionalInt>> optional1 = dataresult2.result();
            if (optional1.isPresent()) {
               Pair<E, OptionalInt> pair = optional1.get();
               pRegistry.registerOrOverride(pair.getSecond(), resourcekey, pair.getFirst(), dataresult2.lifecycle());
            }

            dataresult1 = dataresult2.map((p_135674_) -> {
               return () -> {
                  return pRegistry.get(resourcekey);
               };
            });
         }

         readcache.values.put(resourcekey, dataresult1);
         return dataresult1;
      }
   }

   private <E> RegistryReadOps.ReadCache<E> readCache(ResourceKey<? extends Registry<E>> pRegistryKey) {
      return (RegistryReadOps.ReadCache<E>)this.readCache.computeIfAbsent(pRegistryKey, (p_135707_) -> {
         return new RegistryReadOps.ReadCache<E>();
      });
   }

   protected <E> DataResult<Registry<E>> registry(ResourceKey<? extends Registry<E>> pRegistryKey) {
      return this.registryAccess.ownedRegistry(pRegistryKey).map((p_135667_) -> {
         return DataResult.<Registry<E>>success(p_135667_, p_135667_.elementsLifecycle());
      }).orElseGet(() -> {
         return DataResult.error("Unknown registry: " + pRegistryKey);
      });
   }

   /**
    * This is a cheap java version of a type alias. Because {@code ReadCache<E>} is shorter than {@code
    * Map<ResourceKey<E>, DataResult<Supplier<E>>}.
    */
   static final class ReadCache<E> {
      final Map<ResourceKey<E>, DataResult<Supplier<E>>> values = Maps.newIdentityHashMap();
   }

   public interface ResourceAccess {
      Collection<ResourceLocation> listResources(ResourceKey<? extends Registry<?>> pRegistryKey);

      <E> Optional<DataResult<Pair<E, OptionalInt>>> parseElement(DynamicOps<JsonElement> pOps, ResourceKey<? extends Registry<E>> pRegistryKey, ResourceKey<E> pResourceKey, Decoder<E> pDecoder);

      static RegistryReadOps.ResourceAccess forResourceManager(final ResourceManager pManager) {
         return new RegistryReadOps.ResourceAccess() {
            public Collection<ResourceLocation> listResources(ResourceKey<? extends Registry<?>> p_135734_) {
               return pManager.listResources(p_135734_.location().getPath(), (p_135732_) -> {
                  return p_135732_.endsWith(".json");
               });
            }

            public <E> Optional<DataResult<Pair<E, OptionalInt>>> parseElement(DynamicOps<JsonElement> p_179897_, ResourceKey<? extends Registry<E>> p_179898_, ResourceKey<E> p_179899_, Decoder<E> p_179900_) {
               ResourceLocation resourcelocation = p_179899_.location();
               ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), p_179898_.location().getPath() + "/" + resourcelocation.getPath() + ".json");
               if (!pManager.hasResource(resourcelocation1)) {
                  return Optional.empty();
               } else {
                  try {
                     Resource resource = pManager.getResource(resourcelocation1);

                     Optional optional;
                     try {
                        Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);

                        try {
                           JsonParser jsonparser = new JsonParser();
                           JsonElement jsonelement = jsonparser.parse(reader);
                           if (jsonelement != null) jsonelement.getAsJsonObject().addProperty("forge:registry_name", p_179899_.location().toString());
                           optional = Optional.of(p_179900_.parse(p_179897_, jsonelement).map((p_135730_) -> {
                              return Pair.of(p_135730_, OptionalInt.empty());
                           }));
                        } catch (Throwable throwable2) {
                           try {
                              reader.close();
                           } catch (Throwable throwable1) {
                              throwable2.addSuppressed(throwable1);
                           }

                           throw throwable2;
                        }

                        reader.close();
                     } catch (Throwable throwable3) {
                        if (resource != null) {
                           try {
                              resource.close();
                           } catch (Throwable throwable) {
                              throwable3.addSuppressed(throwable);
                           }
                        }

                        throw throwable3;
                     }

                     if (resource != null) {
                        resource.close();
                     }

                     return optional;
                  } catch (JsonIOException | JsonSyntaxException | IOException ioexception) {
                     return Optional.of(DataResult.error("Failed to parse " + resourcelocation1 + " file: " + ioexception.getMessage()));
                  }
               }
            }

            public String toString() {
               return "ResourceAccess[" + pManager + "]";
            }
         };
      }

      /**
       * An in-memory, JSON serialized form of a {@link net.minecraft.core.RegistryAccess}.
       * It retains the integer IDs and lifecycles of every registry element, and is used to bridge the gap between
       * {@link RegistryReadOps} and {@link net.minecraft.resources.RegistryWriteOps} when using the pair in conjunction
       * to perform a deep copy of the entire registries.
       * This implements {@link ResourceAccess} as it is used as the access for a {@link RegistryReadOps} to read from,
       * when the builtin registries are being initialized.
       */
      public static final class MemoryMap implements RegistryReadOps.ResourceAccess {
         private final Map<ResourceKey<?>, JsonElement> data = Maps.newIdentityHashMap();
         private final Object2IntMap<ResourceKey<?>> ids = new Object2IntOpenCustomHashMap<>(Util.identityStrategy());
         private final Map<ResourceKey<?>, Lifecycle> lifecycles = Maps.newIdentityHashMap();

         public <E> void add(RegistryAccess.RegistryHolder pRegistryAccess, ResourceKey<E> pResourceKey, Encoder<E> pEncoder, int pId, E pElement, Lifecycle pLifecycle) {
            DataResult<JsonElement> dataresult = pEncoder.encodeStart(RegistryWriteOps.create(JsonOps.INSTANCE, pRegistryAccess), pElement);
            Optional<PartialResult<JsonElement>> optional = dataresult.error();
            if (optional.isPresent()) {
               RegistryReadOps.LOGGER.error("Error adding element: {}", (Object)optional.get().message());
            } else {
               this.data.put(pResourceKey, dataresult.result().get());
               this.ids.put(pResourceKey, pId);
               this.lifecycles.put(pResourceKey, pLifecycle);
            }
         }

         public Collection<ResourceLocation> listResources(ResourceKey<? extends Registry<?>> pRegistryKey) {
            return this.data.keySet().stream().filter((p_135762_) -> {
               return p_135762_.isFor(pRegistryKey);
            }).map((p_135759_) -> {
               return new ResourceLocation(p_135759_.location().getNamespace(), pRegistryKey.location().getPath() + "/" + p_135759_.location().getPath() + ".json");
            }).collect(Collectors.toList());
         }

         public <E> Optional<DataResult<Pair<E, OptionalInt>>> parseElement(DynamicOps<JsonElement> pOps, ResourceKey<? extends Registry<E>> pRegistryKey, ResourceKey<E> pResourceKey, Decoder<E> pDecoder) {
            JsonElement jsonelement = this.data.get(pResourceKey);
            if (jsonelement != null) jsonelement.getAsJsonObject().addProperty("forge:registry_name", pResourceKey.location().toString());
            return jsonelement == null ? Optional.of(DataResult.error("Unknown element: " + pResourceKey)) : Optional.of(pDecoder.parse(pOps, jsonelement).setLifecycle(this.lifecycles.get(pResourceKey)).map((p_135756_) -> {
               return Pair.of(p_135756_, OptionalInt.of(this.ids.getInt(pResourceKey)));
            }));
         }
      }
   }
}
