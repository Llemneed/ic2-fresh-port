// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.item;

import ic2.core.IHasGui;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;

public interface IHandHeldInventory
{
    IHasGui getInventory(final EntityPlayer p0, final ItemStack p1);
}
