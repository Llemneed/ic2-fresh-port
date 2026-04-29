package ic2.core.item.tool.electric;

import ic2.core.item.electric.BaseElectricItem;
import ic2.core.item.electric.ElectricItemManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class MiningLaserItem extends BaseElectricItem {
    private final int energyPerShot;
    private final double range;

    public MiningLaserItem(int maxCharge, int transferLimit, int energyTier, int energyPerShot, double range, Properties properties) {
        super(maxCharge, transferLimit, energyTier, properties);
        this.energyPerShot = energyPerShot;
        this.range = range;
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

        HitResult hitResult = player.pick(range, 0.0F, false);
        if (!(hitResult instanceof BlockHitResult blockHitResult)) {
            return InteractionResultHolder.pass(stack);
        }

        BlockState state = level.getBlockState(blockHitResult.getBlockPos());
        if (state.isAir() || state.getDestroySpeed(level, blockHitResult.getBlockPos()) < 0.0F) {
            return InteractionResultHolder.pass(stack);
        }

        ElectricItemManager.discharge(stack, energyPerShot, false);
        ((ServerLevel) level).destroyBlock(blockHitResult.getBlockPos(), true, player);
        return InteractionResultHolder.success(stack);
    }
}
