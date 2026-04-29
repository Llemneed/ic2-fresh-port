package ic2.core.block.entity;

import ic2.core.item.electric.ElectricItemManager;
import ic2.core.item.upgrade.MachineUpgradeItem;
import ic2.core.item.upgrade.MachineUpgradeItem.UpgradeType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractProcessingMachineBlockEntity extends AbstractMachineBlockEntity {
    protected final int inputSlot;
    protected final int outputSlot;
    protected final int upgradeStart;
    protected final int upgradeEnd;
    protected final int chargeSlot;
    protected final int baseEnergyCapacity;
    protected final int energyPerRedstoneCharge;
    protected final int energyStoragePerUpgrade;
    protected final int[] inputTiers;
    protected int progress;

    protected AbstractProcessingMachineBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState blockState,
            int slotCount,
            int inputSlot,
            int outputSlot,
            int upgradeStart,
            int upgradeEnd,
            int chargeSlot,
            int baseEnergyCapacity,
            int energyPerRedstoneCharge,
            int energyStoragePerUpgrade,
            int[] inputTiers
    ) {
        super(type, pos, blockState, slotCount);
        this.inputSlot = inputSlot;
        this.outputSlot = outputSlot;
        this.upgradeStart = upgradeStart;
        this.upgradeEnd = upgradeEnd;
        this.chargeSlot = chargeSlot;
        this.baseEnergyCapacity = baseEnergyCapacity;
        this.energyPerRedstoneCharge = energyPerRedstoneCharge;
        this.energyStoragePerUpgrade = energyStoragePerUpgrade;
        this.inputTiers = inputTiers.clone();
    }

    @Override
    public int getMaxEnergyStored() {
        return baseEnergyCapacity + getUpgradeCount(UpgradeType.ENERGY_STORAGE) * energyStoragePerUpgrade;
    }

    @Override
    public int maxInputPerTick() {
        int tier = Math.min(getUpgradeCount(UpgradeType.TRANSFORMER), inputTiers.length - 1);
        return inputTiers[tier];
    }

    public boolean isChargeItem(ItemStack stack) {
        return stack.is(Items.REDSTONE) || ElectricItemManager.canProvideEnergy(stack);
    }

    protected void consumeChargeItem() {
        ItemStack chargeStack = inventory.getStackInSlot(chargeSlot);
        if (!isChargeItem(chargeStack)) {
            return;
        }

        int missing = getMaxEnergyStored() - energyStored;
        if (missing <= 0) {
            return;
        }

        int discharged = ElectricItemManager.discharge(chargeStack, missing, true);
        if (discharged > 0) {
            inventory.setStackInSlot(chargeSlot, chargeStack);
            energyStored = Math.min(getMaxEnergyStored(), energyStored + discharged);
            setChanged();
            return;
        }

        if (energyStored > getMaxEnergyStored() - energyPerRedstoneCharge) {
            return;
        }

        chargeStack.shrink(1);
        inventory.setStackInSlot(chargeSlot, chargeStack);
        energyStored = Math.min(getMaxEnergyStored(), energyStored + energyPerRedstoneCharge);
        setChanged();
    }

    protected int getUpgradeCount(UpgradeType type) {
        int upgrades = 0;
        for (int slot = upgradeStart; slot <= upgradeEnd; slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (getUpgradeType(stack) == type) {
                upgrades += stack.getCount();
            }
        }
        return upgrades;
    }

    protected UpgradeType getUpgradeType(ItemStack stack) {
        if (!(stack.getItem() instanceof MachineUpgradeItem upgradeItem)) {
            return null;
        }
        return upgradeItem.getType();
    }

    protected boolean canWorkWithRedstone() {
        if (level == null) {
            return true;
        }

        if (getUpgradeCount(UpgradeType.REDSTONE_INVERTER) <= 0) {
            return true;
        }

        return level.hasNeighborSignal(worldPosition);
    }

    protected int scaledProgress(int baseMaxProgress, int minimumProgress) {
        int upgrades = Math.min(4, getUpgradeCount(UpgradeType.OVERCLOCKER));
        double scaledProgress = baseMaxProgress * Math.pow(0.7D, upgrades);
        return Math.max(minimumProgress, Mth.ceil(scaledProgress));
    }

    protected int scaledEnergyPerTick(int baseEnergyPerTick) {
        int upgrades = Math.min(4, getUpgradeCount(UpgradeType.OVERCLOCKER));
        return Math.max(baseEnergyPerTick, Mth.ceil(baseEnergyPerTick * Math.pow(1.6D, upgrades)));
    }
}
