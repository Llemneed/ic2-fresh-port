package ic2.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
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

public record MetalFormerRecipe(
        Mode mode,
        Ingredient ingredient,
        MaceratorRecipe.ResultDefinition result,
        int ingredientCount,
        int energy
) implements Recipe<SingleRecipeInput> {
    public static final MapCodec<MetalFormerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Mode.CODEC.fieldOf("mode").forGetter(MetalFormerRecipe::mode),
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(MetalFormerRecipe::ingredient),
            MaceratorRecipe.ResultDefinition.CODEC.fieldOf("result").forGetter(MetalFormerRecipe::result),
            Codec.INT.optionalFieldOf("ingredientCount", 1).forGetter(MetalFormerRecipe::ingredientCount),
            Codec.INT.optionalFieldOf("energy", 0).forGetter(MetalFormerRecipe::energy)
    ).apply(instance, MetalFormerRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MetalFormerRecipe> STREAM_CODEC = StreamCodec.composite(
            Mode.STREAM_CODEC,
            MetalFormerRecipe::mode,
            Ingredient.CONTENTS_STREAM_CODEC,
            MetalFormerRecipe::ingredient,
            MaceratorRecipe.ResultDefinition.STREAM_CODEC,
            MetalFormerRecipe::result,
            ByteBufCodecs.VAR_INT,
            MetalFormerRecipe::ingredientCount,
            ByteBufCodecs.VAR_INT,
            MetalFormerRecipe::energy,
            MetalFormerRecipe::new
    );

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item()) && input.item().getCount() >= ingredientCount;
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
        return IC2RecipeSerializers.METAL_FORMING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return IC2RecipeTypes.METAL_FORMING.get();
    }

    public enum Mode {
        EXTRUDING,
        ROLLING,
        CUTTING;

        public static final Codec<Mode> CODEC = Codec.STRING.xmap(Mode::fromSerializedName, Mode::getSerializedName);
        public static final StreamCodec<RegistryFriendlyByteBuf, Mode> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public Mode decode(RegistryFriendlyByteBuf buffer) {
                return Mode.fromSerializedName(ByteBufCodecs.STRING_UTF8.decode(buffer));
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, Mode value) {
                ByteBufCodecs.STRING_UTF8.encode(buffer, value.getSerializedName());
            }
        };

        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        private static Mode fromSerializedName(String name) {
            return Mode.valueOf(name.toUpperCase(Locale.ROOT));
        }
    }
}
