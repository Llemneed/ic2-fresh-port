// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.block.machine.container;

import gnu.trove.iterator.TIntIterator;
import ic2.core.util.Tuple;
import java.util.List;
import gnu.trove.TIntCollection;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.inventory.IInventory;
import ic2.core.util.StackUtil;
import net.minecraft.item.ItemStack;
import ic2.core.block.invslot.InvSlot;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.inventory.Slot;
import ic2.core.slot.SlotHologramSlot;
import gnu.trove.map.hash.TIntIntHashMap;
import net.minecraft.entity.player.EntityPlayer;
import gnu.trove.map.TIntIntMap;
import ic2.core.block.machine.tileentity.TileEntityBatchCrafter;

public class ContainerBatchCrafter extends ContainerElectricMachine<TileEntityBatchCrafter>
{
    protected final TIntIntMap indexToSlot;
    public static final short HEIGHT = 206;
    
    public ContainerBatchCrafter(final EntityPlayer player, final TileEntityBatchCrafter tileEntity) {
        super(player, tileEntity, 206, 8, 62);
        this.indexToSlot = (TIntIntMap)new TIntIntHashMap();
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                this.addSlotToContainer((Slot)new SlotHologramSlot(tileEntity.craftingGrid, x + y * 3, 30 + x * 18, 17 + y * 18, 1, new SlotHologramSlot.ChangeCallback() {
                    @Override
                    public void onChanged(final int index) {
                        if (((TileEntityBatchCrafter)ContainerBatchCrafter.this.base).hasWorld() && !((TileEntityBatchCrafter)ContainerBatchCrafter.this.base).getWorld().isRemote) {
                            ((TileEntityBatchCrafter)ContainerBatchCrafter.this.base).matrixChange(index);
                        }
                    }
                }));
            }
        }
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity.craftingOutput, 0, 124, 35));
        for (int slot = 0; slot < 9; ++slot) {
            this.indexToSlot.put(slot, this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity.ingredientsRow[slot], 0, 8 + slot * 18, 84)).slotNumber);
            this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity.containerOutput, slot, 8 + slot * 18, 102));
        }
        for (int slot = 0; slot < 4; ++slot) {
            this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity.upgradeSlot, slot, 152, 8 + slot * 18));
        }
    }

    @Override
    protected ItemStack handlePlayerSlotShiftClick(EntityPlayer player, ItemStack sourceItemStack) {
        Tuple.T2 changes = StackUtil.balanceStacks((this.base).ingredients, (this.base).acceptPredicate, StackUtil.getSlotsFromInv((this.base).ingredients), Collections.singleton(sourceItemStack));
        TIntIterator iter = ((TIntCollection)changes.b).iterator();

        while(iter.hasNext()) {
            int currentSlot = iter.next();
            this.inventorySlots.get(this.indexToSlot.get(currentSlot)).onSlotChanged();
        }

        sourceItemStack = ((List)changes.a).isEmpty()?StackUtil.emptyStack:(ItemStack)((List)changes.a).get(0);
        return sourceItemStack;
    }

    @Override
    public List<String> getNetworkedFields() {
        final List<String> fields = super.getNetworkedFields();
        fields.add("guiProgress");
        fields.add("recipeOutput");
        return fields;
    }
}
