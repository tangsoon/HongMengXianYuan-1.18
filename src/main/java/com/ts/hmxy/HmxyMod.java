package com.ts.hmxy;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mojang.serialization.Codec;
import com.ts.hmxy.util.JingJieHelper;
import com.ts.hmxy.world.item.HmxyItems;
import com.ts.hmxy.world.item.level.block.HmxyBlocks;
import com.ts.hmxy.world.level.levelgen.feature.ConfiguredStructures;
import com.ts.hmxy.world.level.levelgen.structure.Structures;

@Mod("hmxy")
@EventBusSubscriber
public class HmxyMod {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "hmxy";

	public HmxyMod() {
		MinecraftForge.EVENT_BUS.register(this);

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		Structures.DEFERRED_REGISTRY_STRUCTURE.register(modEventBus);
		HmxyItems.ITEMS.register(modEventBus);
		HmxyBlocks.BLOCKS.register(modEventBus);
		
		modEventBus.addListener(this::setup);

		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.addListener(EventPriority.NORMAL, this::addDimensionalSpacing);

		forgeBus.addListener(EventPriority.HIGH, this::biomeModification);
	}

	private void setup(final FMLCommonSetupEvent event) {
		JingJieHelper.initJingJies();
		event.enqueueWork(() -> {
			Structures.setupStructures();
			ConfiguredStructures.registerConfiguredStructures();
		});
	}
	
	 public void biomeModification(final BiomeLoadingEvent event) {
	        /*
	         * Add our structure to all biomes including other modded biomes.
	         * You can skip or add only to certain biomes based on stuff like biome category,
	         * temperature, scale, precipitation, mod id, etc. All kinds of options!
	         *
	         * You can even use the BiomeDictionary as well! To use BiomeDictionary, do
	         * RegistryKey.getOrCreateKey(Registry.BIOME_KEY, event.getName()) to get the biome's
	         * registrykey. Then that can be fed into the dictionary to get the biome's types.
	         */
	        event.getGeneration().getStructures().add(() -> ConfiguredStructures.CONFIGURED_RUN_DOWN_HOUSE);
	    }
	 
	 private static Method GETCODEC_METHOD;
	    @SuppressWarnings("resource")
		public void addDimensionalSpacing(final WorldEvent.Load event) {
	        if(event.getWorld() instanceof ServerLevel){
	            ServerLevel serverWorld = (ServerLevel)event.getWorld();

	            /*
	             * Skip Terraforged's chunk generator as they are a special case of a mod locking down their chunkgenerator.
	             * They will handle your structure spacing for your if you add to BuiltinRegistries.NOISE_GENERATOR_SETTINGS in your structure's registration.
	             * This here is done with reflection as this tutorial is not about setting up and using Mixins.
	             * If you are using mixins, you can call the codec method with an invoker mixin instead of using reflection.
	             */
	            try {
	                if(GETCODEC_METHOD == null) GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "func_230347_a_");
	                @SuppressWarnings({ "unchecked", "resource" })
					ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(serverWorld.getChunkSource().generator));
	                if(cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
	            }
	            catch(Exception e){
	                LOGGER.error("Was unable to check if " + serverWorld.dimension().location() + " is using Terraforged's ChunkGenerator.");
	            }

	            /*
	             * Prevent spawning our structure in Vanilla's superflat world as
	             * people seem to want their superflat worlds free of modded structures.
	             * Also that vanilla superflat is really tricky and buggy to work with in my experience.
	             */
	            if(serverWorld.getChunkSource().getGenerator() instanceof FlatLevelSource &&
	                serverWorld.dimension().equals(Level.OVERWORLD)){
	                return;
	            }

	            /*
	             * putIfAbsent so people can override the spacing with dimension datapacks themselves if they wish to customize spacing more precisely per dimension.
	             * Requires AccessTransformer  (see resources/META-INF/accesstransformer.cfg)
	             *
	             * NOTE: if you add per-dimension spacing configs, you can't use putIfAbsent as BuiltinRegistries.NOISE_GENERATOR_SETTINGS in FMLCommonSetupEvent
	             * already added your default structure spacing to some dimensions. You would need to override the spacing with .put(...)
	             * And if you want to do dimension blacklisting, you need to remove the spacing entry entirely from the map below to prevent generation safely.
	             */
	            Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(serverWorld.getChunkSource().generator.getSettings().structureConfig());
	            tempMap.putIfAbsent(Structures.RUN_DOWN_HOUSE.get(), StructureSettings.DEFAULTS.get(Structures.RUN_DOWN_HOUSE.get()));
	            serverWorld.getChunkSource().generator.getSettings().structureConfig = tempMap;
	        }
	   }
}
