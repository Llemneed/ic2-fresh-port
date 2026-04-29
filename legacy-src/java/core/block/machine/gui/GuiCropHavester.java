// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.block.machine.gui;

import net.minecraft.util.ResourceLocation;
import ic2.core.gui.GuiElement;
import ic2.core.gui.EnergyGauge;
import ic2.core.block.TileEntityBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ic2.core.block.machine.container.ContainerCropHavester;
import ic2.core.GuiIC2;

@SideOnly(Side.CLIENT)
public class GuiCropHavester extends GuiIC2<ContainerCropHavester>
{
    public GuiCropHavester(final ContainerCropHavester container) {
        super(container, 191);
        this.addElement(EnergyGauge.asBolt(this, 156, 43, (TileEntityBlock)container.base));
    }
    
    public ResourceLocation getTexture() {
        return new ResourceLocation("ic2", "textures/gui/GUICropHavester.png");
    }
}
