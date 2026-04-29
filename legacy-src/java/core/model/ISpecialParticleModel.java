// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import ic2.core.block.state.Ic2BlockState;

public interface ISpecialParticleModel
{
    TextureAtlasSprite getParticleTexture(final Ic2BlockState.Ic2BlockStateInstance p0);
}
