// 
// Decompiled by Procyon v0.5.30
// 

package ic2.api.recipe;

import java.util.Map;
import net.minecraftforge.fluids.Fluid;

public interface ISemiFluidFuelManager extends ILiquidAcceptManager
{
    void addFluid(final String p0, final int p1, final double p2);
    
    BurnProperty getBurnProperty(final Fluid p0);
    
    Map<String, BurnProperty> getBurnProperties();
    
    public static final class BurnProperty
    {
        public final int amount;
        public final double power;
        
        public BurnProperty(final int amount, final double power) {
            this.amount = amount;
            this.power = power;
        }
    }
}
