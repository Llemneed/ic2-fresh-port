package ic2.core.block.storage;

import ic2.core.init.IC2BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class MfsuBlockEntity extends BaseEnergyStorageBlockEntity {
    public MfsuBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.MFSU.get(), pos, blockState, 40000000, 2048, 2048, "block.ic2.mfsu");
    }
}
