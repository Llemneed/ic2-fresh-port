package ic2.core.block.storage;

import ic2.core.init.IC2BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class ChargepadMfeBlockEntity extends BaseChargepadBlockEntity {
    public ChargepadMfeBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.CHARGEPAD_MFE.get(), pos, blockState, 4000000, 512, 512, 512, "block.ic2.chargepad_mfe");
    }
}
