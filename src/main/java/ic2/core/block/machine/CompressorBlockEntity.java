package ic2.core.block.machine;

import ic2.core.block.entity.AbstractProcessingMachineBlockEntity;
import ic2.core.energy.EnergyConsumer;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Items;
import ic2.core.init.IC2Sounds;
import ic2.core.item.upgrade.MachineUpgradeItem.UpgradeType;
import ic2.core.menu.CompressorMenu;
import ic2.core.recipe.CompressorRecipe;
import ic2.core.recipe.IC2RecipeTypes;
import ic2.core.sound.MachineSoundHelper;
import java.util.List;
import java.util.function.Predicate;
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

public final class CompressorBlockEntity extends AbstractProcessingMachineBlockEntity implements MenuProvider, EnergyConsumer {
    private static final int SLOT_COUNT = 7;
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int UPGRADE_START = 2;
    private static final int UPGRADE_END = 5;
    private static final int CHARGE_SLOT = 6;
    private static final int BASE_MAX_PROGRESS = 120;
    private static final int BASE_ENERGY_PER_TICK = 2;
    private static final int MAX_ENERGY = 1000;
    private static final int ENERGY_PER_REDSTONE_CHARGE = 200;
    private static final int ENERGY_STORAGE_PER_UPGRADE = 10000;
    private static final int[] INPUT_TIERS = {32, 128, 512, 2048, 8192};

    private record LegacyCompressorRecipe(int inputCount, Predicate<ItemStack> matches, ItemStack output) {
    }

    private static final List<LegacyCompressorRecipe> RECIPES = List.of(
            new LegacyCompressorRecipe(4, stack -> stack.is(Items.CLAY_BALL), new ItemStack(Blocks.CLAY)),
            new LegacyCompressorRecipe(4, stack -> stack.is(Items.BRICK), new ItemStack(Blocks.BRICKS)),
            new LegacyCompressorRecipe(4, stack -> stack.is(Items.SNOWBALL), new ItemStack(Blocks.SNOW_BLOCK)),
            new LegacyCompressorRecipe(4, stack -> stack.is(Items.GLOWSTONE_DUST), new ItemStack(Blocks.GLOWSTONE)),
            new LegacyCompressorRecipe(9, stack -> stack.is(Items.REDSTONE), new ItemStack(Blocks.REDSTONE_BLOCK)),
            new LegacyCompressorRecipe(9, stack -> stack.is(Items.IRON_INGOT), new ItemStack(Blocks.IRON_BLOCK)),
            new LegacyCompressorRecipe(9, stack -> stack.is(Items.GOLD_INGOT), new ItemStack(Blocks.GOLD_BLOCK)),
            new LegacyCompressorRecipe(1, stack -> stack.is(IC2Items.COAL_BALL.get()), new ItemStack(Blocks.COAL_BLOCK)),
            new LegacyCompressorRecipe(1, stack -> stack.is(IC2Items.COAL_CHUNK.get()), new ItemStack(IC2Items.INDUSTRIAL_DIAMOND.get())),
            new LegacyCompressorRecipe(1, stack -> stack.is(IC2Items.CARBON_MESH.get()), new ItemStack(IC2Items.CARBON_PLATE.get()))
    );

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

    public CompressorBlockEntity(BlockPos pos, BlockState blockState) {
        super(
                IC2BlockEntities.COMPRESSOR.get(),
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, CompressorBlockEntity blockEntity) {
        blockEntity.tickProcessing(level, pos, state);
    }

    public ContainerData getData() {
        return data;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ic2.compressor");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CompressorMenu(containerId, playerInventory, this, data);
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

        RecipeHolder<CompressorRecipe> recipe = getDataDrivenRecipe(input);
        if (recipe != null) {
            ItemStack result = recipe.value().assemble(new SingleRecipeInput(input), level.registryAccess()).copy();
            return new ProcessingOperation(result, recipe.value().ingredientCount(), 0.0F);
        }

        // Legacy fallback is kept temporarily for compatibility while the data-driven layer settles.
        return getLegacyCompressorOperation(input);
    }

    private LegacyCompressorRecipe getLegacyRecipe(ItemStack input) {
        for (LegacyCompressorRecipe recipe : RECIPES) {
            if (input.getCount() >= recipe.inputCount && recipe.matches.test(input)) {
                return recipe;
            }
        }
        return null;
    }

    private ProcessingOperation getLegacyCompressorOperation(ItemStack input) {
        LegacyCompressorRecipe recipe = getLegacyRecipe(input);
        return recipe != null
                ? new ProcessingOperation(recipe.output.copy(), recipe.inputCount, 0.0F)
                : ProcessingOperation.empty();
    }

    private RecipeHolder<CompressorRecipe> getDataDrivenRecipe(ItemStack input) {
        if (level == null || input.isEmpty()) {
            return null;
        }

        return level.getRecipeManager()
                .getRecipeFor(IC2RecipeTypes.COMPRESSING.get(), new SingleRecipeInput(input), level)
                .orElse(null);
    }

    @Override
    protected int getOperationMaxProgress() {
        return scaledProgress(BASE_MAX_PROGRESS, 30);
    }

    @Override
    protected int getOperationEnergyPerTick() {
        return scaledEnergyPerTick(BASE_ENERGY_PER_TICK);
    }

    @Override
    protected void playProcessingSound(Level level, BlockPos pos) {
        MachineSoundHelper.playPeriodic(level, pos, IC2Sounds.COMPRESSOR_OPERATING.get());
    }

    @Override
    protected void updateActiveState(Level level, BlockPos pos, BlockState state, boolean active) {
        if (state.getBlock() instanceof CompressorBlock && state.getValue(CompressorBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(CompressorBlock.ACTIVE, active), Block.UPDATE_CLIENTS);
        }
    }
}
