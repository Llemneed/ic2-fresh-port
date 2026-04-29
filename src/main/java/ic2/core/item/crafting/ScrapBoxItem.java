package ic2.core.item.crafting;

import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public final class ScrapBoxItem extends Item {
    private static final List<ItemStack> REWARDS = List.of(
            new ItemStack(Items.DIRT, 4),
            new ItemStack(Items.COBBLESTONE, 8),
            new ItemStack(Items.SAND, 4),
            new ItemStack(Items.STICK, 6),
            new ItemStack(Items.BREAD),
            new ItemStack(Items.APPLE),
            new ItemStack(Items.IRON_INGOT),
            new ItemStack(Items.REDSTONE, 4),
            new ItemStack(Items.BONE_MEAL, 4),
            new ItemStack(Items.GUNPOWDER, 2),
            new ItemStack(Items.GLOWSTONE_DUST, 2),
            new ItemStack(Items.DIAMOND)
    );

    public ScrapBoxItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack held = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            RandomSource random = level.random;
            ItemStack reward = REWARDS.get(random.nextInt(REWARDS.size())).copy();
            if (player instanceof ServerPlayer serverPlayer) {
                if (!serverPlayer.getInventory().add(reward)) {
                    player.drop(reward, false);
                }
            }
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(held, level.isClientSide);
    }
}
