package ic2.core.block.storage;

import com.mojang.serialization.MapCodec;
import ic2.core.init.IC2BlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class BatBoxBlock extends EnergyStorageBlock<BatBoxBlockEntity> {
    public static final MapCodec<BatBoxBlock> CODEC = simpleCodec(BatBoxBlock::new);

    public BatBoxBlock(BlockBehaviour.Properties properties) {
        super(properties, IC2BlockEntities.BATBOX::get, BatBoxBlockEntity::new);
    }

    @Override
    protected MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
        return CODEC;
    }
}
