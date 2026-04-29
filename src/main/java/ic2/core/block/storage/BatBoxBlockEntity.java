package ic2.core.block.storage;

import ic2.core.init.IC2BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class BatBoxBlockEntity extends BaseEnergyStorageBlockEntity {
    public BatBoxBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.BATBOX.get(), pos, blockState, 40000, 32, 32, "block.ic2.batbox");
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BatBoxBlockEntity blockEntity) {
        BaseEnergyStorageBlockEntity.serverTick(level, pos, state, blockEntity);
    }
}
