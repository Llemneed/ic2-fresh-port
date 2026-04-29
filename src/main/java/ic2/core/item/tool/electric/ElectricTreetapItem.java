package ic2.core.item.tool.electric;

import ic2.core.block.RubberWoodBlock;
import ic2.core.init.IC2Items;
import ic2.core.item.electric.BaseElectricItem;
import ic2.core.item.electric.ElectricItemManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class ElectricTreetapItem extends BaseElectricItem {
    private static final int ENERGY_PER_USE = 50;

    public ElectricTreetapItem(int maxCharge, int transferLimit, int energyTier, Properties properties) {
        super(maxCharge, transferLimit, energyTier, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Direction side = context.getClickedFace();
        ItemStack stack = context.getItemInHand();

        if (!(state.getBlock() instanceof RubberWoodBlock rubberWood) || !rubberWood.canExtractResin(state, side)) {
            return InteractionResult.PASS;
        }

        if (ElectricItemManager.getCharge(stack) < ENERGY_PER_USE) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            int quantity = rubberWood.extractResin(level, pos, state, side, level.getRandom());
            if (quantity > 0) {
                ejectResin(level, pos, side, quantity);
                ElectricItemManager.discharge(stack, ENERGY_PER_USE, false);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private static void ejectResin(Level level, BlockPos pos, Direction side, int quantity) {
        double x = pos.getX() + 0.5 + side.getStepX() * 0.35;
        double y = pos.getY() + 0.5 + side.getStepY() * 0.35;
        double z = pos.getZ() + 0.5 + side.getStepZ() * 0.35;

        for (int i = 0; i < quantity; i++) {
            ItemEntity resinEntity = new ItemEntity(level, x, y, z, new ItemStack(IC2Items.STICKY_RESIN.get()));
            resinEntity.setDefaultPickUpDelay();
            level.addFreshEntity(resinEntity);
        }
    }
}
