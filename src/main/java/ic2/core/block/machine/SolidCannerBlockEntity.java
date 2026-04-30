package ic2.core.block.machine;

import ic2.core.block.entity.AbstractProcessingMachineBlockEntity;
import ic2.core.energy.EnergyConsumer;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Items;
import ic2.core.menu.SolidCannerMenu;
import ic2.core.recipe.DualStackRecipeInput;
import ic2.core.recipe.IC2RecipeTypes;
import ic2.core.recipe.SolidCannerRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class SolidCannerBlockEntity extends AbstractProcessingMachineBlockEntity implements MenuProvider, EnergyConsumer {
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
    private int pendingCanInputCount = 1;

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

    public SolidCannerBlockEntity(BlockPos pos, BlockState blockState) {
        super(
                IC2BlockEntities.SOLID_CANNER.get(),
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, SolidCannerBlockEntity blockEntity) {
        blockEntity.tickProcessing(level, pos, state);
    }

    public ContainerData getData() {
        return data;
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
        saveInventory(tag, registries);
        tag.putInt("progress", progress);
        tag.putInt("energy", energyStored);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            loadInventory(tag.getCompound("inventory"), registries);
        }
        progress = tag.getInt("progress");
        energyStored = tag.getInt("energy");
    }

    public boolean hasRecipe(ItemStack input, ItemStack canInput) {
        return !getCanningOperation(input, canInput).isEmpty();
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

    @Override
    protected ProcessingOperation getProcessingOperation(ItemStack input) {
        return getCanningOperation(input, inventory.getStackInSlot(CAN_SLOT));
    }

    @Override
    protected void completeProcessing(ItemStack input, ProcessingOperation operation) {
        consumeInputsForOperation(operation);
        inventory.extractItem(CAN_SLOT, pendingCanInputCount, false);
        insertOperationResult(operation);
        pendingCanInputCount = 1;
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
    protected void updateActiveState(Level level, BlockPos pos, BlockState state, boolean active) {
        if (state.getBlock() instanceof SolidCannerBlock && state.getValue(SolidCannerBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(SolidCannerBlock.ACTIVE, active), Block.UPDATE_CLIENTS);
        }
    }

    private ProcessingOperation getCanningOperation(ItemStack input, ItemStack canInput) {
        RecipeHolder<SolidCannerRecipe> recipe = getDataDrivenRecipe(input, canInput);
        if (recipe != null) {
            pendingCanInputCount = Math.max(1, recipe.value().canCount());
            ItemStack result = recipe.value().getResultItem(level.registryAccess()).copy();
            return new ProcessingOperation(result, recipe.value().inputCount(), 0.0F);
        }

        // Legacy fallback is kept as a compatibility safety net if data-driven recipes are removed or overridden.
        if (!isCannableFood(input) || !isTinCan(canInput)) {
            pendingCanInputCount = 1;
            return ProcessingOperation.empty();
        }
        pendingCanInputCount = 1;
        return new ProcessingOperation(new ItemStack(IC2Items.FILLED_TIN_CAN.get()), 1, 0.0F);
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

    private RecipeHolder<SolidCannerRecipe> getDataDrivenRecipe(ItemStack input, ItemStack canInput) {
        if (level == null || input.isEmpty() || canInput.isEmpty()) {
            return null;
        }

        return level.getRecipeManager()
                .getRecipeFor(IC2RecipeTypes.SOLID_CANNING.get(), new DualStackRecipeInput(input, canInput), level)
                .orElse(null);
    }
}
