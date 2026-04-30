package ic2.core.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class MetalFormerRecipeSerializer implements RecipeSerializer<MetalFormerRecipe> {
    @Override
    public MapCodec<MetalFormerRecipe> codec() {
        return MetalFormerRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, MetalFormerRecipe> streamCodec() {
        return MetalFormerRecipe.STREAM_CODEC;
    }
}
