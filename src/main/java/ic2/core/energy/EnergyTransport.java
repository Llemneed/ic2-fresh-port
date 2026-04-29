package ic2.core.energy;

import java.util.Set;
import net.minecraft.core.BlockPos;

public interface EnergyTransport {
    int routeEnergy(BlockPos sourcePos, int amount, EnergyTier sourceTier, Set<BlockPos> visited, boolean simulate);
}
