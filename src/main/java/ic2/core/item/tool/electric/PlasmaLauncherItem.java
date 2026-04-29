package ic2.core.item.tool.electric;

import ic2.core.item.electric.BaseElectricItem;
import ic2.core.item.electric.ElectricItemManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class PlasmaLauncherItem extends BaseElectricItem {
    private final int energyPerShot;

    public PlasmaLauncherItem(int maxCharge, int transferLimit, int energyTier, int energyPerShot, Properties properties) {
        super(maxCharge, transferLimit, energyTier, properties);
        this.energyPerShot = energyPerShot;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }

        if (ElectricItemManager.getCharge(stack) < energyPerShot) {
            player.displayClientMessage(Component.literal("Not enough EU"), true);
            return InteractionResultHolder.fail(stack);
        }

        Vec3 look = player.getLookAngle();
        SmallFireball projectile = new SmallFireball(level, player, look.scale(1.5D));
        projectile.setPos(player.getX(), player.getEyeY() - 0.1D, player.getZ());
        ((ServerLevel) level).addFreshEntity(projectile);
        ElectricItemManager.discharge(stack, energyPerShot, false);
        return InteractionResultHolder.success(stack);
    }
}
