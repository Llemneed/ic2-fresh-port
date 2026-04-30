package ic2.core.block.machine;

import ic2.core.block.entity.AbstractInventoryBlockEntity;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Blocks;
import ic2.core.menu.IronFurnaceMenu;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public final class IronFurnaceBlockEntity extends AbstractInventoryBlockEntity implements MenuProvider {
    private static final int INPUT_SLOT = 0;
    private static final int FUEL_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;
    private static final int MAX_PROGRESS = 100;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> MAX_PROGRESS;
                case 2 -> burnTime;
                case 3 -> burnTimeTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 2 -> burnTime = value;
                case 3 -> burnTimeTotal = value;
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
    private int burnTime;
    private int burnTimeTotal;
    private float pendingExperience;

    public IronFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(IC2BlockEntities.IRON_FURNACE.get(), pos, blockState, 3);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, IronFurnaceBlockEntity blockEntity) {
        boolean burning = blockEntity.burnTime > 0;
        if (burning) {
            blockEntity.burnTime--;
        }

        ItemStack input = blockEntity.inventory.getStackInSlot(INPUT_SLOT);
        RecipeHolder<SmeltingRecipe> recipe = blockEntity.getRecipe(input);
        boolean canSmelt = recipe != null && blockEntity.canOutput(recipe.value().assemble(new SingleRecipeInput(input), level.registryAccess()));

        if (!burning && canSmelt) {
            ItemStack fuel = blockEntity.inventory.getStackInSlot(FUEL_SLOT);
            int burn = fuel.getBurnTime(RecipeType.SMELTING);
            if (burn > 0) {
                blockEntity.burnTime = burn;
                blockEntity.burnTimeTotal = burn;
                fuel.shrink(1);
                if (fuel.isEmpty()) {
                    blockEntity.inventory.setStackInSlot(FUEL_SLOT, fuel.getCraftingRemainingItem());
                } else {
                    blockEntity.inventory.setStackInSlot(FUEL_SLOT, fuel);
                }
                burning = true;
            }
        }

        if (burning && canSmelt) {
            blockEntity.progress++;
            if (blockEntity.progress >= MAX_PROGRESS) {
                blockEntity.progress = 0;
                blockEntity.smelt(recipe, level);
            }
        } else if (blockEntity.progress > 0) {
            blockEntity.progress = 0;
        }

        boolean lit = burning;
        if (state.getValue(BlockStateProperties.LIT) != lit) {
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, lit), Block.UPDATE_CLIENTS);
        }

        setChanged(level, pos, state);
    }

    public ContainerData getData() {
        return data;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.ic2.iron_furnace");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new IronFurnaceMenu(containerId, playerInventory, this, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveInventory(tag, registries);
        tag.putInt("progress", progress);
        tag.putInt("burnTime", burnTime);
        tag.putInt("burnTimeTotal", burnTimeTotal);
        tag.putFloat("pendingExperience", pendingExperience);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            loadInventory(tag.getCompound("inventory"), registries);
        }
        progress = tag.getInt("progress");
        burnTime = tag.getInt("burnTime");
        burnTimeTotal = tag.getInt("burnTimeTotal");
        pendingExperience = tag.getFloat("pendingExperience");
    }

    public boolean canSmelt(ItemStack input) {
        return getRecipe(input) != null;
    }

    public static boolean isFuel(ItemStack stack) {
        return stack.getBurnTime(RecipeType.SMELTING) > 0;
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

    private RecipeHolder<SmeltingRecipe> getRecipe(ItemStack input) {
        if (level == null || input.isEmpty()) {
            return null;
        }

        return level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), level)
                .orElse(null);
    }

    private void smelt(RecipeHolder<SmeltingRecipe> recipe, Level level) {
        ItemStack result = recipe.value().assemble(new SingleRecipeInput(inventory.getStackInSlot(INPUT_SLOT)), level.registryAccess());
        inventory.extractItem(INPUT_SLOT, 1, false);
        pendingExperience += recipe.value().getExperience();
        ItemStack output = inventory.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) {
            inventory.setStackInSlot(OUTPUT_SLOT, result.copy());
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
        return ItemStack.isSameItemSameComponents(output, result) && output.getCount() + result.getCount() <= output.getMaxStackSize();
    }
}
