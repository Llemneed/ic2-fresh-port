package ic2.core.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class ElectricSmeltingRecipeSerializer implements RecipeSerializer<ElectricSmeltingRecipe> {
    @Override
    public MapCodec<ElectricSmeltingRecipe> codec() {
        return ElectricSmeltingRecipe.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ElectricSmeltingRecipe> streamCodec() {
        return ElectricSmeltingRecipe.STREAM_CODEC;
    }
}
