package ic2.core.block.generator;

import ic2.core.energy.EnergyConsumer;
import ic2.core.init.IC2BlockEntities;
import ic2.core.init.IC2Items;
import ic2.core.menu.GeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.items.ItemStackHandler;

public class GeneratorBlockEntity extends BlockEntity implements MenuProvider {
    private static final int FUEL_SLOT = 0;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> burnTimeTotal;
                case 2 -> energyStored;
                case 3 -> maxEnergy;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> burnTimeTotal = value;
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

    private final int energyPerTick;
    private final int maxEnergy;
    private final int outputPerTick;
    private final String displayKey;
    private int burnTime;
    private int burnTimeTotal;
    private int energyStored;

    public GeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        this(IC2BlockEntities.GENERATOR.get(), pos, blockState, 10, 4000, 16, "block.ic2.generator");
    }

    protected GeneratorBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState blockState,
            int energyPerTick,
            int maxEnergy,
            int outputPerTick,
            String displayKey
    ) {
        super(type, pos, blockState);
        this.energyPerTick = energyPerTick;
        this.maxEnergy = maxEnergy;
        this.outputPerTick = outputPerTick;
        this.displayKey = displayKey;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GeneratorBlockEntity blockEntity) {
        blockEntity.serverTickInternal(level, pos, state);
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

        ItemStack stack = inventory.getStackInSlot(FUEL_SLOT);
        if (!stack.isEmpty()) {
            level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, stack.copy()));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(displayKey);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new GeneratorMenu(containerId, playerInventory, this, data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("burnTime", burnTime);
        tag.putInt("burnTimeTotal", burnTimeTotal);
        tag.putInt("energyStored", energyStored);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        burnTime = tag.getInt("burnTime");
        burnTimeTotal = tag.getInt("burnTimeTotal");
        energyStored = tag.getInt("energyStored");
    }

    public boolean isFuelItem(ItemStack stack) {
        return getFuelValue(stack) > 0;
    }

    protected void serverTickInternal(Level level, BlockPos pos, BlockState state) {
        boolean burning = burnTime > 0;
        if (burning && energyStored < maxEnergy) {
            burnTime--;
            energyStored = Math.min(maxEnergy, energyStored + energyPerTick);
        }

        if (!burning && energyStored <= maxEnergy - energyPerTick) {
            ItemStack fuel = inventory.getStackInSlot(FUEL_SLOT);
            int burn = getFuelValue(fuel);
            if (burn > 0) {
                burnTime = burn;
                burnTimeTotal = burn;
                ItemStack remainder = getFuelRemainder(fuel.copy());
                fuel.shrink(1);
                if (fuel.isEmpty()) {
                    inventory.setStackInSlot(FUEL_SLOT, remainder);
                } else {
                    inventory.setStackInSlot(FUEL_SLOT, fuel);
                }
            }
        }

        pushEnergy();

        boolean lit = burnTime > 0;
        if (state.getValue(BlockStateProperties.LIT) != lit) {
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, lit), Block.UPDATE_CLIENTS);
        }

        setChanged(level, pos, state);
    }

    protected int getFuelValue(ItemStack stack) {
        if (stack.is(IC2Items.SCRAP.get())) {
            return 100;
        }
        return stack.getBurnTime(RecipeType.SMELTING);
    }

    protected ItemStack getFuelRemainder(ItemStack stack) {
        return stack.getCraftingRemainingItem();
    }

    private void pushEnergy() {
        if (level == null || energyStored <= 0) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if (energyStored <= 0) {
                return;
            }

            BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(direction));
            if (blockEntity instanceof EnergyConsumer consumer && consumer.canReceiveEnergy()) {
                int packet = Math.min(outputPerTick, energyStored);
                if (packet > consumer.maxInputPerTick()) {
                    consumer.onOvervoltage(packet);
                    energyStored -= packet;
                    continue;
                }

                int sent = consumer.receiveEnergy(packet);
                energyStored -= sent;
            }
        }
    }

    public static boolean isFuel(ItemStack stack) {
        return stack.is(IC2Items.SCRAP.get()) || stack.getBurnTime(RecipeType.SMELTING) > 0 || stack.is(Items.LAVA_BUCKET);
    }
}
