package ic2.core.block.storage;

import com.mojang.serialization.MapCodec;
import ic2.core.init.IC2BlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ChargepadMfsuBlock extends ChargepadBlock<ChargepadMfsuBlockEntity> {
    public static final MapCodec<ChargepadMfsuBlock> CODEC = simpleCodec(ChargepadMfsuBlock::new);

    public ChargepadMfsuBlock(BlockBehaviour.Properties properties) {
        super(properties, IC2BlockEntities.CHARGEPAD_MFSU::get, ChargepadMfsuBlockEntity::new);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
