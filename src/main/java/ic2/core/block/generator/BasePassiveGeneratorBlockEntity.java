package ic2.core.block.generator;

import ic2.core.energy.EnergyConsumer;
import ic2.core.energy.EnergyTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BasePassiveGeneratorBlockEntity extends BlockEntity implements MenuProvider {
    private final int maxEnergy;
    private final int outputPerTick;
    private final EnergyTier sourceTier;
    private final String displayKey;
    protected int energyStored;

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
        }

        pushEnergy();
        setChanged(level, pos, state);
    }

    protected abstract int getGenerationPerTick();

    private void pushEnergy() {
        if (level == null || energyStored <= 0) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if (energyStored <= 0) {
                return;
            }

            BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(direction));
            if (blockEntity instanceof EnergyConsumer consumer && consumer.canReceiveEnergy()) {
                int packet = Math.min(outputPerTick, energyStored);
                int sent = consumer.receiveEu(packet, sourceTier, false);
                energyStored -= sent;
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(displayKey);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("energyStored", energyStored);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energyStored = tag.getInt("energyStored");
    }

    public void dropContents() {
    }
}
