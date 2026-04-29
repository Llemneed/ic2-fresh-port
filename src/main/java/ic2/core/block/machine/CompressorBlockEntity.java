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
import net.neoforged.neoforge.items.ItemStackHandler;

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
        blockEntity.consumeChargeItem();

        ItemStack input = blockEntity.inventory.getStackInSlot(INPUT_SLOT);
        ItemStack result = blockEntity.getCompressorResult(input);
        int energyPerTick = blockEntity.getEnergyPerTick();
        int maxProgress = blockEntity.getMaxProgress();
        boolean canProcess = blockEntity.canWorkWithRedstone()
                && !result.isEmpty()
                && blockEntity.canOutput(result)
                && blockEntity.energyStored >= energyPerTick;

        if (canProcess) {
            blockEntity.energyStored -= energyPerTick;
            blockEntity.progress++;
            MachineSoundHelper.playPeriodic(level, pos, IC2Sounds.COMPRESSOR_OPERATING.get());

            if (blockEntity.progress >= maxProgress) {
                blockEntity.progress = 0;
                blockEntity.process(input.copy(), result.copy());
            }
        } else if (blockEntity.progress != 0) {
            blockEntity.progress = 0;
        }

        boolean active = canProcess;
        if (state.getBlock() instanceof CompressorBlock && state.getValue(CompressorBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(CompressorBlock.ACTIVE, active), Block.UPDATE_CLIENTS);
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
        loadInventory(tag.getCompound("inventory"), registries);
        progress = tag.getInt("progress");
        energyStored = tag.getInt("energy");
    }

    public boolean hasRecipe(ItemStack input) {
        return !getCompressorResult(input).isEmpty();
    }

    public boolean isUpgrade(ItemStack stack) {
        return getUpgradeType(stack) != null;
    }

    private void process(ItemStack input, ItemStack result) {
        inventory.extractItem(INPUT_SLOT, getIngredientCount(input), false);
        ItemStack output = inventory.getStackInSlot(OUTPUT_SLOT);

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

    private ItemStack getCompressorResult(ItemStack input) {
        if (input.isEmpty()) {
            return ItemStack.EMPTY;
        }

        RecipeHolder<CompressorRecipe> recipe = getDataDrivenRecipe(input);
        if (recipe != null) {
            return recipe.value().assemble(new SingleRecipeInput(input), level.registryAccess()).copy();
        }

        // TODO: migrate any remaining hardcoded compressor recipes to ic2:compressing JSONs.
        return getLegacyCompressorResult(input);
    }

    private int getIngredientCount(ItemStack input) {
        RecipeHolder<CompressorRecipe> recipe = getDataDrivenRecipe(input);
        if (recipe != null) {
            return recipe.value().ingredientCount();
        }

        LegacyCompressorRecipe legacyRecipe = getLegacyRecipe(input);
        return legacyRecipe != null ? legacyRecipe.inputCount : 1;
    }

    private LegacyCompressorRecipe getLegacyRecipe(ItemStack input) {
        for (LegacyCompressorRecipe recipe : RECIPES) {
            if (input.getCount() >= recipe.inputCount && recipe.matches.test(input)) {
                return recipe;
            }
        }
        return null;
    }

    private ItemStack getLegacyCompressorResult(ItemStack input) {
        LegacyCompressorRecipe recipe = getLegacyRecipe(input);
        return recipe != null ? recipe.output.copy() : ItemStack.EMPTY;
    }

    private RecipeHolder<CompressorRecipe> getDataDrivenRecipe(ItemStack input) {
        if (level == null || input.isEmpty()) {
            return null;
        }

        return level.getRecipeManager()
                .getRecipeFor(IC2RecipeTypes.COMPRESSING.get(), new SingleRecipeInput(input), level)
                .orElse(null);
    }

    private int getMaxProgress() {
        return scaledProgress(BASE_MAX_PROGRESS, 30);
    }

    private int getEnergyPerTick() {
        return scaledEnergyPerTick(BASE_ENERGY_PER_TICK);
    }
}
