package ic2.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public abstract class AbstractEuInventoryBlockEntity extends AbstractEuBlockEntity {
    protected final ItemStackHandler inventory;

    protected AbstractEuInventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int slotCount) {
        super(type, pos, blockState);
        this.inventory = new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public void dropContents() {
        Level level = getLevel();
        if (level == null || level.isClientSide) {
            return;
        }

        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            var stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                level.addFreshEntity(new ItemEntity(level,
                        worldPosition.getX() + 0.5,
                        worldPosition.getY() + 0.5,
                        worldPosition.getZ() + 0.5,
                        stack.copy()));
            }
        }
    }

    protected void saveInventory(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory", inventory.serializeNBT(registries));
    }

    protected void loadInventory(CompoundTag inventoryTag, HolderLookup.Provider registries) {
        if (inventoryTag.isEmpty()) {
            return;
        }

        inventory.deserializeNBT(registries, inventoryTag);
    }
}
