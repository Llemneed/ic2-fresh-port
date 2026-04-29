package ic2.core.block.generator;

import com.mojang.serialization.MapCodec;
import ic2.core.init.IC2BlockEntities;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class WindGeneratorBlock extends PassiveGeneratorBlock<WindGeneratorBlockEntity> {
    public static final MapCodec<WindGeneratorBlock> CODEC = simpleCodec(WindGeneratorBlock::new);

    public WindGeneratorBlock(BlockBehaviour.Properties properties) {
        super(properties, IC2BlockEntities.WIND_GENERATOR, WindGeneratorBlockEntity::new);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, IC2BlockEntities.WIND_GENERATOR.get(), WindGeneratorBlockEntity::serverTick);
    }
}
