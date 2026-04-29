package ic2.core.menu;

import ic2.core.block.machine.IronFurnaceBlockEntity;
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

public final class IronFurnaceMenu extends AbstractContainerMenu {
    private final IronFurnaceBlockEntity blockEntity;
    private final ContainerData data;

    public IronFurnaceMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, (IronFurnaceBlockEntity) playerInventory.player.level().getBlockEntity(buffer.readBlockPos()), null);
    }

    public IronFurnaceMenu(int containerId, Inventory playerInventory, IronFurnaceBlockEntity blockEntity, ContainerData data) {
        super(IC2Menus.IRON_FURNACE.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data != null ? data : blockEntity.getData();

        addSlot(new SlotItemHandler(blockEntity.getInventory(), 0, 79, 16) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.canSmelt(stack);
            }
        });
        addSlot(new SlotItemHandler(blockEntity.getInventory(), 1, 79, 52) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return IronFurnaceBlockEntity.isFuel(stack);
            }
        });
        addSlot(new MachineResultSlot(blockEntity.getInventory(), 2, 117, 31, () -> blockEntity.awardExperience(playerInventory.player)));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addDataSlots(this.data);
    }

    public int getScaledProgress() {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        return maxProgress > 0 && progress > 0 ? progress * 24 / maxProgress : 0;
    }

    public int getScaledBurnTime() {
        int burnTime = data.get(2);
        int burnTimeTotal = data.get(3);
        return burnTimeTotal > 0 && burnTime > 0 ? burnTime * 13 / burnTimeTotal : 0;
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

        if (index == 2) {
            if (!moveItemStackTo(stack, 3, 39, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, copied);
        } else if (index == 0 || index == 1) {
            if (!moveItemStackTo(stack, 3, 39, false)) {
                return ItemStack.EMPTY;
            }
        } else if (blockEntity.canSmelt(stack)) {
            if (!moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (IronFurnaceBlockEntity.isFuel(stack)) {
            if (!moveItemStackTo(stack, 1, 2, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (index < 30) {
                if (!moveItemStackTo(stack, 30, 39, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 3, 30, false)) {
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
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, IC2Blocks.IRON_FURNACE.get());
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
