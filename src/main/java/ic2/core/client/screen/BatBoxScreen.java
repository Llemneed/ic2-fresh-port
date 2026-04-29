package ic2.core.client.screen;

import ic2.core.menu.BatBoxMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class BatBoxScreen extends AbstractIc2MachineScreen<BatBoxMenu> {

    public BatBoxScreen(BatBoxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = leftPos;
        int top = topPos;

        drawBasePanel(guiGraphics, left, top);
        drawInfoBadge(guiGraphics, left + 8, top + 8);
        drawSlot(guiGraphics, left + 44, top + 20);
        drawSlot(guiGraphics, left + 112, top + 20);
        drawPlayerInventory(guiGraphics, left, top);
        drawArrow(guiGraphics, left + 66, top + 24, 42, menu.getScaledEnergy() / 2);
        drawEnergyDisplay(guiGraphics, left + 70, top + 45, menu.getScaledEnergy());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void drawEnergyDisplay(GuiGraphics guiGraphics, int x, int y, int energy) {
        guiGraphics.fill(x, y, x + 40, y + 10, 0xFF3A3A3A);
        guiGraphics.fill(x + 1, y + 1, x + 39, y + 9, 0xFFF0F0F0);
        guiGraphics.fill(x + 2, y + 2, x + 38, y + 8, 0xFF101010);

        int fill = Math.max(0, Math.min(36, energy));
        if (fill > 0) {
            guiGraphics.fill(x + 2, y + 2, x + 2 + fill, y + 8, 0xFFFF4D37);
        }
    }
}
