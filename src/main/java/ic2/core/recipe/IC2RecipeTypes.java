package ic2.core.recipe;

import ic2.core.IC2;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IC2RecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, IC2.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<MaceratorRecipe>> MACERATING = RECIPE_TYPES.register(
            "macerating",
            () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return IC2.MODID + ":macerating";
                }
            }
    );

    public static final DeferredHolder<RecipeType<?>, RecipeType<ExtractorRecipe>> EXTRACTING = RECIPE_TYPES.register(
            "extracting",
            () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return IC2.MODID + ":extracting";
                }
            }
    );

    public static final DeferredHolder<RecipeType<?>, RecipeType<CompressorRecipe>> COMPRESSING = RECIPE_TYPES.register(
            "compressing",
            () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return IC2.MODID + ":compressing";
                }
            }
    );

    public static final DeferredHolder<RecipeType<?>, RecipeType<MetalFormerRecipe>> METAL_FORMING = RECIPE_TYPES.register(
            "metal_forming",
            () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return IC2.MODID + ":metal_forming";
                }
            }
    );

    public static final DeferredHolder<RecipeType<?>, RecipeType<SolidCannerRecipe>> SOLID_CANNING = RECIPE_TYPES.register(
            "solid_canning",
            () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return IC2.MODID + ":solid_canning";
                }
            }
    );

    private IC2RecipeTypes() {
    }
}
