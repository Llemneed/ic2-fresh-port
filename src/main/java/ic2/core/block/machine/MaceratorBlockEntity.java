package ic2.core.block.machine;

import ic2.core.block.entity.AbstractProcessingMachineBlockEntity;
import ic2.core.energy.EnergyConsumer;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Blocks;
import ic2.core.init.IC2Items;
import ic2.core.init.IC2Sounds;
import ic2.core.item.upgrade.MachineUpgradeItem.UpgradeType;
import ic2.core.menu.MaceratorMenu;
import ic2.core.recipe.IC2RecipeTypes;
import ic2.core.recipe.MaceratorRecipe;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

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
        blockEntity.tickProcessing(level, pos, state);
    }

    public ContainerData getData() {
        return data;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ic2.macerator");
    }

    @Override
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
        if (input.isEmpty()) {
            return ProcessingOperation.empty();
        }

        RecipeHolder<MaceratorRecipe> recipe = getDataDrivenRecipe(input);
        if (recipe != null) {
            ItemStack result = recipe.value().assemble(new SingleRecipeInput(input), level.registryAccess()).copy();
            return new ProcessingOperation(result, 1, recipe.value().experience());
        }

        // TODO: migrate remaining hardcoded recipes to data-driven ic2:macerating JSONs.
        return getLegacyMaceratorOperation(input);
    }

    @Override
    protected void onProcessingCompleted(ItemStack input, ProcessingOperation operation) {
        pendingExperience += operation.experience();
    }

    @Override
    protected int getOperationMaxProgress() {
        return scaledProgress(BASE_MAX_PROGRESS, 24);
    }

    @Override
    protected int getOperationEnergyPerTick() {
        return scaledEnergyPerTick(BASE_ENERGY_PER_TICK);
    }

    @Override
    protected void playProcessingSound(Level level, BlockPos pos) {
        MachineSoundHelper.playPeriodic(level, pos, IC2Sounds.MACERATOR_OPERATING.get());
    }

    @Override
    protected void updateActiveState(Level level, BlockPos pos, BlockState state, boolean active) {
        if (state.getBlock() instanceof MaceratorBlock && state.getValue(MaceratorBlock.ACTIVE) != active) {
            level.setBlock(pos, state.setValue(MaceratorBlock.ACTIVE, active), Block.UPDATE_CLIENTS);
        }
    }

    private ProcessingOperation getLegacyMaceratorOperation(ItemStack input) {
        if (input.is(IC2Blocks.LEAD_ORE.asItem())) {
            return new ProcessingOperation(new ItemStack(IC2Items.LEAD_DUST.get(), 2), 1, 0.2F);
        }
        if (input.is(IC2Blocks.TIN_ORE.asItem())) {
            return new ProcessingOperation(new ItemStack(IC2Items.TIN_DUST.get(), 2), 1, 0.2F);
        }
        if (input.is(Blocks.COPPER_ORE.asItem()) || input.is(Blocks.DEEPSLATE_COPPER_ORE.asItem())) {
            return new ProcessingOperation(new ItemStack(IC2Items.COPPER_DUST.get(), 2), 1, 0.2F);
        }
        if (input.is(Blocks.IRON_ORE.asItem()) || input.is(Blocks.DEEPSLATE_IRON_ORE.asItem())) {
            return new ProcessingOperation(new ItemStack(IC2Items.IRON_DUST.get(), 2), 1, 0.2F);
        }
        if (input.is(Blocks.GOLD_ORE.asItem()) || input.is(Blocks.DEEPSLATE_GOLD_ORE.asItem())) {
            return new ProcessingOperation(new ItemStack(IC2Items.GOLD_DUST.get(), 2), 1, 0.2F);
        }
        if (input.is(Items.RAW_COPPER)) {
            return new ProcessingOperation(new ItemStack(IC2Items.COPPER_DUST.get(), 2), 1, 0.2F);
        }
        if (input.is(Items.RAW_IRON)) {
            return new ProcessingOperation(new ItemStack(IC2Items.IRON_DUST.get(), 2), 1, 0.2F);
        }
        if (input.is(Items.RAW_GOLD)) {
            return new ProcessingOperation(new ItemStack(IC2Items.GOLD_DUST.get(), 2), 1, 0.2F);
        }
        if (input.is(Blocks.COBBLESTONE.asItem())) {
            return new ProcessingOperation(new ItemStack(Blocks.SAND), 1, 0.0F);
        }
        if (input.is(Blocks.GRAVEL.asItem())) {
            return new ProcessingOperation(new ItemStack(Items.FLINT), 1, 0.0F);
        }
        if (input.is(IC2Items.LEAD_INGOT.get())) {
            return new ProcessingOperation(new ItemStack(IC2Items.LEAD_DUST.get()), 1, 0.1F);
        }
        if (input.is(IC2Items.TIN_INGOT.get())) {
            return new ProcessingOperation(new ItemStack(IC2Items.TIN_DUST.get()), 1, 0.1F);
        }
        if (input.is(Blocks.COPPER_BLOCK.asItem())) {
            return new ProcessingOperation(new ItemStack(IC2Items.COPPER_DUST.get(), 9), 1, 0.1F);
        }
        if (input.is(Items.COPPER_INGOT)) {
            return new ProcessingOperation(new ItemStack(IC2Items.COPPER_DUST.get()), 1, 0.1F);
        }
        if (input.is(Items.IRON_INGOT)) {
            return new ProcessingOperation(new ItemStack(IC2Items.IRON_DUST.get()), 1, 0.1F);
        }
        if (input.is(Items.GOLD_INGOT)) {
            return new ProcessingOperation(new ItemStack(IC2Items.GOLD_DUST.get()), 1, 0.1F);
        }
        if (input.is(Blocks.IRON_BLOCK.asItem())) {
            return new ProcessingOperation(new ItemStack(IC2Items.IRON_DUST.get(), 9), 1, 0.1F);
        }
        if (input.is(Blocks.GOLD_BLOCK.asItem())) {
            return new ProcessingOperation(new ItemStack(IC2Items.GOLD_DUST.get(), 9), 1, 0.1F);
        }
        return ProcessingOperation.empty();
    }

    private RecipeHolder<MaceratorRecipe> getDataDrivenRecipe(ItemStack input) {
        if (level == null || input.isEmpty()) {
            return null;
        }

        return level.getRecipeManager()
                .getRecipeFor(IC2RecipeTypes.MACERATING.get(), new SingleRecipeInput(input), level)
                .orElse(null);
    }
}
