package ic2.core.client.screen;

import ic2.core.menu.IronFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class IronFurnaceScreen extends AbstractIc2MachineScreen<IronFurnaceMenu> {

    public IronFurnaceScreen(IronFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = leftPos;
        int top = topPos;
        drawBasePanel(guiGraphics, left, top);
        drawInfoBadge(guiGraphics, left + 8, top + 8);
        drawSlot(guiGraphics, left + 79, top + 16);
        drawSlot(guiGraphics, left + 79, top + 52);
        drawSlot(guiGraphics, left + 117, top + 31);
        drawPlayerInventory(guiGraphics, left, top);
        drawFurnaceArrow(guiGraphics, left + 98, top + 35, menu.getScaledProgress());

        int burn = menu.getScaledBurnTime();
        drawTripleFlame(guiGraphics, left + 84, top + 36, burn);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void drawFurnaceArrow(GuiGraphics guiGraphics, int x, int y, int progress) {
        guiGraphics.fill(x, y + 4, x + 15, y + 6, 0xFF585858);
        guiGraphics.fill(x + 14, y + 2, x + 18, y + 8, 0xFF585858);

        int fill = Math.max(0, Math.min(15, progress * 15 / 24));
        if (fill > 0) {
            guiGraphics.fill(x, y + 4, x + fill, y + 6, 0xFFEDEDED);
            if (fill >= 11) {
                guiGraphics.fill(x + 14, y + 2, x + 18, y + 8, 0xFFEDEDED);
            }
        }
    }

    private void drawTripleFlame(GuiGraphics guiGraphics, int x, int y, int height) {
        if (height <= 0) {
            return;
        }

        int flame = Math.max(3, Math.min(8, height));
        drawFlameColumn(guiGraphics, x + 0, y, flame);
        drawFlameColumn(guiGraphics, x + 5, y - 1, flame + 1);
        drawFlameColumn(guiGraphics, x + 10, y, flame);
    }

    private void drawFlameColumn(GuiGraphics guiGraphics, int x, int y, int flame) {
        int top = y + 10 - flame;
        guiGraphics.fill(x + 1, top, x + 3, y + 9, 0xFFFFD348);
        guiGraphics.fill(x, top + 2, x + 4, y + 11, 0xFFFF8600);
    }
}
