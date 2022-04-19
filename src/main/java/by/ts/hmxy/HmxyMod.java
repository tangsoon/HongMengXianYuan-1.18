package by.ts.hmxy;

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
import by.ts.hmxy.util.JingJieHelper;
import by.ts.hmxy.world.item.HmxyItems;
import by.ts.hmxy.world.item.level.block.HmxyBlocks;
import by.ts.hmxy.world.level.levelgen.feature.ConfiguredStructures;
import by.ts.hmxy.world.level.levelgen.structure.Structures;

@Mod("hmxy")
@EventBusSubscriber
public class HmxyMod {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "hmxy";

	
	public HmxyMod() {
		MinecraftForge.EVENT_BUS.register(this);

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		Structures.STRUCTURES.register(modEventBus);
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
	        event.getGeneration().getStructures().add(() -> ConfiguredStructures.CONFIGURED_PARK);
	    }
	 
	 private static Method GETCODEC_METHOD;
	    @SuppressWarnings("resource")
		public void addDimensionalSpacing(final WorldEvent.Load event) {
	        if(event.getWorld() instanceof ServerLevel){
	            ServerLevel serverWorld = (ServerLevel)event.getWorld();
	            try {
	                if(GETCODEC_METHOD == null) GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "func_230347_a_");
	                @SuppressWarnings({ "unchecked" })
					ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(serverWorld.getChunkSource().generator));
	                if(cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
	            }
	            catch(Exception e){
	                LOGGER.error("Was unable to check if " + serverWorld.dimension().location() + " is using Terraforged's ChunkGenerator.");
	            }
	            if(serverWorld.getChunkSource().getGenerator() instanceof FlatLevelSource &&
	                serverWorld.dimension().equals(Level.OVERWORLD)){
	                return;
	            }

	            Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(serverWorld.getChunkSource().generator.getSettings().structureConfig());
	            tempMap.putIfAbsent(Structures.PARK.get(), StructureSettings.DEFAULTS.get(Structures.PARK.get()));
	            serverWorld.getChunkSource().generator.getSettings().structureConfig = tempMap;
	        }
	   }
}
