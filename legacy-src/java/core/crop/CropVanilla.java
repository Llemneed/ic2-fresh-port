// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.crop;

import net.minecraft.item.ItemStack;
import ic2.api.crops.ICropTile;
import java.util.ArrayList;
import net.minecraft.util.ResourceLocation;
import java.util.List;
import net.minecraft.block.BlockCrops;

public abstract class CropVanilla extends IC2CropCard
{
    protected final int maxAge;
    
    protected CropVanilla(final BlockCrops block) {
        this(block.getMaxAge());
    }
    
    protected CropVanilla(final int maxAge) {
        this.maxAge = maxAge;
    }
    
    protected List<ResourceLocation> getDefaultTexturesLocation() {
        return super.getTexturesLocation();
    }
    
    @Override
    public List<ResourceLocation> getTexturesLocation() {
        final List<ResourceLocation> ret = new ArrayList<ResourceLocation>(this.getMaxSize());
        for (int size = 1; size <= this.getMaxSize(); ++size) {
            ret.add(new ResourceLocation("blocks/" + this.getId() + "_stage_" + size));
        }
        return ret;
    }
    
    @Override
    public String getDiscoveredBy() {
        return "Notch";
    }
    
    @Override
    public int getMaxSize() {
        return this.maxAge;
    }
    
    @Override
    public boolean canGrow(final ICropTile crop) {
        return crop.getCurrentSize() < this.getMaxSize() && crop.getLightLevel() >= 9;
    }
    
    protected abstract ItemStack getSeeds();
    
    protected abstract ItemStack getProduct();
    
    @Override
    public ItemStack getGain(final ICropTile crop) {
        return this.getProduct();
    }
    
    @Override
    public ItemStack getSeeds(final ICropTile crop) {
        if (crop.getStatGain() <= 1 && crop.getStatGrowth() <= 1 && crop.getStatResistance() <= 1) {
            return this.getSeeds();
        }
        return super.getSeeds(crop);
    }
}
