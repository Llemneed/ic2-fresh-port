package ic2.core.block.storage;

import ic2.core.init.IC2BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class ChargepadMfsuBlockEntity extends BaseChargepadBlockEntity {
    public ChargepadMfsuBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.CHARGEPAD_MFSU.get(), pos, blockState, 40000000, 2048, 2048, 2048, "block.ic2.chargepad_mfsu");
    }
}
