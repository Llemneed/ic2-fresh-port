package ic2.core.init;

import ic2.core.IC2;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class IC2Worldgen {
    public static final ResourceKey<ConfiguredFeature<?, ?>> COPPER_ORE = configured("copper_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEAD_ORE = configured("lead_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TIN_ORE = configured("tin_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> URANIUM_ORE = configured("uranium_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RUBBER_TREE = configured("rubber_tree");

    public static final ResourceKey<PlacedFeature> COPPER_ORE_PLACED = placed("copper_ore");
    public static final ResourceKey<PlacedFeature> LEAD_ORE_PLACED = placed("lead_ore");
    public static final ResourceKey<PlacedFeature> TIN_ORE_PLACED = placed("tin_ore");
    public static final ResourceKey<PlacedFeature> URANIUM_ORE_PLACED = placed("uranium_ore");
    public static final ResourceKey<PlacedFeature> RUBBER_TREE_PLACED = placed("rubber_tree");

    private IC2Worldgen() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(IC2.MODID, path);
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configured(String path) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, id(path));
    }

    private static ResourceKey<PlacedFeature> placed(String path) {
        return ResourceKey.create(Registries.PLACED_FEATURE, id(path));
    }
}
