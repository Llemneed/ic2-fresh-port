package ic2.core.recipe;

import ic2.core.IC2;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IC2RecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, IC2.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MaceratorRecipe>> MACERATING =
            RECIPE_SERIALIZERS.register("macerating", MaceratorRecipeSerializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ExtractorRecipe>> EXTRACTING =
            RECIPE_SERIALIZERS.register("extracting", ExtractorRecipeSerializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CompressorRecipe>> COMPRESSING =
            RECIPE_SERIALIZERS.register("compressing", CompressorRecipeSerializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MetalFormerRecipe>> METAL_FORMING =
            RECIPE_SERIALIZERS.register("metal_forming", MetalFormerRecipeSerializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SolidCannerRecipe>> SOLID_CANNING =
            RECIPE_SERIALIZERS.register("solid_canning", SolidCannerRecipeSerializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RecyclerRecipe>> RECYCLING =
            RECIPE_SERIALIZERS.register("recycling", RecyclerRecipeSerializer::new);

    private IC2RecipeSerializers() {
    }
}
