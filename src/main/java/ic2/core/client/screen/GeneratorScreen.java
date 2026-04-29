package ic2.core.client.screen;

import ic2.core.menu.GeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class GeneratorScreen extends AbstractIc2MachineScreen<GeneratorMenu> {

    public GeneratorScreen(GeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = leftPos;
        int top = topPos;
        drawBasePanel(guiGraphics, left, top);
        drawInfoBadge(guiGraphics, left + 8, top + 8);
        drawSlot(guiGraphics, left + 56, top + 16);
        drawSlot(guiGraphics, left + 56, top + 52);
        drawPlayerInventory(guiGraphics, left, top);
        drawGeneratorDisplay(guiGraphics, left + 100, top + 39, menu.getScaledEnergy());

        int burn = menu.getScaledBurnTime();
        drawTripleFlame(guiGraphics, left + 57, top + 36, burn);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void drawGeneratorDisplay(GuiGraphics guiGraphics, int x, int y, int energy) {
        guiGraphics.fill(x, y, x + 36, y + 18, 0xFF3A3A3A);
        guiGraphics.fill(x + 1, y + 1, x + 35, y + 17, 0xFFF0F0F0);
        guiGraphics.fill(x + 3, y + 3, x + 33, y + 15, 0xFF101010);

        int litBars = Math.max(0, Math.min(4, energy / 12));
        for (int bar = 0; bar < 4; bar++) {
            int color = bar < litBars ? 0xFFFF3030 : 0xFF3A3A3A;
            int barX = x + 6 + bar * 6;
            guiGraphics.fill(barX, y + 5, barX + 4, y + 13, color);
        }
    }

    private void drawTripleFlame(GuiGraphics guiGraphics, int x, int y, int height) {
        if (height <= 0) {
            return;
        }

        int flame = Math.max(3, Math.min(8, height));
        drawFlameColumn(guiGraphics, x + 0, y, flame - 1);
        drawFlameColumn(guiGraphics, x + 4, y - 1, flame + 1);
        drawFlameColumn(guiGraphics, x + 8, y, flame - 1);
    }

    private void drawFlameColumn(GuiGraphics guiGraphics, int x, int y, int flame) {
        int top = y + 10 - flame;
        guiGraphics.fill(x + 1, top, x + 3, y + 9, 0xFFFFD348);
        guiGraphics.fill(x, top + 2, x + 4, y + 11, 0xFFFF8600);
    }
}
