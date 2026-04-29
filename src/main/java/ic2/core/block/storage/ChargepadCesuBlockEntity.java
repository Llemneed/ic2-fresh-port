package ic2.core.block.storage;

import ic2.core.init.IC2BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class ChargepadCesuBlockEntity extends BaseChargepadBlockEntity {
    public ChargepadCesuBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.CHARGEPAD_CESU.get(), pos, blockState, 300000, 128, 128, 128, "block.ic2.chargepad_cesu");
    }
}
