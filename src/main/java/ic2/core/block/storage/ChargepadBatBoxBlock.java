package ic2.core.block.storage;

import com.mojang.serialization.MapCodec;
import ic2.core.init.IC2BlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ChargepadBatBoxBlock extends ChargepadBlock<ChargepadBatBoxBlockEntity> {
    public static final MapCodec<ChargepadBatBoxBlock> CODEC = simpleCodec(ChargepadBatBoxBlock::new);

    public ChargepadBatBoxBlock(BlockBehaviour.Properties properties) {
        super(properties, IC2BlockEntities.CHARGEPAD_BATBOX::get, ChargepadBatBoxBlockEntity::new);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
