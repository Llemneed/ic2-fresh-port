package ic2.core.menu;

import ic2.core.block.machine.RecyclerBlockEntity;
import ic2.core.init.IC2Blocks;
import ic2.core.init.IC2Menus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class RecyclerMenu extends AbstractContainerMenu {
    private final RecyclerBlockEntity blockEntity;
    private final ContainerData data;

    public RecyclerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, (RecyclerBlockEntity) playerInventory.player.level().getBlockEntity(buffer.readBlockPos()), null);
    }

    public RecyclerMenu(int containerId, Inventory playerInventory, RecyclerBlockEntity blockEntity, ContainerData data) {
        super(IC2Menus.RECYCLER.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data != null ? data : blockEntity.getData();

        addSlot(new SlotItemHandler(blockEntity.getInventory(), 0, 73, 16) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.hasRecipe(stack);
            }
        });
        addSlot(new MachineResultSlot(blockEntity.getInventory(), 1, 73, 56, () -> {}));
        addUpgradeSlot(2, 185, 8);
        addUpgradeSlot(3, 185, 26);
        addUpgradeSlot(4, 185, 44);
        addUpgradeSlot(5, 185, 62);
        addSlot(new SlotItemHandler(blockEntity.getInventory(), 6, 14, 56) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.isChargeItem(stack);
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addDataSlots(this.data);
    }

    public int getScaledProgress() {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        if (progress <= 0 || maxProgress <= 0) {
            return 0;
        }
        return progress * 24 / maxProgress;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copied = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        int containerSlots = 7;

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        copied = stack.copy();

        if (index == 1) {
            if (!moveItemStackTo(stack, containerSlots, slots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else if (index == 0) {
            if (!moveItemStackTo(stack, containerSlots, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, copied);
        } else if (index >= 2 && index <= 6) {
            if (!moveItemStackTo(stack, containerSlots, slots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else if (blockEntity.isUpgrade(stack)) {
            if (!moveItemStackTo(stack, 2, 6, false) && !moveItemStackTo(stack, 6, 7, false)) {
                return ItemStack.EMPTY;
            }
        } else if (blockEntity.isChargeItem(stack)) {
            if (!moveItemStackTo(stack, 6, 7, false)) {
                return ItemStack.EMPTY;
            }
        } else if (blockEntity.hasRecipe(stack)) {
            if (!moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (index < containerSlots + 27) {
                if (!moveItemStackTo(stack, containerSlots + 27, slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, containerSlots, containerSlots + 27, false)) {
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
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, IC2Blocks.RECYCLER.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 6 + column * 18, 83 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int slot = 0; slot < 9; slot++) {
            addSlot(new Slot(playerInventory, slot, 6 + slot * 18, 141));
        }
    }

    private void addUpgradeSlot(int slotIndex, int x, int y) {
        addSlot(new SlotItemHandler(blockEntity.getInventory(), slotIndex, x, y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.isUpgrade(stack);
            }
        });
    }
}
