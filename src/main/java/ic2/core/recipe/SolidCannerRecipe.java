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
import net.minecraft.world.level.Level;

public record SolidCannerRecipe(
        Ingredient input,
        Ingredient canInput,
        MaceratorRecipe.ResultDefinition result,
        int inputCount,
        int canCount,
        int energy
) implements Recipe<DualStackRecipeInput> {
    public static final MapCodec<SolidCannerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(SolidCannerRecipe::input),
            Ingredient.CODEC_NONEMPTY.fieldOf("canInput").forGetter(SolidCannerRecipe::canInput),
            MaceratorRecipe.ResultDefinition.CODEC.fieldOf("result").forGetter(SolidCannerRecipe::result),
            Codec.INT.optionalFieldOf("inputCount", 1).forGetter(SolidCannerRecipe::inputCount),
            Codec.INT.optionalFieldOf("canCount", 1).forGetter(SolidCannerRecipe::canCount),
            Codec.INT.optionalFieldOf("energy", 0).forGetter(SolidCannerRecipe::energy)
    ).apply(instance, SolidCannerRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SolidCannerRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            SolidCannerRecipe::input,
            Ingredient.CONTENTS_STREAM_CODEC,
            SolidCannerRecipe::canInput,
            MaceratorRecipe.ResultDefinition.STREAM_CODEC,
            SolidCannerRecipe::result,
            ByteBufCodecs.VAR_INT,
            SolidCannerRecipe::inputCount,
            ByteBufCodecs.VAR_INT,
            SolidCannerRecipe::canCount,
            ByteBufCodecs.VAR_INT,
            SolidCannerRecipe::energy,
            SolidCannerRecipe::new
    );

    @Override
    public boolean matches(DualStackRecipeInput input, Level level) {
        return this.input.test(input.first())
                && this.canInput.test(input.second())
                && input.first().getCount() >= inputCount
                && input.second().getCount() >= canCount;
    }

    @Override
    public ItemStack assemble(DualStackRecipeInput input, HolderLookup.Provider registries) {
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
        return IC2RecipeSerializers.SOLID_CANNING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return IC2RecipeTypes.SOLID_CANNING.get();
    }
}
