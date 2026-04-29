package ic2.core.block.generator;

import ic2.core.init.IC2BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class StirlingGeneratorBlockEntity extends GeneratorBlockEntity {
    public StirlingGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.STIRLING_GENERATOR.get(), pos, blockState, 20, 8000, 32, "block.ic2.stirling_generator");
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, StirlingGeneratorBlockEntity blockEntity) {
        blockEntity.serverTickInternal(level, pos, state);
    }
}
