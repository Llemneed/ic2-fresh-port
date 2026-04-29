package ic2.core.item.food;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public final class FilledTinCanItem extends Item {
    private static final FoodProperties FOOD = new FoodProperties.Builder()
            .nutrition(6)
            .saturationModifier(0.8F)
            .build();

    public FilledTinCanItem(Item.Properties properties) {
        super(properties.food(FOOD).stacksTo(16));
    }
}
