package ic2.core.block.machine;

import ic2.core.block.entity.AbstractProcessingMachineBlockEntity;
import ic2.core.energy.EnergyConsumer;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Items;
import ic2.core.menu.MetalFormerMenu;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class MetalFormerBlockEntity extends AbstractProcessingMachineBlockEntity implements MenuProvider, EnergyConsumer {
    private static final int SLOT_COUNT = 7;
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int UPGRADE_START = 2;
    private static final int UPGRADE_END = 5;
    private static final int CHARGE_SLOT = 6;
    private static final int BASE_MAX_PROGRESS = 120;
    private static final int BASE_ENERGY_PER_TICK = 10;
    private static final int MAX_ENERGY = 2000;
    private static final int ENERGY_PER_REDSTONE_CHARGE = 200;
    private static final int ENERGY_STORAGE_PER_UPGRADE = 10000;
    private static final int[] INPUT_TIERS = {32, 128, 512, 2048, 8192};

    public enum Mode {
        EXTRUDING,
        ROLLING,
        CUTTING
    }

    private record MetalFormerRecipe(int inputCount, Predicate<ItemStack> matches, ItemStack output) {
    }

    private static final List<MetalFormerRecipe> EXTRUDING_RECIPES = List.of(
            new MetalFormerRecipe(1, stack -> stack.is(IC2Items.TIN_INGOT.get()), new ItemStack(IC2Items.TIN_CAN.get()))
    );
    private static final List<MetalFormerRecipe> ROLLING_RECIPES = List.of(
            new MetalFormerRecipe(1, stack -> stack.is(Items.IRON_INGOT), new ItemStack(IC2Items.IRON_PLATE.get())),
            new MetalFormerRecipe(1, stack -> stack.is(Items.GOLD_INGOT), new ItemStack(IC2Items.GOLD_PLATE.get())),
            new MetalFormerRecipe(1, stack -> stack.is(Items.COPPER_INGOT), new ItemStack(IC2Items.COPPER_PLATE.get())),
            new MetalFormerRecipe(1, stack -> stack.is(IC2Items.TIN_INGOT.get()), new ItemStack(IC2Items.TIN_PLATE.get())),
            new MetalFormerRecipe(1, stack -> stack.is(IC2Items.LEAD_INGOT.get()), new ItemStack(IC2Items.LEAD_PLATE.get())),
            new MetalFormerRecipe(1, stack -> stack.is(IC2Items.BRONZE_INGOT.get()), new ItemStack(IC2Items.BRONZE_PLATE.get()))
    );
    private static final List<MetalFormerRecipe> CUTTING_RECIPES = List.of(
            new MetalFormerRecipe(1, stack -> stack.is(Blocks.IRON_BLOCK.asItem()), new ItemStack(IC2Items.IRON_PLATE.get(), 9)),
            new MetalFormerRecipe(1, stack -> stack.is(Blocks.GOLD_BLOCK.asItem()), new ItemStack(IC2Items.GOLD_PLATE.get(), 9)),
            new MetalFormerRecipe(1, stack -> stack.is(Blocks.COPPER_BLOCK.asItem()), new ItemStack(IC2Items.COPPER_PLATE.get(), 9))
    );

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> getMaxProgress();
                case 2 -> energyStored;
                case 3 -> getMaxEnergyStored();
                case 4 -> mode.ordinal();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 2 -> energyStored = value;
                case 4 -> mode = Mode.values()[Mth.clamp(value, 0, Mode.values().length - 1)];
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    private Mode mode = Mode.EXTRUDING;

    public MetalFormerBlockEntity(BlockPos pos, BlockState blockState) {
        super(
                IC2BlockEntities.METAL_FORMER.get(),
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, MetalFormerBlockEntity blockEntity) {
        blockEntity.consumeChargeItem();

        MetalFormerRecipe recipe = blockEntity.getRecipe(blockEntity.inventory.getStackInSlot(INPUT_SLOT));
        int energyPerTick = blockEntity.getEnergyPerTick();
        int maxProgress = blockEntity.getMaxProgress();
        boolean canProcess = blockEntity.canWorkWithRedstone()
                && recipe != null
                && blockEntity.canOutput(recipe.output)
                && blockEntity.energyStored >= energyPerTick;

        if (canProcess) {
            blockEntity.energyStored -= energyPerTick;
            blockEntity.progress++;

            if (blockEntity.progress >= maxProgress) {
                blockEntity.progress = 0;
                blockEntity.process(recipe);
            }
        } else if (blockEntity.progress != 0) {
            blockEntity.progress = 0;
        }

        boolean active = canProcess;
        if (state.getBlock() instanceof MetalFormerBlock && state.getValue(MetalFormerBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(MetalFormerBlock.ACTIVE, active), Block.UPDATE_CLIENTS);
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

    public void cycleMode() {
        mode = Mode.values()[(mode.ordinal() + 1) % Mode.values().length];
        progress = 0;
        setChanged();
    }

    public int getModeIndex() {
        return mode.ordinal();
    }

    public Component getModeMessage() {
        return Component.literal("Metal Former: " + switch (mode) {
            case EXTRUDING -> "Extruding";
            case ROLLING -> "Rolling";
            case CUTTING -> "Cutting";
        });
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ic2.metal_former");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MetalFormerMenu(containerId, playerInventory, this, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveInventory(tag, registries);
        tag.putInt("progress", progress);
        tag.putInt("energy", energyStored);
        tag.putInt("mode", mode.ordinal());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadInventory(tag.getCompound("inventory"), registries);
        progress = tag.getInt("progress");
        energyStored = tag.getInt("energy");
        mode = Mode.values()[Mth.clamp(tag.getInt("mode"), 0, Mode.values().length - 1)];
    }

    public boolean hasRecipe(ItemStack input) {
        return getRecipe(input) != null;
    }

    public boolean isUpgrade(ItemStack stack) {
        return getUpgradeType(stack) != null;
    }

    private void process(MetalFormerRecipe recipe) {
        inventory.extractItem(INPUT_SLOT, recipe.inputCount, false);
        ItemStack output = inventory.getStackInSlot(OUTPUT_SLOT);
        ItemStack result = recipe.output.copy();

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

    private MetalFormerRecipe getRecipe(ItemStack input) {
        if (input.isEmpty()) {
            return null;
        }
        for (MetalFormerRecipe recipe : getRecipesForMode()) {
            if (input.getCount() >= recipe.inputCount && recipe.matches.test(input)) {
                return recipe;
            }
        }
        return null;
    }

    private List<MetalFormerRecipe> getRecipesForMode() {
        return switch (mode) {
            case EXTRUDING -> EXTRUDING_RECIPES;
            case ROLLING -> ROLLING_RECIPES;
            case CUTTING -> CUTTING_RECIPES;
        };
    }

    private int getMaxProgress() {
        return scaledProgress(BASE_MAX_PROGRESS, 30);
    }

    private int getEnergyPerTick() {
        return scaledEnergyPerTick(BASE_ENERGY_PER_TICK);
    }
}
