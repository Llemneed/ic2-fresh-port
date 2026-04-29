package ic2.core.menu;

import ic2.core.block.storage.BaseEnergyStorageBlockEntity;
import ic2.core.init.IC2Menus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class BatBoxMenu extends AbstractContainerMenu {
    private final BaseEnergyStorageBlockEntity blockEntity;
    private final ContainerData data;

    public BatBoxMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, (BaseEnergyStorageBlockEntity) playerInventory.player.level().getBlockEntity(buffer.readBlockPos()), null);
    }

    public BatBoxMenu(int containerId, Inventory playerInventory, BaseEnergyStorageBlockEntity blockEntity, ContainerData data) {
        super(IC2Menus.BATBOX.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data != null ? data : blockEntity.getData();

        addSlot(new SlotItemHandler(blockEntity.getInventory(), 0, 44, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.isChargeableItem(stack);
            }
        });
        addSlot(new SlotItemHandler(blockEntity.getInventory(), 1, 112, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.isDischargeableItem(stack);
            }
        });
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addDataSlots(this.data);
    }

    public int getScaledEnergy() {
        return getScaledEnergy(48);
    }

    public int getScaledEnergy(int width) {
        int stored = data.get(0);
        int max = data.get(1);
        if (stored <= 0 || max <= 0) {
            return 0;
        }
        return stored * width / max;
    }

    public int getEnergyStored() {
        return data.get(0);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copied = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        int containerSlots = 2;
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        copied = stack.copy();

        if (index < containerSlots) {
            if (!moveItemStackTo(stack, containerSlots, slots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else if (blockEntity.isDischargeableItem(stack)) {
            if (!moveItemStackTo(stack, 1, 2, false)) {
                return ItemStack.EMPTY;
            }
        } else if (blockEntity.isChargeableItem(stack)) {
            if (!moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < containerSlots + 27) {
            if (!moveItemStackTo(stack, containerSlots + 27, slots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, containerSlots, containerSlots + 27, false)) {
            return ItemStack.EMPTY;
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
