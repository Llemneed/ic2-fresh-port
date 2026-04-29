// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.item.type;

import ic2.core.profile.NotExperimental;
import ic2.core.profile.NotClassic;
import ic2.core.block.state.IIdProvider;

public enum IngotResourceType implements IIdProvider
{
    alloy(0), 
    bronze(1), 
    copper(2), 
    lead(3), 
    silver(4), 
    steel(5), 
    tin(6), 
    refined_iron(7), 
    uranium(8);
    
    private final int id;
    
    private IngotResourceType(final int id) {
        this.id = id;
    }
    
    @Override
    public String getName() {
        return this.name();
    }
    
    @Override
    public int getId() {
        return this.id;
    }
}
