package ic2.core.energy;

import java.util.HashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class EnergyNetHelper {
    private EnergyNetHelper() {
    }

    public static int sendEnergy(Level level, BlockPos fromPos, Direction direction, int amount, EnergyTier sourceTier) {
        if (amount <= 0) {
            return 0;
        }

        BlockPos targetPos = fromPos.relative(direction);
        BlockEntity blockEntity = level.getBlockEntity(targetPos);
        if (blockEntity == null) {
            return 0;
        }

        if (blockEntity instanceof EnergyTransport transport) {
            return transport.routeEnergy(fromPos, amount, sourceTier, new HashSet<>(), false);
        }

        if (blockEntity instanceof EnergyConsumer consumer && consumer.canReceiveEnergy()) {
            return consumer.receiveEu(amount, sourceTier, false);
        }

        return 0;
    }
}
