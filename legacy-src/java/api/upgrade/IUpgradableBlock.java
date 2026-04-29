// 
// Decompiled by Procyon v0.5.30
// 

package ic2.api.upgrade;

import java.util.Set;

public interface IUpgradableBlock
{
    double getEnergy();
    
    boolean useEnergy(final double p0);
    
    Set<UpgradableProperty> getUpgradableProperties();
}
