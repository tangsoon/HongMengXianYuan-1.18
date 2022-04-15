package com.ts.hmxy.world.level.levelgen.feature;

import com.ts.hmxy.HmxyMod;
import com.ts.hmxy.world.level.levelgen.structure.Structures;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ConfiguredStructures {
    public static ConfiguredStructureFeature<?, ?> CONFIGURED_RUN_DOWN_HOUSE = Structures.RUN_DOWN_HOUSE.get().configured(NoneFeatureConfiguration.INSTANCE);

    public static void registerConfiguredStructures() {
        Registry<ConfiguredStructureFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE;
        Registry.register(registry, new ResourceLocation(HmxyMod.MOD_ID, "configured_run_down_house"), CONFIGURED_RUN_DOWN_HOUSE);
    }
}