package ic2.core.block.storage;

import ic2.core.block.entity.AbstractEuInventoryBlockEntity;
import ic2.core.energy.EnergyConsumer;
import ic2.core.energy.EnergyTier;
import ic2.core.item.electric.ElectricItemManager;
import ic2.core.menu.BatBoxMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseEnergyStorageBlockEntity extends AbstractEuInventoryBlockEntity implements MenuProvider, EnergyConsumer {
    protected static final int CHARGE_SLOT = 0;
    protected static final int DISCHARGE_SLOT = 1;

    private final int maxEnergy;
    private final int outputPerTick;
    private final int inputPerTick;
    private final EnergyTier sinkTier;
    private final EnergyTier sourceTier;
    private final String displayKey;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energyStored;
                case 1 -> maxEnergy;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                energyStored = value;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };
    protected BaseEnergyStorageBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState blockState,
            int maxEnergy,
            int inputPerTick,
            int outputPerTick,
            String displayKey
    ) {
        super(type, pos, blockState, 2);
        this.maxEnergy = maxEnergy;
        this.outputPerTick = outputPerTick;
        this.inputPerTick = inputPerTick;
        this.sinkTier = EnergyTier.forPacket(inputPerTick);
        this.sourceTier = EnergyTier.forPacket(outputPerTick);
        this.displayKey = displayKey;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BaseEnergyStorageBlockEntity blockEntity) {
        blockEntity.dischargeItemIntoStorage();
        blockEntity.chargeItemFromStorage();
        blockEntity.pushEnergy();
        blockEntity.afterBaseTick();
        setChanged(level, pos, state);
    }

    public ContainerData getData() {
        return data;
    }

    public int getEnergyStored() {
        return energyStored;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getMaxEnergyStored() {
        return maxEnergy;
    }

    public int getOutputPerTick() {
        return outputPerTick;
    }

    public void chargePlayerInventory(Inventory playerInventory) {
        if (level == null || level.isClientSide || energyStored <= 0) {
            return;
        }

        int budget = Math.min(outputPerTick, energyStored);
        int before = budget;

        for (var target : playerInventory.items) {
            if (budget <= 0) {
                break;
            }
            budget = chargeTarget(target, budget);
        }

        for (var target : playerInventory.offhand) {
            if (budget <= 0) {
                break;
            }
            budget = chargeTarget(target, budget);
        }

        for (var target : playerInventory.armor) {
            if (budget <= 0) {
                break;
            }
            budget = chargeTarget(target, budget);
        }

        int used = before - budget;
        if (used > 0) {
            energyStored -= used;
            setChanged();
        }
    }

    public boolean isChargeableItem(net.minecraft.world.item.ItemStack stack) {
        return ElectricItemManager.canAcceptEnergy(stack);
    }

    public boolean isDischargeableItem(net.minecraft.world.item.ItemStack stack) {
        return ElectricItemManager.canProvideEnergy(stack) && ElectricItemManager.hasEnergy(stack);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(displayKey);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BatBoxMenu(containerId, playerInventory, this, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveInventory(tag, registries);
        tag.putInt("energyStored", energyStored);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            loadInventory(tag.getCompound("inventory"), registries);
        }
        energyStored = tag.getInt("energyStored");
    }

    @Override
    public int receiveEnergy(int amount) {
        int accepted = Math.min(Math.min(amount, maxInputPerTick()), maxEnergy - energyStored);
        energyStored += accepted;
        if (accepted > 0) {
            setChanged();
        }
        return accepted;
    }

    @Override
    public boolean canReceiveEnergy() {
        return energyStored < maxEnergy;
    }

    @Override
    public int maxInputPerTick() {
        return inputPerTick;
    }

    @Override
    public EnergyTier getSinkTier() {
        return sinkTier;
    }

    @Override
    public void onOvervoltage(int amount) {
        if (level == null || level.isClientSide) {
            return;
        }

        BlockPos pos = getBlockPos();
        level.removeBlock(pos, false);
        level.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 2.0F, Level.ExplosionInteraction.BLOCK);
    }

    private void chargeItemFromStorage() {
        if (energyStored <= 0) {
            return;
        }

        var stack = inventory.getStackInSlot(CHARGE_SLOT);
        if (!isChargeableItem(stack)) {
            return;
        }

        int moved = ElectricItemManager.charge(stack, Math.min(outputPerTick, energyStored));
        if (moved > 0) {
            energyStored -= moved;
            inventory.setStackInSlot(CHARGE_SLOT, stack);
            setChanged();
        }
    }

    private void dischargeItemIntoStorage() {
        if (energyStored >= maxEnergy) {
            return;
        }

        var stack = inventory.getStackInSlot(DISCHARGE_SLOT);
        if (!isDischargeableItem(stack)) {
            return;
        }

        int moved = ElectricItemManager.discharge(stack, Math.min(inputPerTick, maxEnergy - energyStored), true);
        if (moved > 0) {
            energyStored += moved;
            inventory.setStackInSlot(DISCHARGE_SLOT, stack);
            setChanged();
        }
    }

    private void pushEnergy() {
        if (level == null || energyStored <= 0 || !(getBlockState().getBlock() instanceof EnergyStorageBlock<?>)) {
            return;
        }

        Direction outputSide = getBlockState().getValue(EnergyStorageBlock.FACING);
        pushEnergyToSide(outputSide, outputPerTick, sourceTier);
    }

    private int chargeTarget(net.minecraft.world.item.ItemStack target, int budget) {
        if (target.isEmpty() || !ElectricItemManager.canAcceptEnergy(target)) {
            return budget;
        }

        int accepted = ElectricItemManager.charge(target, budget);
        return budget - accepted;
    }

    protected void afterBaseTick() {
    }
}
