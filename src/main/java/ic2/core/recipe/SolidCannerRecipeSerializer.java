package ic2.core.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class SolidCannerRecipeSerializer implements RecipeSerializer<SolidCannerRecipe> {
    @Override
    public MapCodec<SolidCannerRecipe> codec() {
        return SolidCannerRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SolidCannerRecipe> streamCodec() {
        return SolidCannerRecipe.STREAM_CODEC;
    }
}
