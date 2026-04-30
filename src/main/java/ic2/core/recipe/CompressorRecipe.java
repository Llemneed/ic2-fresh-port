package ic2.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public record CompressorRecipe(
        Ingredient ingredient,
        MaceratorRecipe.ResultDefinition result,
        int ingredientCount,
        int energy
) implements Recipe<SingleRecipeInput> {
    public static final MapCodec<CompressorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CompressorRecipe::ingredient),
            MaceratorRecipe.ResultDefinition.CODEC.fieldOf("result").forGetter(CompressorRecipe::result),
            Codec.INT.optionalFieldOf("ingredientCount", 1).forGetter(CompressorRecipe::ingredientCount),
            Codec.INT.optionalFieldOf("energy", 0).forGetter(CompressorRecipe::energy)
    ).apply(instance, CompressorRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CompressorRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            CompressorRecipe::ingredient,
            MaceratorRecipe.ResultDefinition.STREAM_CODEC,
            CompressorRecipe::result,
            ByteBufCodecs.VAR_INT,
            CompressorRecipe::ingredientCount,
            ByteBufCodecs.VAR_INT,
            CompressorRecipe::energy,
            CompressorRecipe::new
    );

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item()) && input.item().getCount() >= ingredientCount;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        Item item = BuiltInRegistries.ITEM.get(result.id());
        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, result.count());
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
        return IC2RecipeSerializers.COMPRESSING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return IC2RecipeTypes.COMPRESSING.get();
    }
}
