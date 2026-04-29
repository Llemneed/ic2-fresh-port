// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.block.machine.container;

import java.util.List;
import ic2.core.block.invslot.InvSlot;
import net.minecraft.inventory.Slot;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import ic2.core.block.machine.tileentity.TileEntityCropHarvester;

public class ContainerCropHavester extends ContainerElectricMachine<TileEntityCropHarvester>
{
    public ContainerCropHavester(final EntityPlayer player, final TileEntityCropHarvester base) {
        super(player, base, 191, 152, 58);
        for (int y = 0; y < base.contentSlot.size() / 5; ++y) {
            for (int x = 0; x < 5; ++x) {
                this.addSlotToContainer((Slot)new SlotInvSlot(base.contentSlot, x + y * 5, 44 + x * 18, 22 + y * 18));
            }
        }
        this.addSlotToContainer((Slot)new SlotInvSlot(base.upgradeSlot, 0, 80, 80));
        this.addSlotToContainer((Slot)new SlotInvSlot(base.cropnalyzerSlot, 0, 15, 40));
    }
    
    @Override
    public List<String> getNetworkedFields() {
        final List<String> ret = super.getNetworkedFields();
        ret.add("energy");
        return ret;
    }
}
