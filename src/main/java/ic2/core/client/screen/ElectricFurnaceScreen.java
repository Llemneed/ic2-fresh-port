package ic2.core.client.screen;

import ic2.core.menu.ElectricFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class ElectricFurnaceScreen extends AbstractIc2MachineScreen<ElectricFurnaceMenu> {

    public ElectricFurnaceScreen(ElectricFurnaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = leftPos;
        int top = topPos;

        drawMainPanel(guiGraphics, left, top);
        drawUpgradePanel(guiGraphics, left + 151, top);
        drawInfoBadge(guiGraphics, left + 8, top + 8);
        drawSlot(guiGraphics, left + 55, top + 16);
        drawSlot(guiGraphics, left + 111, top + 30);
        drawSlot(guiGraphics, left + 55, top + 52);
        drawPlayerInventory(guiGraphics, left, top);
        drawEnergyBar(guiGraphics, left + 59, top + 37, menu.getScaledEnergy());
        drawArrow(guiGraphics, left + 80, top + 35, 24, menu.getScaledProgress());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void drawMainPanel(GuiGraphics guiGraphics, int left, int top) {
        guiGraphics.fill(left, top, left + 176, top + 166, 0xFF171717);
        guiGraphics.fill(left + 1, top + 1, left + 175, top + 165, 0xFFC7C7C7);
        guiGraphics.fill(left + 2, top + 2, left + 174, top + 164, 0xFFCFCFCF);
        guiGraphics.fill(left + 4, top + 80, left + 172, top + 82, 0xFF2C2C2C);
    }

    private void drawUpgradePanel(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y, x + 24, y + 82, 0xFF171717);
        guiGraphics.fill(x + 1, y + 1, x + 23, y + 81, 0xFFC7C7C7);
        guiGraphics.fill(x + 2, y + 2, x + 22, y + 80, 0xFFCFCFCF);
        for (int i = 0; i < 4; i++) {
            drawSlot(guiGraphics, x + 2, y + 8 + i * 18);
        }
    }

    private void drawEnergyBar(GuiGraphics guiGraphics, int x, int y, int fill) {
        guiGraphics.fill(x, y, x + 8, y + 34, 0xFF313131);
        guiGraphics.fill(x + 1, y + 1, x + 7, y + 33, 0xFF5A5A5A);
        if (fill > 0) {
            guiGraphics.fill(x + 3, y + 30 - fill, x + 5, y + 30, 0xFF44D95A);
        }
    }
}
