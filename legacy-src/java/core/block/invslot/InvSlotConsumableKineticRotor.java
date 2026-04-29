// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.block.invslot;

import net.minecraft.tileentity.TileEntity;
import ic2.core.IC2;
import ic2.core.network.NetworkManager;
import net.minecraft.item.ItemStack;
import ic2.core.block.TileEntityInventory;
import ic2.api.item.IKineticRotor;

public class InvSlotConsumableKineticRotor extends InvSlotConsumableClass
{
    private final String updateName;
    private final IKineticRotor.GearboxType type;
    
    public InvSlotConsumableKineticRotor(final TileEntityInventory base1, final String name1, final Access access1, final int count, final InvSide preferredSide1, final IKineticRotor.GearboxType type) {
        this(base1, name1, access1, count, preferredSide1, type, null);
    }
    
    public InvSlotConsumableKineticRotor(final TileEntityInventory base, final String name, final Access access, final int count, final InvSide preferredSide, final IKineticRotor.GearboxType type, final String field) {
        super(base, name, access, count, preferredSide, IKineticRotor.class);
        this.type = type;
        this.updateName = field;
    }
    
    @Override
    public boolean accepts(final ItemStack stack) {
        return super.accepts(stack) && ((IKineticRotor)stack.getItem()).isAcceptedType(stack, this.type);
    }
    
    @Override
    public void onChanged() {
        if (this.updateName != null && this.base.hasWorld() && !this.base.getWorld().isRemote) {
            IC2.network.get(true).updateTileEntityField(this.base, this.updateName);
        }
    }
}
