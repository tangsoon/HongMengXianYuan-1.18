package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class MultiNoiseBiomeSource extends BiomeSource {
   private static final MultiNoiseBiomeSource.NoiseParameters DEFAULT_NOISE_PARAMETERS = new MultiNoiseBiomeSource.NoiseParameters(-7, ImmutableList.of(1.0D, 1.0D));
   public static final MapCodec<MultiNoiseBiomeSource> DIRECT_CODEC = RecordCodecBuilder.mapCodec((p_48477_) -> {
      return p_48477_.group(Codec.LONG.fieldOf("seed").forGetter((p_151852_) -> {
         return p_151852_.seed;
      }), RecordCodecBuilder.<Pair<Biome.ClimateParameters, Supplier<Biome>>>create((p_151838_) -> {
         return p_151838_.group(Biome.ClimateParameters.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(p_151838_, Pair::of);
      }).listOf().fieldOf("biomes").forGetter((p_151850_) -> {
         return p_151850_.parameters;
      }), MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("temperature_noise").forGetter((p_151848_) -> {
         return p_151848_.temperatureParams;
      }), MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("humidity_noise").forGetter((p_151846_) -> {
         return p_151846_.humidityParams;
      }), MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("altitude_noise").forGetter((p_151844_) -> {
         return p_151844_.altitudeParams;
      }), MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("weirdness_noise").forGetter((p_151842_) -> {
         return p_151842_.weirdnessParams;
      })).apply(p_48477_, MultiNoiseBiomeSource::new);
   });
   public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(MultiNoiseBiomeSource.PresetInstance.CODEC, DIRECT_CODEC).xmap((p_48473_) -> {
      return p_48473_.map(MultiNoiseBiomeSource.PresetInstance::biomeSource, Function.identity());
   }, (p_48471_) -> {
      return p_48471_.preset().map(Either::<MultiNoiseBiomeSource.PresetInstance, MultiNoiseBiomeSource>left).orElseGet(() -> {
         return Either.right(p_48471_);
      });
   }).codec();
   private final MultiNoiseBiomeSource.NoiseParameters temperatureParams;
   private final MultiNoiseBiomeSource.NoiseParameters humidityParams;
   private final MultiNoiseBiomeSource.NoiseParameters altitudeParams;
   private final MultiNoiseBiomeSource.NoiseParameters weirdnessParams;
   private final NormalNoise temperatureNoise;
   private final NormalNoise humidityNoise;
   private final NormalNoise altitudeNoise;
   private final NormalNoise weirdnessNoise;
   private final List<Pair<Biome.ClimateParameters, Supplier<Biome>>> parameters;
   private final boolean useY;
   private final long seed;
   private final Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> preset;

   public MultiNoiseBiomeSource(long pSeed, List<Pair<Biome.ClimateParameters, Supplier<Biome>>> pParameters) {
      this(pSeed, pParameters, Optional.empty());
   }

   MultiNoiseBiomeSource(long pSeed, List<Pair<Biome.ClimateParameters, Supplier<Biome>>> pParameters, Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> pPreset) {
      this(pSeed, pParameters, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, pPreset);
   }

   private MultiNoiseBiomeSource(long p_48441_, List<Pair<Biome.ClimateParameters, Supplier<Biome>>> p_48442_, MultiNoiseBiomeSource.NoiseParameters p_48443_, MultiNoiseBiomeSource.NoiseParameters p_48444_, MultiNoiseBiomeSource.NoiseParameters p_48445_, MultiNoiseBiomeSource.NoiseParameters p_48446_) {
      this(p_48441_, p_48442_, p_48443_, p_48444_, p_48445_, p_48446_, Optional.empty());
   }

   private MultiNoiseBiomeSource(long pSeed, List<Pair<Biome.ClimateParameters, Supplier<Biome>>> pParameters, MultiNoiseBiomeSource.NoiseParameters pTemperatureParams, MultiNoiseBiomeSource.NoiseParameters pHumidityParams, MultiNoiseBiomeSource.NoiseParameters pAltitudeParams, MultiNoiseBiomeSource.NoiseParameters pWeirdnessParams, Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> pPreset) {
      super(pParameters.stream().map(Pair::getSecond));
      this.seed = pSeed;
      this.preset = pPreset;
      this.temperatureParams = pTemperatureParams;
      this.humidityParams = pHumidityParams;
      this.altitudeParams = pAltitudeParams;
      this.weirdnessParams = pWeirdnessParams;
      this.temperatureNoise = NormalNoise.create(new WorldgenRandom(pSeed), pTemperatureParams.firstOctave(), pTemperatureParams.amplitudes());
      this.humidityNoise = NormalNoise.create(new WorldgenRandom(pSeed + 1L), pHumidityParams.firstOctave(), pHumidityParams.amplitudes());
      this.altitudeNoise = NormalNoise.create(new WorldgenRandom(pSeed + 2L), pAltitudeParams.firstOctave(), pAltitudeParams.amplitudes());
      this.weirdnessNoise = NormalNoise.create(new WorldgenRandom(pSeed + 3L), pWeirdnessParams.firstOctave(), pWeirdnessParams.amplitudes());
      this.parameters = pParameters;
      this.useY = false;
   }

   public static MultiNoiseBiomeSource overworld(Registry<Biome> pBiomes, long pSeed) {
      ImmutableList<Pair<Biome.ClimateParameters, Supplier<Biome>>> immutablelist = parameters(pBiomes);
      MultiNoiseBiomeSource.NoiseParameters multinoisebiomesource$noiseparameters = new MultiNoiseBiomeSource.NoiseParameters(-9, 1.0D, 0.0D, 3.0D, 3.0D, 3.0D, 3.0D);
      MultiNoiseBiomeSource.NoiseParameters multinoisebiomesource$noiseparameters1 = new MultiNoiseBiomeSource.NoiseParameters(-7, 1.0D, 2.0D, 4.0D, 4.0D);
      MultiNoiseBiomeSource.NoiseParameters multinoisebiomesource$noiseparameters2 = new MultiNoiseBiomeSource.NoiseParameters(-9, 1.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.0D);
      MultiNoiseBiomeSource.NoiseParameters multinoisebiomesource$noiseparameters3 = new MultiNoiseBiomeSource.NoiseParameters(-8, 1.2D, 0.6D, 0.0D, 0.0D, 1.0D, 0.0D);
      return new MultiNoiseBiomeSource(pSeed, immutablelist, multinoisebiomesource$noiseparameters, multinoisebiomesource$noiseparameters1, multinoisebiomesource$noiseparameters2, multinoisebiomesource$noiseparameters3, Optional.empty());
   }

   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public BiomeSource withSeed(long pSeed) {
      return new MultiNoiseBiomeSource(pSeed, this.parameters, this.temperatureParams, this.humidityParams, this.altitudeParams, this.weirdnessParams, this.preset);
   }

   private Optional<MultiNoiseBiomeSource.PresetInstance> preset() {
      return this.preset.map((p_48475_) -> {
         return new MultiNoiseBiomeSource.PresetInstance(p_48475_.getSecond(), p_48475_.getFirst(), this.seed);
      });
   }

   /**
    * Gets the biome at the given quart positions.
    * Note that the coordinates passed into this method are 1/4 the scale of block coordinates. The noise biome is then
    * used by the {@link net.minecraft.world.level.biome.BiomeZoomer} to produce a biome for each unique position,
    * whilst only saving the biomes once per each 4x4x4 cube.
    */
   public Biome getNoiseBiome(int pX, int pY, int pZ) {
      int i = this.useY ? pY : 0;
      Biome.ClimateParameters biome$climateparameters = new Biome.ClimateParameters((float)this.temperatureNoise.getValue((double)pX, (double)i, (double)pZ), (float)this.humidityNoise.getValue((double)pX, (double)i, (double)pZ), (float)this.altitudeNoise.getValue((double)pX, (double)i, (double)pZ), (float)this.weirdnessNoise.getValue((double)pX, (double)i, (double)pZ), 0.0F);
      return this.parameters.stream().min(Comparator.comparing((p_48469_) -> {
         return p_48469_.getFirst().fitness(biome$climateparameters);
      })).map(Pair::getSecond).map(Supplier::get).orElse(net.minecraft.data.worldgen.biome.Biomes.THE_VOID);
   }

   public static ImmutableList<Pair<Biome.ClimateParameters, Supplier<Biome>>> parameters(Registry<Biome> p_151831_) {
      return ImmutableList.of(Pair.of(new Biome.ClimateParameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
         return p_151831_.getOrThrow(Biomes.PLAINS);
      }));
   }

   public boolean stable(long pSeed) {
      return this.seed == pSeed && this.preset.isPresent() && Objects.equals(this.preset.get().getSecond(), MultiNoiseBiomeSource.Preset.NETHER);
   }

   static class NoiseParameters {
      private final int firstOctave;
      private final DoubleList amplitudes;
      public static final Codec<MultiNoiseBiomeSource.NoiseParameters> CODEC = RecordCodecBuilder.create((p_48510_) -> {
         return p_48510_.group(Codec.INT.fieldOf("firstOctave").forGetter(MultiNoiseBiomeSource.NoiseParameters::firstOctave), Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(MultiNoiseBiomeSource.NoiseParameters::amplitudes)).apply(p_48510_, MultiNoiseBiomeSource.NoiseParameters::new);
      });

      public NoiseParameters(int p_48506_, List<Double> p_48507_) {
         this.firstOctave = p_48506_;
         this.amplitudes = new DoubleArrayList(p_48507_);
      }

      public NoiseParameters(int pFirstOctave, double... pAmplitudes) {
         this.firstOctave = pFirstOctave;
         this.amplitudes = new DoubleArrayList(pAmplitudes);
      }

      public int firstOctave() {
         return this.firstOctave;
      }

      public DoubleList amplitudes() {
         return this.amplitudes;
      }
   }

   public static class Preset {
      static final Map<ResourceLocation, MultiNoiseBiomeSource.Preset> BY_NAME = Maps.newHashMap();
      public static final MultiNoiseBiomeSource.Preset NETHER = new MultiNoiseBiomeSource.Preset(new ResourceLocation("nether"), (p_48524_, p_48525_, p_48526_) -> {
         return new MultiNoiseBiomeSource(p_48526_, ImmutableList.of(Pair.of(new Biome.ClimateParameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
            return p_48525_.getOrThrow(Biomes.NETHER_WASTES);
         }), Pair.of(new Biome.ClimateParameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F), () -> {
            return p_48525_.getOrThrow(Biomes.SOUL_SAND_VALLEY);
         }), Pair.of(new Biome.ClimateParameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F), () -> {
            return p_48525_.getOrThrow(Biomes.CRIMSON_FOREST);
         }), Pair.of(new Biome.ClimateParameters(0.0F, 0.5F, 0.0F, 0.0F, 0.375F), () -> {
            return p_48525_.getOrThrow(Biomes.WARPED_FOREST);
         }), Pair.of(new Biome.ClimateParameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.175F), () -> {
            return p_48525_.getOrThrow(Biomes.BASALT_DELTAS);
         })), Optional.of(Pair.of(p_48525_, p_48524_)));
      });
      final ResourceLocation name;
      private final Function3<MultiNoiseBiomeSource.Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> biomeSource;

      public Preset(ResourceLocation pName, Function3<MultiNoiseBiomeSource.Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> pBiomeSource) {
         this.name = pName;
         this.biomeSource = pBiomeSource;
         BY_NAME.put(pName, this);
      }

      public MultiNoiseBiomeSource biomeSource(Registry<Biome> pLookupRegistry, long pSeed) {
         return this.biomeSource.apply(this, pLookupRegistry, pSeed);
      }
   }

   static final class PresetInstance {
      public static final MapCodec<MultiNoiseBiomeSource.PresetInstance> CODEC = RecordCodecBuilder.mapCodec((p_48558_) -> {
         return p_48558_.group(ResourceLocation.CODEC.flatXmap((p_151869_) -> {
            return Optional.ofNullable(MultiNoiseBiomeSource.Preset.BY_NAME.get(p_151869_)).map(DataResult::success).orElseGet(() -> {
               return DataResult.error("Unknown preset: " + p_151869_);
            });
         }, (p_151867_) -> {
            return DataResult.success(p_151867_.name);
         }).fieldOf("preset").stable().forGetter(MultiNoiseBiomeSource.PresetInstance::preset), RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(MultiNoiseBiomeSource.PresetInstance::biomes), Codec.LONG.fieldOf("seed").stable().forGetter(MultiNoiseBiomeSource.PresetInstance::seed)).apply(p_48558_, p_48558_.stable(MultiNoiseBiomeSource.PresetInstance::new));
      });
      private final MultiNoiseBiomeSource.Preset preset;
      private final Registry<Biome> biomes;
      private final long seed;

      PresetInstance(MultiNoiseBiomeSource.Preset p_48546_, Registry<Biome> p_48547_, long p_48548_) {
         this.preset = p_48546_;
         this.biomes = p_48547_;
         this.seed = p_48548_;
      }

      public MultiNoiseBiomeSource.Preset preset() {
         return this.preset;
      }

      public Registry<Biome> biomes() {
         return this.biomes;
      }

      public long seed() {
         return this.seed;
      }

      public MultiNoiseBiomeSource biomeSource() {
         return this.preset.biomeSource(this.biomes, this.seed);
      }
   }
}