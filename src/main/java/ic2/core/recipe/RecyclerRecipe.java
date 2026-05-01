package ic2.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public record RecyclerRecipe(
        Ingredient ingredient,
        MaceratorRecipe.ResultDefinition result,
        float chance,
        int energy
) implements Recipe<SingleRecipeInput> {
    public static final MapCodec<RecyclerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(RecyclerRecipe::ingredient),
            MaceratorRecipe.ResultDefinition.CODEC.fieldOf("result").forGetter(RecyclerRecipe::result),
            Codec.FLOAT.optionalFieldOf("chance", 0.125F).forGetter(RecyclerRecipe::chance),
            Codec.INT.optionalFieldOf("energy", 0).forGetter(RecyclerRecipe::energy)
    ).apply(instance, RecyclerRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RecyclerRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            RecyclerRecipe::ingredient,
            MaceratorRecipe.ResultDefinition.STREAM_CODEC,
            RecyclerRecipe::result,
            ByteBufCodecs.FLOAT,
            RecyclerRecipe::chance,
            ByteBufCodecs.VAR_INT,
            RecyclerRecipe::energy,
            RecyclerRecipe::new
    );

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return getResultItem(registries);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        Item item = BuiltInRegistries.ITEM.get(result.id());
        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, result.count());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return IC2RecipeSerializers.RECYCLING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return IC2RecipeTypes.RECYCLING.get();
    }
}
