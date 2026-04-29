package ic2.core.block.machine;

import ic2.core.energy.EnergyConsumer;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Blocks;
import ic2.core.init.IC2Items;
import ic2.core.item.electric.ElectricItemManager;
import ic2.core.item.upgrade.MachineUpgradeItem;
import ic2.core.item.upgrade.MachineUpgradeItem.UpgradeType;
import ic2.core.menu.SolidCannerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class SolidCannerBlockEntity extends BlockEntity implements MenuProvider, EnergyConsumer {
    private static final int SLOT_COUNT = 8;
    private static final int INPUT_SLOT = 0;
    private static final int CAN_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;
    private static final int UPGRADE_START = 3;
    private static final int UPGRADE_END = 6;
    private static final int CHARGE_SLOT = 7;
    private static final int BASE_MAX_PROGRESS = 100;
    private static final int BASE_ENERGY_PER_TICK = 2;
    private static final int MAX_ENERGY = 1000;
    private static final int ENERGY_PER_REDSTONE_CHARGE = 200;
    private static final int ENERGY_STORAGE_PER_UPGRADE = 10000;
    private static final int[] INPUT_TIERS = {32, 128, 512, 2048, 8192};

    private final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> getMaxProgress();
                case 2 -> energyStored;
                case 3 -> getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 2 -> energyStored = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    private int progress;
    private int energyStored;

    public SolidCannerBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.SOLID_CANNER.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SolidCannerBlockEntity blockEntity) {
        blockEntity.consumeChargeItem();

        ItemStack output = blockEntity.getCanningResult(
                blockEntity.inventory.getStackInSlot(INPUT_SLOT),
                blockEntity.inventory.getStackInSlot(CAN_SLOT)
        );
        int energyPerTick = blockEntity.getEnergyPerTick();
        int maxProgress = blockEntity.getMaxProgress();
        boolean canProcess = blockEntity.canWorkWithRedstone()
                && !output.isEmpty()
                && blockEntity.canOutput(output)
                && blockEntity.energyStored >= energyPerTick;

        if (canProcess) {
            blockEntity.energyStored -= energyPerTick;
            blockEntity.progress++;

            if (blockEntity.progress >= maxProgress) {
                blockEntity.progress = 0;
                blockEntity.process(output.copy());
            }
        } else if (blockEntity.progress != 0) {
            blockEntity.progress = 0;
        }

        boolean active = canProcess;
        if (state.getBlock() instanceof SolidCannerBlock && state.getValue(SolidCannerBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(SolidCannerBlock.ACTIVE, active), Block.UPDATE_CLIENTS);
        }

        if (canProcess || blockEntity.progress == 0) {
            setChanged(level, pos, state);
        }
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public ContainerData getData() {
        return data;
    }

    public void dropContents() {
        if (level == null || level.isClientSide) {
            return;
        }

        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, stack.copy()));
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ic2.solid_canner");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SolidCannerMenu(containerId, playerInventory, this, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("progress", progress);
        tag.putInt("energy", energyStored);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadInventory(tag.getCompound("inventory"), registries);
        progress = tag.getInt("progress");
        energyStored = tag.getInt("energy");
    }

    public boolean hasRecipe(ItemStack input, ItemStack canInput) {
        return !getCanningResult(input, canInput).isEmpty();
    }

    public boolean isInput(ItemStack stack) {
        return isCannableFood(stack);
    }

    public boolean isTinCan(ItemStack stack) {
        return stack.is(IC2Items.TIN_CAN.get());
    }

    public boolean isUpgrade(ItemStack stack) {
        return getUpgradeType(stack) != null;
    }

    public boolean isChargeItem(ItemStack stack) {
        return stack.is(Items.REDSTONE) || ElectricItemManager.canProvideEnergy(stack);
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
    public int maxInputPerTick() {
        int tier = Math.min(getUpgradeCount(UpgradeType.TRANSFORMER), INPUT_TIERS.length - 1);
        return INPUT_TIERS[tier];
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

    public int getMaxEnergyStored() {
        return MAX_ENERGY + getUpgradeCount(UpgradeType.ENERGY_STORAGE) * ENERGY_STORAGE_PER_UPGRADE;
    }

    private void process(ItemStack result) {
        inventory.extractItem(INPUT_SLOT, 1, false);
        inventory.extractItem(CAN_SLOT, 1, false);
        ItemStack output = inventory.getStackInSlot(OUTPUT_SLOT);

        if (output.isEmpty()) {
            inventory.setStackInSlot(OUTPUT_SLOT, result);
        } else {
            output.grow(result.getCount());
            inventory.setStackInSlot(OUTPUT_SLOT, output);
        }
    }

    private ItemStack getCanningResult(ItemStack input, ItemStack canInput) {
        if (!isCannableFood(input) || !isTinCan(canInput)) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(IC2Items.FILLED_TIN_CAN.get());
    }

    private boolean canOutput(ItemStack result) {
        ItemStack output = inventory.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(output, result)) {
            return false;
        }
        return output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    private boolean isCannableFood(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.is(Items.BREAD)
                || stack.is(Items.APPLE)
                || stack.is(Items.BAKED_POTATO)
                || stack.is(Items.CARROT)
                || stack.is(Items.POTATO)
                || stack.is(Items.COOKED_BEEF)
                || stack.is(Items.COOKED_PORKCHOP)
                || stack.is(Items.COOKED_CHICKEN)
                || stack.is(Items.COOKED_MUTTON)
                || stack.is(Items.COOKED_RABBIT)
                || stack.is(Items.COOKED_COD)
                || stack.is(Items.COOKED_SALMON);
    }

    private void consumeChargeItem() {
        ItemStack chargeStack = inventory.getStackInSlot(CHARGE_SLOT);
        if (!isChargeItem(chargeStack)) {
            return;
        }

        int missing = getMaxEnergyStored() - energyStored;
        if (missing <= 0) {
            return;
        }

        int discharged = ElectricItemManager.discharge(chargeStack, missing, true);
        if (discharged > 0) {
            inventory.setStackInSlot(CHARGE_SLOT, chargeStack);
            energyStored = Math.min(getMaxEnergyStored(), energyStored + discharged);
            setChanged();
            return;
        }

        if (energyStored > getMaxEnergyStored() - ENERGY_PER_REDSTONE_CHARGE) {
            return;
        }

        chargeStack.shrink(1);
        inventory.setStackInSlot(CHARGE_SLOT, chargeStack);
        energyStored = Math.min(getMaxEnergyStored(), energyStored + ENERGY_PER_REDSTONE_CHARGE);
        setChanged();
    }

    private int getUpgradeCount(UpgradeType type) {
        int upgrades = 0;
        for (int slot = UPGRADE_START; slot <= UPGRADE_END; slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (getUpgradeType(stack) == type) {
                upgrades += stack.getCount();
            }
        }
        return upgrades;
    }

    private int getMaxProgress() {
        int upgrades = Math.min(4, getUpgradeCount(UpgradeType.OVERCLOCKER));
        double scaledProgress = BASE_MAX_PROGRESS * Math.pow(0.7D, upgrades);
        return Math.max(20, Mth.ceil(scaledProgress));
    }

    private int getEnergyPerTick() {
        int upgrades = Math.min(4, getUpgradeCount(UpgradeType.OVERCLOCKER));
        return Math.max(BASE_ENERGY_PER_TICK, Mth.ceil(BASE_ENERGY_PER_TICK * Math.pow(1.6D, upgrades)));
    }

    private UpgradeType getUpgradeType(ItemStack stack) {
        if (!(stack.getItem() instanceof MachineUpgradeItem upgradeItem)) {
            return null;
        }
        return upgradeItem.getType();
    }

    private boolean canWorkWithRedstone() {
        if (level == null) {
            return true;
        }
        if (getUpgradeCount(UpgradeType.REDSTONE_INVERTER) <= 0) {
            return true;
        }
        return level.hasNeighborSignal(worldPosition);
    }

    private void loadInventory(CompoundTag inventoryTag, HolderLookup.Provider registries) {
        ItemStackHandler loadedInventory = new ItemStackHandler(1);
        loadedInventory.deserializeNBT(registries, inventoryTag);

        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            inventory.setStackInSlot(slot, ItemStack.EMPTY);
        }

        int copySlots = Math.min(inventory.getSlots(), loadedInventory.getSlots());
        for (int slot = 0; slot < copySlots; slot++) {
            inventory.setStackInSlot(slot, loadedInventory.getStackInSlot(slot).copy());
        }
    }
}
