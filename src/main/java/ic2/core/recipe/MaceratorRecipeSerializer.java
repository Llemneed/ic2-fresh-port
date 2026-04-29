package ic2.core.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class MaceratorRecipeSerializer implements RecipeSerializer<MaceratorRecipe> {
    @Override
    public MapCodec<MaceratorRecipe> codec() {
        return MaceratorRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, MaceratorRecipe> streamCodec() {
        return MaceratorRecipe.STREAM_CODEC;
    }
}
