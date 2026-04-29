// 
// Decompiled by Procyon v0.5.30
// 

package ic2.api.recipe;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface IPatternStorage
{
    boolean addPattern(final ItemStack p0);
    
    List<ItemStack> getPatterns();
}
