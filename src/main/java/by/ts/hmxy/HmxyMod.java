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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mojang.serialization.Codec;

import by.ts.hmxy.event.EntityRenderersHandler;
import by.ts.hmxy.util.Attrs;
import by.ts.hmxy.util.HmxyHelper;
import by.ts.hmxy.world.entity.HmxyEntities;
import by.ts.hmxy.world.item.HmxyItems;
import by.ts.hmxy.world.item.level.block.HmxyBlocks;
import by.ts.hmxy.world.item.level.material.HmxyFluids;
import by.ts.hmxy.world.level.levelgen.feature.ConfiguredStructures;
import by.ts.hmxy.world.level.levelgen.structure.Structures;
//TODO 圆林的亭子没有对称。
//TODO 硬山建筑不加载？
@Mod("hmxy")
@EventBusSubscriber
public class HmxyMod {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "hmxy";

	public HmxyMod() {
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.register(this);
		forgeBus.addListener(EventPriority.NORMAL, this::addDimensionalSpacing);
		forgeBus.addListener(EventPriority.HIGH, this::biomeModification);
		forgeBus.addListener(EventPriority.HIGH, EntityRenderersHandler::new);
	
		
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);	
		Structures.STRUCTURES.register(modEventBus);
		HmxyItems.ITEMS.register(modEventBus);
		HmxyBlocks.BLOCKS.register(modEventBus);
		HmxyFluids.FLUIDS.register(modEventBus);
		HmxyEntities.ENTITIES.register(modEventBus);
		HmxyEntities.ITEMS.register(modEventBus);
		Attrs.ATTRIBUTES.register(modEventBus);	

		HmxyHelper.initJingJies();
		
		new Thread(()->{
			InputStream inStream= ClassLoader.getSystemResourceAsStream("data/hmxy/console_banner.txt");
			if(inStream!=null) {
				InputStreamReader in=new InputStreamReader(inStream);
				char[] cs=new char[64];
				try {
					while((in.read(cs))!=-1) {
						System.out.print(cs);
					}
					System.out.println();
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
			else {
				HmxyMod.LOGGER.warn("未能正确加载横幅，信仰崩塌！！！");
			}
		}).run();
		
		Class<?> testModClass;
		String testModName="by.ts.hmxy.HmxyModTest";
		try {
			testModClass=Class.forName(testModName);
			testModClass.getConstructor().newInstance();
			LOGGER.info("启用测试类: "+testModName);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException  | InvocationTargetException | NoSuchMethodException  e) {
			LOGGER.info("未启用测试类: "+testModName);
		}
	}

	private void setup(final FMLCommonSetupEvent event) {
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
		if (event.getWorld() instanceof ServerLevel) {
			ServerLevel serverWorld = (ServerLevel) event.getWorld();
			try {
				if (GETCODEC_METHOD == null)
					GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "func_230347_a_");
				@SuppressWarnings({ "unchecked" })
				ResourceLocation cgRL = Registry.CHUNK_GENERATOR
						.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD
								.invoke(serverWorld.getChunkSource().generator));
				if (cgRL != null && cgRL.getNamespace().equals("terraforged"))
					return;
			} catch (Exception e) {
				LOGGER.error("Was unable to check if " + serverWorld.dimension().location()
						+ " is using Terraforged's ChunkGenerator.");
			}
			if (serverWorld.getChunkSource().getGenerator() instanceof FlatLevelSource
					&& serverWorld.dimension().equals(Level.OVERWORLD)) {
				return;
			}

			Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(
					serverWorld.getChunkSource().generator.getSettings().structureConfig());
			tempMap.putIfAbsent(Structures.PARK.get(), StructureSettings.DEFAULTS.get(Structures.PARK.get()));
			serverWorld.getChunkSource().generator.getSettings().structureConfig = tempMap;
		}
	}
	
	public static ResourceLocation modLoc(String path) {
		return new ResourceLocation(MOD_ID,path);
	}
}
