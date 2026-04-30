package ic2.core.block.machine;

import ic2.core.block.entity.AbstractProcessingMachineBlockEntity;
import ic2.core.energy.EnergyConsumer;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Blocks;
import ic2.core.init.IC2Items;
import ic2.core.init.IC2Sounds;
import ic2.core.item.upgrade.MachineUpgradeItem.UpgradeType;
import ic2.core.menu.ExtractorMenu;
import ic2.core.recipe.ExtractorRecipe;
import ic2.core.recipe.IC2RecipeTypes;
import ic2.core.sound.MachineSoundHelper;
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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class ExtractorBlockEntity extends AbstractProcessingMachineBlockEntity implements MenuProvider, EnergyConsumer {
    private static final int SLOT_COUNT = 7;
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int UPGRADE_START = 2;
    private static final int UPGRADE_END = 5;
    private static final int CHARGE_SLOT = 6;
    private static final int BASE_MAX_PROGRESS = 80;
    private static final int BASE_ENERGY_PER_TICK = 3;
    private static final int MAX_ENERGY = 1000;
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

    public ExtractorBlockEntity(BlockPos pos, BlockState blockState) {
        super(
                IC2BlockEntities.EXTRACTOR.get(),
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, ExtractorBlockEntity blockEntity) {
        blockEntity.tickProcessing(level, pos, state);
    }

    public ContainerData getData() {
        return data;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ic2.extractor");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ExtractorMenu(containerId, playerInventory, this, data);
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

    public boolean hasRecipe(ItemStack input) {
        return !getProcessingOperation(input).isEmpty();
    }

    public boolean isUpgrade(ItemStack stack) {
        return getUpgradeType(stack) != null;
    }

    @Override
    protected ProcessingOperation getProcessingOperation(ItemStack input) {
        if (input.isEmpty()) {
            return ProcessingOperation.empty();
        }

        RecipeHolder<ExtractorRecipe> recipe = getDataDrivenRecipe(input);
        if (recipe != null) {
            ItemStack result = recipe.value().assemble(new SingleRecipeInput(input), level.registryAccess()).copy();
            return new ProcessingOperation(result, recipe.value().ingredientCount(), 0.0F);
        }

        // Legacy fallback is kept temporarily for compatibility while the data-driven layer settles.
        return getLegacyExtractorOperation(input);
    }

    private ProcessingOperation getLegacyExtractorOperation(ItemStack input) {
        if (input.is(IC2Items.STICKY_RESIN.get())) {
            return new ProcessingOperation(new ItemStack(IC2Items.RUBBER.get(), 3), 1, 0.0F);
        }

        if (input.is(IC2Blocks.RUBBER_SAPLING.asItem()) || input.is(IC2Blocks.RUBBER_WOOD.asItem())) {
            return new ProcessingOperation(new ItemStack(IC2Items.RUBBER.get()), 1, 0.0F);
        }

        if (input.is(IC2Items.FILLED_TIN_CAN.get())) {
            return new ProcessingOperation(new ItemStack(IC2Items.TIN_CAN.get()), 1, 0.0F);
        }

        if (input.is(Blocks.CLAY.asItem())) {
            return new ProcessingOperation(new ItemStack(Items.CLAY_BALL, 4), 1, 0.0F);
        }

        if (input.is(Blocks.BRICKS.asItem())) {
            return new ProcessingOperation(new ItemStack(Items.BRICK, 4), 1, 0.0F);
        }

        if (input.is(Blocks.SNOW_BLOCK.asItem())) {
            return new ProcessingOperation(new ItemStack(Items.SNOWBALL, 4), 1, 0.0F);
        }

        return ProcessingOperation.empty();
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
        MachineSoundHelper.playPeriodic(level, pos, IC2Sounds.EXTRACTOR_OPERATING.get());
    }

    @Override
    protected void updateActiveState(Level level, BlockPos pos, BlockState state, boolean active) {
        if (state.getBlock() instanceof ExtractorBlock && state.getValue(ExtractorBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(ExtractorBlock.ACTIVE, active), Block.UPDATE_CLIENTS);
        }
    }

    private RecipeHolder<ExtractorRecipe> getDataDrivenRecipe(ItemStack input) {
        if (level == null || input.isEmpty()) {
            return null;
        }

        return level.getRecipeManager()
                .getRecipeFor(IC2RecipeTypes.EXTRACTING.get(), new SingleRecipeInput(input), level)
                .orElse(null);
    }
}
