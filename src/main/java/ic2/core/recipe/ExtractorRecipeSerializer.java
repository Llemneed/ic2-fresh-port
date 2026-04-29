package ic2.core.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class ExtractorRecipeSerializer implements RecipeSerializer<ExtractorRecipe> {
    @Override
    public MapCodec<ExtractorRecipe> codec() {
        return ExtractorRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ExtractorRecipe> streamCodec() {
        return ExtractorRecipe.STREAM_CODEC;
    }
}
