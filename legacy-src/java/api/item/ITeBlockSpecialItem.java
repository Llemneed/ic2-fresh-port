// 
// Decompiled by Procyon v0.5.30
// 

package ic2.api.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public interface ITeBlockSpecialItem
{
    boolean doesOverrideDefault(final ItemStack p0);
    
    ModelResourceLocation getModelLocation(final ItemStack p0);
}
