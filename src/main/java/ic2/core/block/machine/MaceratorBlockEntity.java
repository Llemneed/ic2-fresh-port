package ic2.core.block.machine;

import ic2.core.energy.EnergyConsumer;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Blocks;
import ic2.core.init.IC2Items;
import ic2.core.init.IC2Sounds;
import ic2.core.item.electric.ElectricItemManager;
import ic2.core.item.upgrade.MachineUpgradeItem;
import ic2.core.item.upgrade.MachineUpgradeItem.UpgradeType;
import ic2.core.menu.MaceratorMenu;
import ic2.core.sound.MachineSoundHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class MaceratorBlockEntity extends BlockEntity implements MenuProvider, EnergyConsumer {
    private static final int SLOT_COUNT = 7;
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int UPGRADE_START = 2;
    private static final int UPGRADE_END = 5;
    private static final int CHARGE_SLOT = 6;
    private static final int BASE_MAX_PROGRESS = 100;
    private static final int BASE_ENERGY_PER_TICK = 4;
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
    private float pendingExperience;

    public MaceratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.MACERATOR.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MaceratorBlockEntity blockEntity) {
        blockEntity.consumeChargeItem();

        ItemStack input = blockEntity.inventory.getStackInSlot(INPUT_SLOT);
        ItemStack result = blockEntity.getMaceratorResult(input);
        int energyPerTick = blockEntity.getEnergyPerTick();
        int maxProgress = blockEntity.getMaxProgress();
        boolean canProcess = blockEntity.canWorkWithRedstone()
                && !result.isEmpty()
                && blockEntity.canOutput(result)
                && blockEntity.energyStored >= energyPerTick;

        if (canProcess) {
            blockEntity.energyStored -= energyPerTick;
            blockEntity.progress++;
            MachineSoundHelper.playLoop(level, pos, IC2Sounds.MACERATOR_OPERATING.get());

            if (blockEntity.progress >= maxProgress) {
                blockEntity.progress = 0;
                blockEntity.process(input.copy(), result.copy());
            }
        } else if (blockEntity.progress != 0) {
            blockEntity.progress = 0;
        }

        boolean active = canProcess;
        if (state.getBlock() instanceof MaceratorBlock && state.getValue(MaceratorBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(MaceratorBlock.ACTIVE, active), Block.UPDATE_CLIENTS);
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
                ItemEntity entity = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, stack.copy());
                level.addFreshEntity(entity);
            }
        }
    }

    public Component getDisplayName() {
        return Component.translatable("block.ic2.macerator");
    }

    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MaceratorMenu(containerId, playerInventory, this, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("progress", progress);
        tag.putInt("energy", energyStored);
        tag.putFloat("pendingExperience", pendingExperience);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadInventory(tag.getCompound("inventory"), registries);
        progress = tag.getInt("progress");
        energyStored = tag.getInt("energy");
        pendingExperience = tag.getFloat("pendingExperience");
    }

    public int getEnergyStored() {
        return energyStored;
    }

    public int getMaxEnergyStored() {
        return MAX_ENERGY + getUpgradeCount(UpgradeType.ENERGY_STORAGE) * ENERGY_STORAGE_PER_UPGRADE;
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

    public boolean hasRecipe(ItemStack input) {
        return !getMaceratorResult(input).isEmpty();
    }

    public boolean isUpgrade(ItemStack stack) {
        return getUpgradeType(stack) != null;
    }

    public boolean isChargeItem(ItemStack stack) {
        return stack.is(Items.REDSTONE) || ElectricItemManager.canProvideEnergy(stack);
    }

    public void awardExperience(Player player) {
        if (!(level instanceof ServerLevel serverLevel) || pendingExperience <= 0.0F) {
            return;
        }

        int experience = Mth.floor(pendingExperience);
        if (experience < pendingExperience && Math.random() < pendingExperience - experience) {
            experience++;
        }

        pendingExperience = 0.0F;
        setChanged();

        if (experience > 0) {
            ExperienceOrb.award(serverLevel, player.position(), experience);
        }
    }

    private void process(ItemStack input, ItemStack result) {
        inventory.extractItem(INPUT_SLOT, 1, false);
        ItemStack output = inventory.getStackInSlot(OUTPUT_SLOT);
        pendingExperience += getExperienceForInput(input);

        if (output.isEmpty()) {
            inventory.setStackInSlot(OUTPUT_SLOT, result);
        } else {
            output.grow(result.getCount());
            inventory.setStackInSlot(OUTPUT_SLOT, output);
        }
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

    private ItemStack getMaceratorResult(ItemStack input) {
        if (input.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (input.is(IC2Blocks.LEAD_ORE.asItem())) {
            return new ItemStack(IC2Items.LEAD_DUST.get(), 2);
        }

        if (input.is(IC2Blocks.TIN_ORE.asItem())) {
            return new ItemStack(IC2Items.TIN_DUST.get(), 2);
        }

        if (input.is(Blocks.COPPER_ORE.asItem()) || input.is(Blocks.DEEPSLATE_COPPER_ORE.asItem())) {
            return new ItemStack(IC2Items.COPPER_DUST.get(), 2);
        }

        if (input.is(Blocks.IRON_ORE.asItem()) || input.is(Blocks.DEEPSLATE_IRON_ORE.asItem())) {
            return new ItemStack(IC2Items.IRON_DUST.get(), 2);
        }

        if (input.is(Blocks.GOLD_ORE.asItem()) || input.is(Blocks.DEEPSLATE_GOLD_ORE.asItem())) {
            return new ItemStack(IC2Items.GOLD_DUST.get(), 2);
        }

        if (input.is(Items.RAW_COPPER)) {
            return new ItemStack(IC2Items.COPPER_DUST.get(), 2);
        }

        if (input.is(Items.RAW_IRON)) {
            return new ItemStack(IC2Items.IRON_DUST.get(), 2);
        }

        if (input.is(Items.RAW_GOLD)) {
            return new ItemStack(IC2Items.GOLD_DUST.get(), 2);
        }

        if (input.is(Blocks.COBBLESTONE.asItem())) {
            return new ItemStack(Blocks.SAND);
        }

        if (input.is(Blocks.GRAVEL.asItem())) {
            return new ItemStack(Items.FLINT);
        }

        if (input.is(IC2Items.LEAD_INGOT.get())) {
            return new ItemStack(IC2Items.LEAD_DUST.get());
        }

        if (input.is(IC2Items.TIN_INGOT.get())) {
            return new ItemStack(IC2Items.TIN_DUST.get());
        }

        if (input.is(Blocks.COPPER_BLOCK.asItem())) {
            return new ItemStack(IC2Items.COPPER_DUST.get(), 9);
        }

        if (input.is(Items.COPPER_INGOT)) {
            return new ItemStack(IC2Items.COPPER_DUST.get());
        }

        if (input.is(Items.IRON_INGOT)) {
            return new ItemStack(IC2Items.IRON_DUST.get());
        }

        if (input.is(Items.GOLD_INGOT)) {
            return new ItemStack(IC2Items.GOLD_DUST.get());
        }

        if (input.is(Blocks.IRON_BLOCK.asItem())) {
            return new ItemStack(IC2Items.IRON_DUST.get(), 9);
        }

        if (input.is(Blocks.GOLD_BLOCK.asItem())) {
            return new ItemStack(IC2Items.GOLD_DUST.get(), 9);
        }

        return ItemStack.EMPTY;
    }

    private float getExperienceForInput(ItemStack input) {
        if (input.is(IC2Blocks.LEAD_ORE.asItem()) || input.is(IC2Blocks.TIN_ORE.asItem())
                || input.is(Blocks.COPPER_ORE.asItem()) || input.is(Blocks.DEEPSLATE_COPPER_ORE.asItem())
                || input.is(Blocks.IRON_ORE.asItem()) || input.is(Blocks.DEEPSLATE_IRON_ORE.asItem())
                || input.is(Blocks.GOLD_ORE.asItem()) || input.is(Blocks.DEEPSLATE_GOLD_ORE.asItem())
                || input.is(Items.RAW_COPPER) || input.is(Items.RAW_IRON) || input.is(Items.RAW_GOLD)) {
            return 0.2F;
        }

        if (input.is(IC2Items.LEAD_INGOT.get()) || input.is(IC2Items.TIN_INGOT.get())
                || input.is(Items.COPPER_INGOT) || input.is(Items.IRON_INGOT) || input.is(Items.GOLD_INGOT)
                || input.is(Blocks.COPPER_BLOCK.asItem()) || input.is(Blocks.IRON_BLOCK.asItem()) || input.is(Blocks.GOLD_BLOCK.asItem())) {
            return 0.1F;
        }

        return 0.0F;
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
        return Math.max(24, Mth.ceil(scaledProgress));
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
