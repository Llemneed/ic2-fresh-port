package ic2.core.client.screen;

import ic2.core.menu.MetalFormerMenu;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

public final class MetalFormerScreen extends AbstractIc2MachineScreen<MetalFormerMenu> {
    public MetalFormerScreen(MetalFormerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = leftPos;
        int top = topPos;

        drawBasePanel(guiGraphics, left, top);
        drawUpgradePanel(guiGraphics, left + 181, top);
        drawInfoBadge(guiGraphics, left + 8, top + 8);
        drawSlot(guiGraphics, left + 14, top + 56);
        drawSlot(guiGraphics, left + 73, top + 16);
        drawSlot(guiGraphics, left + 73, top + 56);
        drawPlayerInventory(guiGraphics, left, top);
        drawArrow(guiGraphics, left + 95, top + 35, 24, menu.getScaledProgress());
        drawModeLabel(guiGraphics, left + 49, top + 30);
        drawEnergyBolt(guiGraphics, left + 24, top + 45);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderContextTooltips(guiGraphics, mouseX, mouseY);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void drawUpgradePanel(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y, x + 24, y + 82, 0xFF171717);
        guiGraphics.fill(x + 1, y + 1, x + 23, y + 81, 0xFFC7C7C7);
        guiGraphics.fill(x + 2, y + 2, x + 22, y + 80, 0xFFCFCFCF);
        for (int i = 0; i < 4; i++) {
            drawSlot(guiGraphics, x + 2, y + 8 + i * 18);
        }
    }

    private void drawModeLabel(GuiGraphics guiGraphics, int x, int y) {
        String text = switch (menu.getMode()) {
            case 0 -> "Extruding";
            case 1 -> "Rolling";
            case 2 -> "Cutting";
            default -> "Unknown";
        };
        guiGraphics.drawString(font, text, x, y, LABEL_COLOR, false);
        guiGraphics.drawString(font, "Shift+RMB block", x - 10, y + 10, 0xFF707070, false);
    }

    private void drawEnergyBolt(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x + 4, y, x + 8, y + 8, 0xFFE43131);
        guiGraphics.fill(x + 2, y + 7, x + 7, y + 11, 0xFFE43131);
        guiGraphics.fill(x + 5, y + 10, x + 10, y + 16, 0xFFE43131);
    }

    private void renderContextTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int localX = mouseX - leftPos;
        int localY = mouseY - topPos;

        if (isInside(localX, localY, 14, 56, 18, 18)) {
            guiGraphics.renderTooltip(font, tooltipLines(
                    Component.literal("Charge slot"),
                    Component.literal("Redstone dust or charged items")
            ), mouseX, mouseY);
            return;
        }

        if (isInside(localX, localY, 181, 8, 24, 72)) {
            guiGraphics.renderTooltip(font, tooltipLines(
                    Component.literal("Upgrade slots"),
                    Component.literal("Overclocker / Transformer / Storage / Inverter")
            ), mouseX, mouseY);
        }
    }

    private boolean isInside(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private List<FormattedCharSequence> tooltipLines(Component... lines) {
        return Arrays.stream(lines)
                .map(Component::getVisualOrderText)
                .toList();
    }
}
