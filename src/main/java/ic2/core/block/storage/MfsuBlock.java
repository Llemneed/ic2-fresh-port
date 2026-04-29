package ic2.core.block.storage;

import com.mojang.serialization.MapCodec;
import ic2.core.init.IC2BlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class MfsuBlock extends EnergyStorageBlock<MfsuBlockEntity> {
    public static final MapCodec<MfsuBlock> CODEC = simpleCodec(MfsuBlock::new);

    public MfsuBlock(BlockBehaviour.Properties properties) {
        super(properties, IC2BlockEntities.MFSU::get, MfsuBlockEntity::new);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
