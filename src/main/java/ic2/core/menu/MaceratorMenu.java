package ic2.core.menu;

import ic2.core.block.machine.MaceratorBlockEntity;
import ic2.core.init.IC2Blocks;
import ic2.core.init.IC2Menus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class MaceratorMenu extends AbstractContainerMenu {
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int UPGRADE_START = 2;
    private static final int UPGRADE_END_EXCLUSIVE = 6;
    private static final int CHARGE_SLOT = 6;
    private static final int CONTAINER_SLOTS = 7;
    private static final int MACHINE_SLOT_START = 0;
    private static final int MACHINE_SLOT_END = CONTAINER_SLOTS;
    private static final int PLAYER_INV_START = MACHINE_SLOT_END;
    private static final int PLAYER_INV_END = PLAYER_INV_START + 27;
    private static final int HOTBAR_START = PLAYER_INV_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final MaceratorBlockEntity blockEntity;
    private final ContainerData data;

    public MaceratorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, (MaceratorBlockEntity) playerInventory.player.level().getBlockEntity(buffer.readBlockPos()), null);
    }

    public MaceratorMenu(int containerId, Inventory playerInventory, MaceratorBlockEntity blockEntity, ContainerData data) {
        super(IC2Menus.MACERATOR.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data != null ? data : blockEntity.getData();
        checkContainerDataCount(this.data, 4);

        addSlot(new SlotItemHandler(blockEntity.getInventory(), 0, 73, 16) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return blockEntity.hasRecipe(stack);
            }
        });
        addSlot(new MachineResultSlot(blockEntity.getInventory(), 1, 73, 56, () -> blockEntity.awardExperience(playerInventory.player)));
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
        int arrowWidth = 24;

        if (progress <= 0 || maxProgress <= 0) {
            return 0;
        }

        return progress * arrowWidth / maxProgress;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }

        ItemStack copied = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        copied = stack.copy();

        if (index == OUTPUT_SLOT) {
            if (!moveItemStackTo(stack, PLAYER_INV_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index == INPUT_SLOT) {
            if (!moveItemStackTo(stack, PLAYER_INV_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, copied);
        } else if (index >= UPGRADE_START && index < MACHINE_SLOT_END) {
            if (!moveItemStackTo(stack, PLAYER_INV_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (blockEntity.isUpgrade(stack)) {
            if (!moveItemStackTo(stack, UPGRADE_START, UPGRADE_END_EXCLUSIVE, false)) {
                return ItemStack.EMPTY;
            }
        } else if (blockEntity.isChargeItem(stack)) {
            if (!moveItemStackTo(stack, CHARGE_SLOT, CHARGE_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (blockEntity.hasRecipe(stack)) {
            if (!moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= PLAYER_INV_START && index < PLAYER_INV_END) {
            if (!moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= HOTBAR_START && index < HOTBAR_END) {
            if (!moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_END, false)) {
                return ItemStack.EMPTY;
            }
        } else {
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
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, IC2Blocks.MACERATOR.get());
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
