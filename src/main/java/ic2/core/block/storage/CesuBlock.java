package ic2.core.block.storage;

import com.mojang.serialization.MapCodec;
import ic2.core.init.IC2BlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class CesuBlock extends EnergyStorageBlock<CesuBlockEntity> {
    public static final MapCodec<CesuBlock> CODEC = simpleCodec(CesuBlock::new);

    public CesuBlock(BlockBehaviour.Properties properties) {
        super(properties, IC2BlockEntities.CESU::get, CesuBlockEntity::new);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
