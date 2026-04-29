package ic2.core.block.storage;

import com.mojang.serialization.MapCodec;
import ic2.core.init.IC2BlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ChargepadCesuBlock extends ChargepadBlock<ChargepadCesuBlockEntity> {
    public static final MapCodec<ChargepadCesuBlock> CODEC = simpleCodec(ChargepadCesuBlock::new);

    public ChargepadCesuBlock(BlockBehaviour.Properties properties) {
        super(properties, IC2BlockEntities.CHARGEPAD_CESU::get, ChargepadCesuBlockEntity::new);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
