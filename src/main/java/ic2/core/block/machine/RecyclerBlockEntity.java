package ic2.core.block.machine;

import ic2.core.block.entity.AbstractProcessingMachineBlockEntity;
import ic2.core.energy.EnergyConsumer;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Blocks;
import ic2.core.init.IC2Items;
import ic2.core.init.IC2Sounds;
import ic2.core.item.upgrade.MachineUpgradeItem.UpgradeType;
import ic2.core.menu.RecyclerMenu;
import ic2.core.sound.MachineSoundHelper;
import ic2.core.recipe.IC2RecipeTypes;
import ic2.core.recipe.RecyclerRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class RecyclerBlockEntity extends AbstractProcessingMachineBlockEntity implements MenuProvider, EnergyConsumer {
    private static final int SLOT_COUNT = 7;
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int UPGRADE_START = 2;
    private static final int UPGRADE_END = 5;
    private static final int CHARGE_SLOT = 6;
    private static final int BASE_MAX_PROGRESS = 60;
    private static final int BASE_ENERGY_PER_TICK = 2;
    private static final int MAX_ENERGY = 1000;
    private static final int ENERGY_PER_REDSTONE_CHARGE = 200;
    private static final int ENERGY_STORAGE_PER_UPGRADE = 10000;
    private static final int[] INPUT_TIERS = {32, 128, 512, 2048, 8192};
    private static final float BASE_SCRAP_CHANCE = 0.125F;
    private float pendingScrapChance = BASE_SCRAP_CHANCE;

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

    public RecyclerBlockEntity(BlockPos pos, BlockState blockState) {
        super(
                IC2BlockEntities.RECYCLER.get(),
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, RecyclerBlockEntity blockEntity) {
        blockEntity.tickProcessing(level, pos, state);
    }

    public ContainerData getData() {
        return data;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ic2.recycler");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new RecyclerMenu(containerId, playerInventory, this, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveInventory(tag, registries);
        tag.putInt("progress", progress);
        tag.putInt("energy", energyStored);
        tag.putFloat("pendingScrapChance", pendingScrapChance);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            loadInventory(tag.getCompound("inventory"), registries);
        }
        progress = tag.getInt("progress");
        energyStored = tag.getInt("energy");
        pendingScrapChance = tag.contains("pendingScrapChance") ? tag.getFloat("pendingScrapChance") : BASE_SCRAP_CHANCE;
    }

    public boolean hasRecipe(ItemStack input) {
        return !getProcessingOperation(input).isEmpty();
    }

    public boolean isUpgrade(ItemStack stack) {
        return getUpgradeType(stack) != null;
    }

    @Override
    protected ProcessingOperation getProcessingOperation(ItemStack input) {
        if (level != null) {
            RecipeHolder<RecyclerRecipe> recipeHolder = level.getRecipeManager()
                    .getRecipeFor(IC2RecipeTypes.RECYCLING.get(), new SingleRecipeInput(input), level)
                    .orElse(null);
            if (recipeHolder != null) {
                RecyclerRecipe recipe = recipeHolder.value();
                ItemStack result = recipe.getResultItem(level.registryAccess());
                if (result.isEmpty() || !canOutput(result)) {
                    return ProcessingOperation.empty();
                }
                pendingScrapChance = recipe.chance();
                return new ProcessingOperation(result, 1, 0.0F);
            }
        }

        // TODO(milestone-3): Remove generic recycler fallback once recycling JSON coverage is broad enough.
        if (!canRecycle(input) || !canOutput(new ItemStack(IC2Items.SCRAP.get()))) {
            pendingScrapChance = BASE_SCRAP_CHANCE;
            return ProcessingOperation.empty();
        }
        pendingScrapChance = BASE_SCRAP_CHANCE;
        return new ProcessingOperation(new ItemStack(IC2Items.SCRAP.get()), 1, 0.0F);
    }

    @Override
    protected void completeProcessing(ItemStack input, ProcessingOperation operation) {
        consumeInputsForOperation(operation);
        RandomSource random = level != null ? level.random : RandomSource.create();
        float chance = pendingScrapChance + getUpgradeCount(UpgradeType.OVERCLOCKER) * 0.01F;
        if (random.nextFloat() > chance) {
            return;
        }

        insertOperationResult(operation);
    }

    @Override
    protected int getOperationMaxProgress() {
        return scaledProgress(BASE_MAX_PROGRESS, 12);
    }

    @Override
    protected int getOperationEnergyPerTick() {
        return scaledEnergyPerTick(BASE_ENERGY_PER_TICK);
    }

    @Override
    protected void playProcessingSound(Level level, BlockPos pos) {
        MachineSoundHelper.playPeriodic(level, pos, IC2Sounds.RECYCLER_OPERATING.get());
    }

    @Override
    protected void updateActiveState(Level level, BlockPos pos, BlockState state, boolean active) {
        if (state.getBlock() instanceof RecyclerBlock && state.getValue(RecyclerBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(RecyclerBlock.ACTIVE, active), Block.UPDATE_CLIENTS);
        }
    }

    private boolean canRecycle(ItemStack input) {
        if (input.isEmpty()) {
            return false;
        }
        if (input.is(IC2Items.SCRAP.get()) || input.is(IC2Items.SCRAP_BOX.get())) {
            return false;
        }
        if (isUpgrade(input) || isChargeItem(input)) {
            return false;
        }
        Item item = input.getItem();
        if (item == Blocks.BEDROCK.asItem() || item == Items.BARRIER || item == Items.STRUCTURE_BLOCK || item == Items.STRUCTURE_VOID) {
            return false;
        }
        if (item instanceof BlockItem blockItem && blockItem.getBlock() == IC2Blocks.RECYCLER.get()) {
            return false;
        }
        return true;
    }

    private boolean canOutput(ItemStack result) {
        ItemStack output = inventory.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(output, result)) {
            return false;
        }
        return output.getCount() < output.getMaxStackSize();
    }
}
