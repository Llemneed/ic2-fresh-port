// 
// Decompiled by Procyon v0.5.30
// 

package ic2.api.info;

import net.minecraft.item.ItemStack;

public interface IInfoProvider
{
    double getEnergyValue(final ItemStack p0);
    
    int getFuelValue(final ItemStack p0, final boolean p1);
}
