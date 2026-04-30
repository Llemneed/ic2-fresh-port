package ic2.core.block.entity;

import ic2.core.item.electric.ElectricItemManager;
import ic2.core.item.upgrade.MachineUpgradeItem;
import ic2.core.item.upgrade.MachineUpgradeItem.UpgradeType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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

    public static record ProcessingOperation(ItemStack result, int ingredientCount, float experience) {
        public static ProcessingOperation empty() {
            return new ProcessingOperation(ItemStack.EMPTY, 0, 0.0F);
        }

        public boolean isEmpty() {
            return result.isEmpty();
        }
    }

    protected final void tickProcessing(Level level, BlockPos pos, BlockState state) {
        consumeChargeItem();

        ItemStack input = inventory.getStackInSlot(inputSlot);
        ProcessingOperation operation = getProcessingOperation(input);
        int energyPerTick = getOperationEnergyPerTick();
        int maxProgress = getOperationMaxProgress();
        boolean canProcess = canWorkWithRedstone()
                && !operation.isEmpty()
                && canOutputStack(operation.result())
                && energyStored >= energyPerTick;

        if (canProcess) {
            energyStored -= energyPerTick;
            progress++;
            playProcessingSound(level, pos);

            if (progress >= maxProgress) {
                progress = 0;
                ItemStack completedInput = input.copy();
                onProcessingCompleted(completedInput, operation);
                completeProcessing(completedInput, operation);
            }
        } else if (progress != 0) {
            progress = 0;
        }

        updateActiveState(level, pos, state, canProcess);

        if (canProcess || progress == 0) {
            setChanged(level, pos, state);
        }
    }

    protected ProcessingOperation getProcessingOperation(ItemStack input) {
        return ProcessingOperation.empty();
    }

    protected int getOperationEnergyPerTick() {
        return 0;
    }

    protected int getOperationMaxProgress() {
        return 0;
    }

    protected void playProcessingSound(Level level, BlockPos pos) {
    }

    protected void updateActiveState(Level level, BlockPos pos, BlockState state, boolean active) {
    }

    protected void onProcessingCompleted(ItemStack input, ProcessingOperation operation) {
    }

    protected void completeProcessing(ItemStack input, ProcessingOperation operation) {
        consumeInputsForOperation(operation);
        insertOperationResult(operation);
    }

    protected final boolean canOutputStack(ItemStack result) {
        ItemStack output = inventory.getStackInSlot(outputSlot);
        if (output.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(output, result)) {
            return false;
        }
        return output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    protected void consumeInputsForOperation(ProcessingOperation operation) {
        inventory.extractItem(inputSlot, Math.max(1, operation.ingredientCount()), false);
    }

    protected final void insertOperationResult(ProcessingOperation operation) {
        ItemStack output = inventory.getStackInSlot(outputSlot);
        ItemStack result = operation.result().copy();

        if (output.isEmpty()) {
            inventory.setStackInSlot(outputSlot, result);
        } else {
            output.grow(result.getCount());
            inventory.setStackInSlot(outputSlot, output);
        }
    }
}
