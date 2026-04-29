package ic2.core.block.generator;

import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Sounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public final class WaterGeneratorBlockEntity extends BasePassiveGeneratorBlockEntity {
    public WaterGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.WATER_GENERATOR.get(), pos, blockState, 2000, 4, "block.ic2.water_generator");
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WaterGeneratorBlockEntity blockEntity) {
        blockEntity.serverTickInternal(level, pos, state);
    }

    @Override
    protected int getGenerationPerTick() {
        if (level == null || level.isClientSide) {
            return 0;
        }

        int adjacentWater = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            FluidState fluid = level.getFluidState(worldPosition.relative(direction));
            if (fluid.getType() == Fluids.WATER && fluid.isSource()) {
                adjacentWater++;
            }
        }

        return Math.min(4, adjacentWater);
    }

    @Override
    protected SoundEvent getOperatingSound() {
        return IC2Sounds.WATERMILL_OPERATING.get();
    }
}
