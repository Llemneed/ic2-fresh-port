package ic2.core.item.tool.electric;

import ic2.core.item.electric.BaseElectricItem;
import ic2.core.item.electric.ElectricItemManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class ElectricWrenchItem extends BaseElectricItem {
    private static final int ENERGY_PER_USE = 100;

    public ElectricWrenchItem(int maxCharge, int transferLimit, int energyTier, Properties properties) {
        super(maxCharge, transferLimit, energyTier, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = context.getItemInHand();
        if (ElectricItemManager.getCharge(stack) < ENERGY_PER_USE) {
            context.getPlayer().displayClientMessage(Component.literal("Not enough EU"), true);
            return InteractionResult.FAIL;
        }

        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        BlockState rotated = rotateState(state);
        if (rotated.equals(state)) {
            return InteractionResult.PASS;
        }

        ((ServerLevel) level).setBlock(pos, rotated, 3);
        ElectricItemManager.discharge(stack, ENERGY_PER_USE, false);
        return InteractionResult.SUCCESS;
    }

    private static BlockState rotateState(BlockState state) {
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            DirectionProperty property = BlockStateProperties.HORIZONTAL_FACING;
            return state.setValue(property, state.getValue(property).getClockWise());
        }

        if (state.hasProperty(BlockStateProperties.FACING)) {
            DirectionProperty property = BlockStateProperties.FACING;
            Direction current = state.getValue(property);
            Direction next = switch (current) {
                case DOWN -> Direction.UP;
                case UP -> Direction.NORTH;
                case NORTH -> Direction.SOUTH;
                case SOUTH -> Direction.WEST;
                case WEST -> Direction.EAST;
                case EAST -> Direction.DOWN;
            };
            return state.setValue(property, next);
        }

        if (state.hasProperty(BlockStateProperties.AXIS)) {
            EnumProperty<Direction.Axis> property = BlockStateProperties.AXIS;
            Direction.Axis current = state.getValue(property);
            Direction.Axis next = switch (current) {
                case X -> Direction.Axis.Y;
                case Y -> Direction.Axis.Z;
                case Z -> Direction.Axis.X;
            };
            return state.setValue(property, next);
        }

        return state.rotate(Rotation.CLOCKWISE_90);
    }
}
