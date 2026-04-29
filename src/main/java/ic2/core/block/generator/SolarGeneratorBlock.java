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

public final class SolarGeneratorBlock extends PassiveGeneratorBlock<SolarGeneratorBlockEntity> {
    public static final MapCodec<SolarGeneratorBlock> CODEC = simpleCodec(SolarGeneratorBlock::new);

    public SolarGeneratorBlock(BlockBehaviour.Properties properties) {
        super(properties, IC2BlockEntities.SOLAR_GENERATOR, SolarGeneratorBlockEntity::new);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, IC2BlockEntities.SOLAR_GENERATOR.get(), SolarGeneratorBlockEntity::serverTick);
    }
}
