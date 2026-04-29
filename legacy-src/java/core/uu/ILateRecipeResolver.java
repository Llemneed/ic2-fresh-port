// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.uu;

import java.util.List;

public interface ILateRecipeResolver
{
    List<RecipeTransformation> getTransformations(final Iterable<LeanItemStack> p0);
}
