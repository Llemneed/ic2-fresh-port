package ic2.core.block.entity;

import ic2.core.energy.EnergyConsumer;
import ic2.core.energy.EnergyNetHelper;
import ic2.core.energy.EnergyTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractEuBlockEntity extends BlockEntity implements EnergyConsumer {
    protected int energyStored;

    protected AbstractEuBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public int getEnergyStored() {
        return energyStored;
    }

    @Override
    public int receiveEnergy(int amount) {
        int accepted = Math.min(Math.min(amount, maxInputPerTick()), getMaxEnergyStored() - energyStored);
        energyStored += accepted;
        if (accepted > 0) {
            setChanged();
        }
        return accepted;
    }

    @Override
    public boolean canReceiveEnergy() {
        return energyStored < getMaxEnergyStored();
    }

    @Override
    public void onOvervoltage(int amount) {
        Level level = getLevel();
        if (level == null || level.isClientSide) {
            return;
        }

        BlockPos pos = getBlockPos();
        level.removeBlock(pos, false);
        level.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 2.0F, Level.ExplosionInteraction.BLOCK);
    }

    protected final int pushEnergyToAllSides(int maxPacket, EnergyTier sourceTier) {
        Level level = getLevel();
        if (level == null || energyStored <= 0 || maxPacket <= 0) {
            return 0;
        }

        int totalSent = 0;
        for (Direction direction : Direction.values()) {
            if (energyStored <= 0) {
                break;
            }

            int packet = Math.min(maxPacket, energyStored);
            int sent = EnergyNetHelper.sendEnergy(level, worldPosition, direction, packet, sourceTier);
            if (sent > 0) {
                energyStored -= sent;
                totalSent += sent;
            }
        }

        return totalSent;
    }

    protected final int pushEnergyToSide(Direction direction, int maxPacket, EnergyTier sourceTier) {
        Level level = getLevel();
        if (level == null || energyStored <= 0 || maxPacket <= 0) {
            return 0;
        }

        int packet = Math.min(maxPacket, energyStored);
        int sent = EnergyNetHelper.sendEnergy(level, worldPosition, direction, packet, sourceTier);
        if (sent > 0) {
            energyStored -= sent;
        }
        return sent;
    }

    public abstract int getMaxEnergyStored();
}
