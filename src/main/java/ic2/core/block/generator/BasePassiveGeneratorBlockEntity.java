package ic2.core.block.generator;

import ic2.core.block.entity.AbstractEuBlockEntity;
import ic2.core.energy.EnergyNetHelper;
import ic2.core.energy.EnergyTier;
import ic2.core.sound.MachineSoundHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BasePassiveGeneratorBlockEntity extends AbstractEuBlockEntity implements MenuProvider {
    private final int maxEnergy;
    private final int outputPerTick;
    private final EnergyTier sourceTier;
    private final String displayKey;

    protected BasePassiveGeneratorBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState blockState,
            int maxEnergy,
            int outputPerTick,
            String displayKey
    ) {
        super(type, pos, blockState);
        this.maxEnergy = maxEnergy;
        this.outputPerTick = outputPerTick;
        this.sourceTier = EnergyTier.forPacket(outputPerTick);
        this.displayKey = displayKey;
    }

    protected void serverTickInternal(Level level, BlockPos pos, BlockState state) {
        int generated = getGenerationPerTick();
        if (generated > 0) {
            energyStored = Math.min(maxEnergy, energyStored + generated);
            SoundEvent sound = getOperatingSound();
            if (sound != null) {
                MachineSoundHelper.playPeriodic(level, pos, sound);
            }
        }

        pushEnergy();
        setChanged(level, pos, state);
    }

    protected abstract int getGenerationPerTick();

    protected SoundEvent getOperatingSound() {
        return null;
    }

    private void pushEnergy() {
        if (level == null || energyStored <= 0) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if (energyStored <= 0) {
                return;
            }

            int packet = Math.min(outputPerTick, energyStored);
            int sent = EnergyNetHelper.sendEnergy(level, worldPosition, direction, packet, sourceTier);
            energyStored -= sent;
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(displayKey);
    }

    @Override
    public int getMaxEnergyStored() {
        return maxEnergy;
    }

    @Override
    public int receiveEnergy(int amount) {
        return 0;
    }

    @Override
    public boolean canReceiveEnergy() {
        return false;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return null;
    }

    public void dropContents() {
    }
}
