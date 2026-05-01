package ic2.core.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class RecyclerRecipeSerializer implements RecipeSerializer<RecyclerRecipe> {
    @Override
    public MapCodec<RecyclerRecipe> codec() {
        return RecyclerRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, RecyclerRecipe> streamCodec() {
        return RecyclerRecipe.STREAM_CODEC;
    }
}
