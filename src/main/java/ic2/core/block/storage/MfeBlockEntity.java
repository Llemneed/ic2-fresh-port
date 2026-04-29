package ic2.core.block.storage;

import ic2.core.init.IC2BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class MfeBlockEntity extends BaseEnergyStorageBlockEntity {
    public MfeBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.MFE.get(), pos, blockState, 4000000, 512, 512, "block.ic2.mfe");
    }
}
