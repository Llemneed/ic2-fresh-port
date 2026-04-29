package ic2.core.block.machine;

import ic2.core.block.entity.AbstractProcessingMachineBlockEntity;
import ic2.core.energy.EnergyConsumer;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Blocks;
import ic2.core.init.IC2Items;
import ic2.core.init.IC2Sounds;
import ic2.core.item.upgrade.MachineUpgradeItem.UpgradeType;
import ic2.core.menu.MaceratorMenu;
import ic2.core.recipe.MaceratorRecipe;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;

public final class MaceratorBlockEntity extends AbstractProcessingMachineBlockEntity implements MenuProvider, EnergyConsumer {
    private static final int SLOT_COUNT = 7;
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int UPGRADE_START = 2;
    private static final int UPGRADE_END = 5;
    private static final int CHARGE_SLOT = 6;
    private static final int BASE_MAX_PROGRESS = 100;
    private static final int BASE_ENERGY_PER_TICK = 4;
    private static final int ENERGY_PER_REDSTONE_CHARGE = 200;
    private static final int ENERGY_STORAGE_PER_UPGRADE = 10000;
    private static final int[] INPUT_TIERS = {32, 128, 512, 2048, 8192};
    private static final int MAX_ENERGY = 1000;

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

    private float pendingExperience;

    public MaceratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(
                IC2BlockEntities.MACERATOR.get(),
                pos,
                blockState,
                SLOT_COUNT,
                INPUT_SLOT,
                OUTPUT_SLOT,
                UPGRADE_START,
                UPGRADE_END,
                CHARGE_SLOT,
                MAX_ENERGY,
                ENERGY_PER_REDSTONE_CHARGE,
                ENERGY_STORAGE_PER_UPGRADE,
                INPUT_TIERS
        );
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
            MachineSoundHelper.playPeriodic(level, pos, IC2Sounds.MACERATOR_OPERATING.get());

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

    public ContainerData getData() {
        return data;
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
        saveInventory(tag, registries);
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

    public boolean hasRecipe(ItemStack input) {
        return !getMaceratorResult(input).isEmpty();
    }

    public boolean isUpgrade(ItemStack stack) {
        return getUpgradeType(stack) != null;
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

        RecipeHolder<MaceratorRecipe> recipe = getDataDrivenRecipe(input);
        if (recipe != null) {
            return recipe.value().assemble(new SingleRecipeInput(input), level.registryAccess()).copy();
        }

        // TODO: migrate remaining hardcoded recipes to data-driven ic2:macerating JSONs.
        return getLegacyMaceratorResult(input);
    }

    private ItemStack getLegacyMaceratorResult(ItemStack input) {
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
        RecipeHolder<MaceratorRecipe> recipe = getDataDrivenRecipe(input);
        if (recipe != null) {
            return recipe.value().experience();
        }

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

    private int getMaxProgress() {
        return scaledProgress(BASE_MAX_PROGRESS, 24);
    }

    private int getEnergyPerTick() {
        return scaledEnergyPerTick(BASE_ENERGY_PER_TICK);
    }

    private RecipeHolder<MaceratorRecipe> getDataDrivenRecipe(ItemStack input) {
        if (level == null || input.isEmpty()) {
            return null;
        }

        return level.getRecipeManager()
                .getRecipeFor(ic2.core.recipe.IC2RecipeTypes.MACERATING.get(), new SingleRecipeInput(input), level)
                .orElse(null);
    }
}
