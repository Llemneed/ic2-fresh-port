// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import ic2.core.block.invslot.InvSlot;
import net.minecraft.inventory.IInventory;

public class IInventoryInvSlot implements IInventory
{
    public final InvSlot slot;
    
    public IInventoryInvSlot(final InvSlot slot) {
        this.slot = slot;
    }
    
    public int getSizeInventory() {
        return this.slot.size();
    }
    
    public int getInventoryStackLimit() {
        return this.slot.getStackSizeLimit();
    }
    
    public boolean isEmpty() {
        return this.slot.isEmpty();
    }
    
    public boolean isItemValidForSlot(final int index, final ItemStack stack) {
        return this.slot.accepts(stack);
    }
    
    public ItemStack getStackInSlot(final int index) {
        return this.slot.get(index);
    }
    
    public ItemStack decrStackSize(final int index, final int count) {
        final ItemStack stack = this.getStackInSlot(index);
        if (!StackUtil.isEmpty(stack)) {
            final int amount = Math.min(StackUtil.getSize(stack), count);
            final ItemStack out = StackUtil.copyWithSize(stack, amount);
            this.setInventorySlotContents(index, StackUtil.decSize(stack, amount));
            return out;
        }
        return StackUtil.emptyStack;
    }
    
    public void setInventorySlotContents(final int index, final ItemStack stack) {
        this.slot.put(index, stack);
    }
    
    public ItemStack removeStackFromSlot(final int index) {
        final ItemStack stack = this.getStackInSlot(index);
        this.setInventorySlotContents(index, StackUtil.emptyStack);
        return stack;
    }
    
    public void clear() {
        this.slot.clear();
    }
    
    public void markDirty() {
        this.slot.onChanged();
    }
    
    public boolean isUsableByPlayer(final EntityPlayer player) {
        return true;
    }
    
    public void openInventory(final EntityPlayer player) {
    }
    
    public void closeInventory(final EntityPlayer player) {
    }
    
    public boolean hasCustomName() {
        return this.slot.base.hasCustomName();
    }
    
    public String getName() {
        return this.slot.base.getName();
    }
    
    public ITextComponent getDisplayName() {
        return this.slot.base.getDisplayName();
    }
    
    public int getFieldCount() {
        return this.slot.base.getFieldCount();
    }
    
    public int getField(final int id) {
        return this.slot.base.getField(id);
    }
    
    public void setField(final int id, final int value) {
        this.slot.base.setField(id, value);
    }
}
