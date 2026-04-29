package ic2.core.item.tool.electric;

import ic2.core.item.electric.BaseElectricItem;
import ic2.core.item.electric.ElectricItemManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class ScannerItem extends BaseElectricItem {
    private final int energyPerScan;
    private final double range;
    private final boolean advanced;

    public ScannerItem(int maxCharge, int transferLimit, int energyTier, int energyPerScan, double range, boolean advanced, Properties properties) {
        super(maxCharge, transferLimit, energyTier, properties);
        this.energyPerScan = energyPerScan;
        this.range = range;
        this.advanced = advanced;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }

        if (ElectricItemManager.getCharge(stack) < energyPerScan) {
            player.displayClientMessage(Component.literal("Not enough EU"), true);
            return InteractionResultHolder.fail(stack);
        }

        HitResult hitResult = player.pick(range, 0.0F, false);
        if (!(hitResult instanceof BlockHitResult blockHitResult)) {
            player.displayClientMessage(Component.literal("No target"), true);
            return InteractionResultHolder.pass(stack);
        }

        BlockPos pos = blockHitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);
        ElectricItemManager.discharge(stack, energyPerScan, false);
        player.displayClientMessage(Component.literal("Block: " + state.getBlock().getName().getString()), false);
        player.displayClientMessage(Component.literal("Hardness: " + state.getDestroySpeed(level, pos)), false);
        if (advanced) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            player.displayClientMessage(Component.literal("Pos: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()), false);
            player.displayClientMessage(Component.literal("Block Entity: " + (blockEntity == null ? "none" : blockEntity.getType().toString())), false);
        }
        return InteractionResultHolder.success(stack);
    }
}
