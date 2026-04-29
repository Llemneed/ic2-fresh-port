package ic2.core.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class CompressorRecipeSerializer implements RecipeSerializer<CompressorRecipe> {
    @Override
    public MapCodec<CompressorRecipe> codec() {
        return CompressorRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CompressorRecipe> streamCodec() {
        return CompressorRecipe.STREAM_CODEC;
    }
}
