package ic2.core.block.wiring;

import ic2.core.energy.EnergyConsumer;
import ic2.core.energy.EnergyTier;
import ic2.core.energy.EnergyTransport;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Blocks;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public final class CableBlockEntity extends BlockEntity implements EnergyTransport {
    private final CableType cableType;

    public CableBlockEntity(BlockPos pos, BlockState state) {
        this(resolveType(state), pos, state, resolveCableType(state));
    }

    private CableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, CableType cableType) {
        super(type, pos, state);
        this.cableType = cableType;
    }

    public CableType getCableType() {
        return cableType;
    }

    @Override
    public int routeEnergy(BlockPos sourcePos, int amount, EnergyTier sourceTier, Set<BlockPos> visited, boolean simulate) {
        if (level == null || amount <= 0) {
            return 0;
        }

        if (amount > cableType.packetLimit() || sourceTier.packetSize() > cableType.packetLimit()) {
            if (!simulate) {
                melt(level, getBlockPos(), getBlockState());
            }
            return 0;
        }

        if (!visited.add(getBlockPos())) {
            return 0;
        }

        int available = Math.max(0, amount - cableType.lossPerBlock());
        if (available <= 0) {
            return 0;
        }

        int transferred = 0;
        for (Direction direction : Direction.values()) {
            if (transferred >= available) {
                break;
            }

            BlockPos targetPos = worldPosition.relative(direction);
            if (targetPos.equals(sourcePos) || visited.contains(targetPos)) {
                continue;
            }

            BlockEntity blockEntity = level.getBlockEntity(targetPos);
            if (blockEntity == null) {
                continue;
            }

            int offered = available - transferred;
            int accepted = 0;
            if (blockEntity instanceof EnergyTransport transport) {
                accepted = transport.routeEnergy(worldPosition, offered, sourceTier, new HashSet<>(visited), simulate);
            } else if (blockEntity instanceof EnergyConsumer consumer && consumer.canReceiveEnergy()) {
                accepted = consumer.receiveEu(offered, sourceTier, simulate);
            }

            if (accepted > 0) {
                transferred += accepted;
            }
        }

        return transferred;
    }

    private static void melt(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) {
            return;
        }

        level.removeBlock(pos, false);
        level.levelEvent(2001, pos, Block.getId(state));
    }

    private static BlockEntityType<?> resolveType(BlockState state) {
        if (state.is(IC2Blocks.TIN_CABLE.get())) {
            return IC2BlockEntities.TIN_CABLE.get();
        }
        if (state.is(IC2Blocks.COPPER_CABLE.get())) {
            return IC2BlockEntities.COPPER_CABLE.get();
        }
        if (state.is(IC2Blocks.GOLD_CABLE.get())) {
            return IC2BlockEntities.GOLD_CABLE.get();
        }
        return IC2BlockEntities.GLASS_FIBRE_CABLE.get();
    }

    private static CableType resolveCableType(BlockState state) {
        if (state.is(IC2Blocks.TIN_CABLE.get())) {
            return CableType.TIN;
        }
        if (state.is(IC2Blocks.COPPER_CABLE.get())) {
            return CableType.COPPER;
        }
        if (state.is(IC2Blocks.GOLD_CABLE.get())) {
            return CableType.GOLD;
        }
        return CableType.GLASS_FIBRE;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }
}
