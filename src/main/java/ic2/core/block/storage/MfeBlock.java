package ic2.core.block.storage;

import com.mojang.serialization.MapCodec;
import ic2.core.init.IC2BlockEntities;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class MfeBlock extends EnergyStorageBlock<MfeBlockEntity> {
    public static final MapCodec<MfeBlock> CODEC = simpleCodec(MfeBlock::new);

    public MfeBlock(BlockBehaviour.Properties properties) {
        super(properties, IC2BlockEntities.MFE::get, MfeBlockEntity::new);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
