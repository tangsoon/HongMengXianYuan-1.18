package by.ts.hmxy.feature;

import java.util.List;
import by.ts.hmxy.block.HmxyBlocks;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class LingMaiFeature {
	public static Holder<ConfiguredFeature<OreConfiguration, ?>> CONFIG;
    public static Holder<PlacedFeature> PLACE;
    
    public static void registerFeatures() {
    	CONFIG = FeatureUtils.register(
                HmxyBlocks.LING_MAI.get().getRegistryName().toString(),
                Feature.ORE,
                new OreConfiguration(
                        OreFeatures.STONE_ORE_REPLACEABLES,
                        HmxyBlocks.LING_MAI.get().defaultBlockState(), 10));
    	PLACE = PlacementUtils.register(
    			HmxyBlocks.LING_MAI.get().getRegistryName().toString(),
                CONFIG,
                List.of(CountPlacement.of(10),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(
                                VerticalAnchor.bottom(),
                                VerticalAnchor.top())));
    }
}
