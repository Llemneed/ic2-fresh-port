// 
// Decompiled by Procyon v0.5.30
// 

package ic2.api.energy.tile;

import net.minecraft.util.EnumFacing;

public interface IEnergyAcceptor extends IEnergyTile
{
    boolean acceptsEnergyFrom(final IEnergyEmitter p0, final EnumFacing p1);
}
