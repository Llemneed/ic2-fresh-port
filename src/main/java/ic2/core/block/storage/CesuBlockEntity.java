package ic2.core.block.storage;

import ic2.core.init.IC2BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class CesuBlockEntity extends BaseEnergyStorageBlockEntity {
    public CesuBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.CESU.get(), pos, blockState, 300000, 128, 128, "block.ic2.cesu");
    }
}
