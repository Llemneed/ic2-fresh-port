package ic2.core.item.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class WindMeterItem extends Item {
    public WindMeterItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide()) {
            double heightFactor = Mth.clamp((player.getY() - level.getMinBuildHeight()) / 256.0D, 0.0D, 1.0D);
            double weatherBonus = level.isThundering() ? 0.35D : level.isRaining() ? 0.2D : 0.0D;
            double base = 0.15D + heightFactor * 0.85D + weatherBonus;
            double wind = Math.round(base * 100.0D) / 100.0D;
            player.displayClientMessage(Component.literal("Wind strength: " + wind), true);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
