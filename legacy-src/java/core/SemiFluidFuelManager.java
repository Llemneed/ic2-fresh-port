// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core;

import java.util.Collections;
import java.util.Iterator;
import net.minecraftforge.fluids.FluidRegistry;
import java.util.HashSet;
import java.util.Set;
import net.minecraftforge.fluids.Fluid;
import java.util.HashMap;
import java.util.Map;
import ic2.api.recipe.ISemiFluidFuelManager;

public class SemiFluidFuelManager implements ISemiFluidFuelManager
{
    private final Map<String, BurnProperty> burnProperties;
    
    public SemiFluidFuelManager() {
        this.burnProperties = new HashMap<String, BurnProperty>();
    }
    
    @Override
    public void addFluid(final String fluidName, final int amount, final double power) {
        if (this.burnProperties.containsKey(fluidName)) {
            throw new RuntimeException("The fluid " + fluidName + " does already have a burn property assigned.");
        }
        this.burnProperties.put(fluidName, new BurnProperty(amount, power));
    }
    
    @Override
    public BurnProperty getBurnProperty(final Fluid fluid) {
        if (fluid == null) {
            return null;
        }
        return this.burnProperties.get(fluid.getName());
    }
    
    @Override
    public boolean acceptsFluid(final Fluid fluid) {
        return fluid != null && this.burnProperties.containsKey(fluid.getName());
    }
    
    @Override
    public Set<Fluid> getAcceptedFluids() {
        final Set<Fluid> ret = new HashSet<Fluid>(this.burnProperties.size() * 2, 0.5f);
        for (final String fluidName : this.burnProperties.keySet()) {
            final Fluid fluid = FluidRegistry.getFluid(fluidName);
            if (fluid != null) {
                ret.add(fluid);
            }
        }
        return ret;
    }
    
    @Override
    public Map<String, BurnProperty> getBurnProperties() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends BurnProperty>)this.burnProperties);
    }
}
