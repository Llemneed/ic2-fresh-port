package ic2.core.block.storage;

import ic2.core.init.IC2BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class ChargepadBatBoxBlockEntity extends BaseChargepadBlockEntity {
    public ChargepadBatBoxBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.CHARGEPAD_BATBOX.get(), pos, blockState, 40000, 32, 32, 32, "block.ic2.chargepad_batbox");
    }
}
