package ic2.core.block.generator;

import ic2.core.init.IC2BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class SolarGeneratorBlockEntity extends BasePassiveGeneratorBlockEntity {
    public SolarGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.SOLAR_GENERATOR.get(), pos, blockState, 2000, 1, "block.ic2.solar_generator");
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SolarGeneratorBlockEntity blockEntity) {
        blockEntity.serverTickInternal(level, pos, state);
    }

    @Override
    protected int getGenerationPerTick() {
        if (level == null || level.isClientSide || !level.isDay()) {
            return 0;
        }

        BlockPos skyPos = worldPosition.above();
        if (!level.canSeeSky(skyPos)) {
            return 0;
        }

        return (!level.isRainingAt(skyPos) && !level.isThundering()) ? 1 : 0;
    }
}
