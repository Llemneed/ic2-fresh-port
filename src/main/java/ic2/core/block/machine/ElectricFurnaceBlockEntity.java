package ic2.core.block.machine;

import ic2.core.block.entity.AbstractProcessingMachineBlockEntity;
import ic2.core.energy.EnergyConsumer;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Blocks;
import ic2.core.init.IC2Sounds;
import ic2.core.item.upgrade.MachineUpgradeItem.UpgradeType;
import ic2.core.menu.ElectricFurnaceMenu;
import ic2.core.sound.MachineSoundHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class ElectricFurnaceBlockEntity extends AbstractProcessingMachineBlockEntity implements MenuProvider, EnergyConsumer {
    private static final int SLOT_COUNT = 7;
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int UPGRADE_START = 2;
    private static final int UPGRADE_END = 5;
    private static final int CHARGE_SLOT = 6;
    private static final int BASE_MAX_PROGRESS = 78;
    private static final int BASE_ENERGY_PER_TICK = 5;
    private static final int MAX_ENERGY = 2000;
    private static final int ENERGY_PER_REDSTONE_CHARGE = 200;
    private static final int ENERGY_STORAGE_PER_UPGRADE = 10000;
    private static final int[] INPUT_TIERS = {32, 128, 512, 2048, 8192};

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> getOperationMaxProgress();
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

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(
                IC2BlockEntities.ELECTRIC_FURNACE.get(),
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, ElectricFurnaceBlockEntity blockEntity) {
        blockEntity.tickProcessing(level, pos, state);
    }

    public ContainerData getData() {
        return data;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ic2.electric_furnace");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ElectricFurnaceMenu(containerId, playerInventory, this, data);
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

    public boolean canSmelt(ItemStack input) {
        return !getProcessingOperation(input).isEmpty();
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

    @Override
    protected ProcessingOperation getProcessingOperation(ItemStack input) {
        RecipeHolder<SmeltingRecipe> recipe = getRecipe(input);
        if (recipe == null) {
            return ProcessingOperation.empty();
        }

        ItemStack result = recipe.value().assemble(new SingleRecipeInput(input), level.registryAccess()).copy();
        return new ProcessingOperation(result, 1, recipe.value().getExperience());
    }

    @Override
    protected void onProcessingCompleted(ItemStack input, ProcessingOperation operation) {
        pendingExperience += operation.experience();
    }

    @Override
    protected int getOperationMaxProgress() {
        return scaledProgress(BASE_MAX_PROGRESS, 20);
    }

    @Override
    protected int getOperationEnergyPerTick() {
        return scaledEnergyPerTick(BASE_ENERGY_PER_TICK);
    }

    @Override
    protected void playProcessingSound(Level level, BlockPos pos) {
        MachineSoundHelper.playPeriodic(level, pos, IC2Sounds.ELECTRIC_FURNACE_OPERATING.get());
    }

    @Override
    protected void updateActiveState(Level level, BlockPos pos, BlockState state, boolean active) {
        if (state.getBlock() instanceof ElectricFurnaceBlock && state.getValue(ElectricFurnaceBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(ElectricFurnaceBlock.ACTIVE, active), Block.UPDATE_CLIENTS);
        }
    }

    private RecipeHolder<SmeltingRecipe> getRecipe(ItemStack input) {
        if (level == null || input.isEmpty()) {
            return null;
        }

        return level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), level)
                .orElse(null);
    }

}
