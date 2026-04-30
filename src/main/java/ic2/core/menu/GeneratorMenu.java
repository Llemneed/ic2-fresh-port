package ic2.core.menu;

import ic2.core.block.generator.GeneratorBlockEntity;
import ic2.core.init.IC2Menus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class GeneratorMenu extends AbstractContainerMenu {
    private static final int FUEL_SLOT = 0;
    private static final int CONTAINER_SLOTS = 1;

    private final GeneratorBlockEntity blockEntity;
    private final ContainerData data;

    public GeneratorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, (GeneratorBlockEntity) playerInventory.player.level().getBlockEntity(buffer.readBlockPos()), null);
    }

    public GeneratorMenu(int containerId, Inventory playerInventory, GeneratorBlockEntity blockEntity, ContainerData data) {
        super(IC2Menus.GENERATOR.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data != null ? data : blockEntity.getData();

        addSlot(new SlotItemHandler(blockEntity.getInventory(), 0, 56, 52) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.isFuelItem(stack);
            }
        });
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addDataSlots(this.data);
    }

    public int getScaledBurnTime() {
        int burnTime = data.get(0);
        int burnTimeTotal = data.get(1);
        return burnTimeTotal > 0 && burnTime > 0 ? burnTime * 13 / burnTimeTotal : 0;
    }

    public int getScaledEnergy() {
        int stored = data.get(2);
        int max = data.get(3);
        return max > 0 && stored > 0 ? stored * 48 / max : 0;
    }

    public int getEnergyStored() {
        return data.get(2);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copied = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        copied = stack.copy();

        if (index == FUEL_SLOT) {
            if (!moveItemStackTo(stack, CONTAINER_SLOTS, slots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else if (blockEntity.isFuelItem(stack)) {
            if (!moveItemStackTo(stack, FUEL_SLOT, FUEL_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (index < CONTAINER_SLOTS + 27) {
                if (!moveItemStackTo(stack, CONTAINER_SLOTS + 27, slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, CONTAINER_SLOTS, CONTAINER_SLOTS + 27, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == copied.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);
        return copied;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.getLevel() != null
                && blockEntity.getLevel().getBlockEntity(blockEntity.getBlockPos()) == blockEntity
                && player.distanceToSqr(
                        blockEntity.getBlockPos().getX() + 0.5D,
                        blockEntity.getBlockPos().getY() + 0.5D,
                        blockEntity.getBlockPos().getZ() + 0.5D
                ) <= 64.0D;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 7 + column * 18, 83 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int slot = 0; slot < 9; slot++) {
            addSlot(new Slot(playerInventory, slot, 7 + slot * 18, 141));
        }
    }
}
