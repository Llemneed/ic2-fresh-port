// 
// Decompiled by Procyon v0.5.30
// 

package ic2.api.energy.tile;

public interface IExplosionPowerOverride
{
    boolean shouldExplode();
    
    float getExplosionPower(final int p0, final float p1);
}
