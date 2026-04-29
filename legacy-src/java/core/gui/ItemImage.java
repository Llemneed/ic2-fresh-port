// 
// Decompiled by Procyon v0.5.30
// 

package ic2.core.gui;

import ic2.core.GuiIC2;
import net.minecraft.item.ItemStack;
import com.google.common.base.Supplier;

public class ItemImage extends GuiElement<ItemImage>
{
    private final Supplier<ItemStack> itemSupplier;
    
    public ItemImage(final GuiIC2<?> gui, final int x, final int y, final Supplier<ItemStack> itemSupplier) {
        super(gui, x, y, 16, 16);
        this.itemSupplier = itemSupplier;
    }
    
    @Override
    public void drawBackground(final int mouseX, final int mouseY) {
        super.drawBackground(mouseX, mouseY);
        final ItemStack stack = (ItemStack)this.itemSupplier.get();
        if (stack != null) {
            this.gui.drawItem(this.x, this.y, stack);
        }
    }
}
