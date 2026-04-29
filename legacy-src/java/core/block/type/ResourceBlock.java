// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.block.type;

import net.minecraft.block.SoundType;
import ic2.core.profile.NotClassic;
import ic2.core.block.state.IIdProvider;

public enum ResourceBlock implements IIdProvider, IExtBlockType, IBlockSound
{
    basalt(20.0f, 45.0f, false), 
    copper_ore(3.0f, 5.0f, false), 
    lead_ore(2.0f, 4.0f, false), 
    tin_ore(3.0f, 5.0f, false), 
    uranium_ore(4.0f, 6.0f, false), 
    bronze_block(5.0f, 10.0f, true), 
    copper_block(4.0f, 10.0f, true), 
    lead_block(4.0f, 10.0f, true), 
    steel_block(8.0f, 10.0f, true), 
    tin_block(4.0f, 10.0f, true), 
    uranium_block(6.0f, 10.0f, true), 
    reinforced_stone(80.0f, 180.0f, false), 
    machine(5.0f, 10.0f, true), 
    advanced_machine(8.0f, 10.0f, true), 
    reactor_vessel(40.0f, 90.0f, false), 
    silver_block(4.0f, 10.0f, true);
    
    private final float hardness;
    private final float explosionResistance;
    private final boolean metal;
    
    private ResourceBlock(final float hardness, final float explosionResistance, final boolean metal) {
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
        this.metal = metal;
    }
    
    @Override
    public String getName() {
        return this.name();
    }
    
    @Override
    public int getId() {
        return this.ordinal();
    }
    
    @Override
    public float getHardness() {
        return this.hardness;
    }
    
    @Override
    public float getExplosionResistance() {
        return this.explosionResistance;
    }
    
    @Override
    public SoundType getSound() {
        return this.metal ? SoundType.METAL : SoundType.STONE;
    }
}
