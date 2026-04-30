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

public record MaceratorRecipe(
        Ingredient ingredient,
        ResultDefinition result,
        float experience,
        int energy
) implements Recipe<SingleRecipeInput> {
    public static final MapCodec<MaceratorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(MaceratorRecipe::ingredient),
            ResultDefinition.CODEC.fieldOf("result").forGetter(MaceratorRecipe::result),
            Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(MaceratorRecipe::experience),
            Codec.INT.optionalFieldOf("energy", 0).forGetter(MaceratorRecipe::energy)
    ).apply(instance, MaceratorRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MaceratorRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            MaceratorRecipe::ingredient,
            ResultDefinition.STREAM_CODEC,
            MaceratorRecipe::result,
            ByteBufCodecs.FLOAT,
            MaceratorRecipe::experience,
            ByteBufCodecs.VAR_INT,
            MaceratorRecipe::energy,
            MaceratorRecipe::new
    );

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item());
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
        return IC2RecipeSerializers.MACERATING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return IC2RecipeTypes.MACERATING.get();
    }

    public record ResultDefinition(ResourceLocation id, int count) {
        public static final MapCodec<ResultDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(ResultDefinition::id),
                Codec.INT.optionalFieldOf("count", 1).forGetter(ResultDefinition::count)
        ).apply(instance, ResultDefinition::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ResultDefinition> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC,
                ResultDefinition::id,
                ByteBufCodecs.VAR_INT,
                ResultDefinition::count,
                ResultDefinition::new
        );
    }
}
