package by.ts.hmxy.world.level.levelgen.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import by.ts.hmxy.HmxyMod;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class Structures {

	public static final DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister
			.create(ForgeRegistries.STRUCTURE_FEATURES, HmxyMod.MOD_ID);

	public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> PARK = STRUCTURES.register("park",
			() -> (new ParkStructure(NoneFeatureConfiguration.CODEC)));

	public static void setupStructures() {
		setupMapSpacingAndLand(PARK.get(), new StructureFeatureConfiguration(10, 5, 1234567890), true);
	}

	public static <F extends StructureFeature<?>> void setupMapSpacingAndLand(F structure,
			StructureFeatureConfiguration StructureFeatureConfiguration, boolean transformSurroundingLand) {
		StructureFeature.STRUCTURES_REGISTRY.put(structure.getRegistryName().toString(), structure);

		if (transformSurroundingLand) {
			StructureFeature.NOISE_AFFECTING_FEATURES = ImmutableList.<StructureFeature<?>>builder()
					.addAll(StructureFeature.NOISE_AFFECTING_FEATURES).add(structure).build();
		}

		StructureSettings.DEFAULTS = ImmutableMap.<StructureFeature<?>, StructureFeatureConfiguration>builder()
				.putAll(StructureSettings.DEFAULTS).put(structure, StructureFeatureConfiguration).build();
		BuiltinRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(settings -> {
			Map<StructureFeature<?>, StructureFeatureConfiguration> structureMap = settings.getValue()
					.structureSettings().structureConfig();
			if (structureMap instanceof ImmutableMap) {
				Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(structureMap);
				tempMap.put(structure, StructureFeatureConfiguration);
				settings.getValue().structureSettings().structureConfig = tempMap;
			} else {
				structureMap.put(structure, StructureFeatureConfiguration);
			}
		});
	}
}
