package ic2.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractMachineBlockEntity extends AbstractEuInventoryBlockEntity {
    protected AbstractMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int slotCount) {
        super(type, pos, blockState, slotCount);
    }
}
