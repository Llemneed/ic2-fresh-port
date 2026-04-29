package ic2.core.block.generator;

import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Sounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class WindGeneratorBlockEntity extends BasePassiveGeneratorBlockEntity {
    public WindGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.WIND_GENERATOR.get(), pos, blockState, 4000, 8, "block.ic2.wind_generator");
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WindGeneratorBlockEntity blockEntity) {
        blockEntity.serverTickInternal(level, pos, state);
    }

    @Override
    protected int getGenerationPerTick() {
        if (level == null || level.isClientSide) {
            return 0;
        }

        BlockPos skyPos = worldPosition.above();
        if (!level.canSeeSky(skyPos)) {
            return 0;
        }

        int base = 1;
        int heightBonus = Math.max(0, (worldPosition.getY() - 64) / 32);
        int weatherBonus = level.isThundering() ? 2 : (level.isRaining() ? 1 : 0);
        return Math.min(8, base + heightBonus + weatherBonus);
    }

    @Override
    protected SoundEvent getOperatingSound() {
        return IC2Sounds.WINDMILL_OPERATING.get();
    }
}
