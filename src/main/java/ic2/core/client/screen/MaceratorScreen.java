package ic2.core.client.screen;

import ic2.core.menu.MaceratorMenu;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class MaceratorScreen extends AbstractIc2MachineScreen<MaceratorMenu> {
    private static final List<Component> RECIPE_HINTS = List.of(
            Component.literal("Copper Ore / Raw Copper -> 2 Copper Dust"),
            Component.literal("Tin Ore -> 2 Tin Dust"),
            Component.literal("Lead Ore -> 2 Lead Dust"),
            Component.literal("Cobblestone -> Sand"),
            Component.literal("Gravel -> Flint"),
            Component.literal("Copper Ingot -> Copper Dust"),
            Component.literal("Overclockers: faster, but higher EU/t")
    );
    private boolean showRecipeHints;

    public MaceratorScreen(MaceratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = leftPos;
        int top = topPos;

        drawMainPanel(guiGraphics, left, top);
        drawUpgradePanel(guiGraphics, left + 181, top);
        drawInfoBadge(guiGraphics, left + 8, top + 8);
        drawBookSlot(guiGraphics, left + 14, top + 18);
        drawSlot(guiGraphics, left + 14, top + 56);
        drawSlot(guiGraphics, left + 73, top + 16);
        drawSlot(guiGraphics, left + 73, top + 56);
        drawPlayerInventory(guiGraphics, left, top);
        drawMaceratorProcess(guiGraphics, left + 74, top + 35, menu.getScaledProgress());

        drawEnergyBolt(guiGraphics, left + 24, top + 45);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (showRecipeHints) {
            renderRecipeHints(guiGraphics);
        }
        renderContextTooltips(guiGraphics, mouseX, mouseY);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int localX = (int) mouseX - leftPos;
        int localY = (int) mouseY - topPos;
        if (localX >= 14 && localX < 32 && localY >= 18 && localY < 36 && button == 0) {
            showRecipeHints = !showRecipeHints;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
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

    private void drawEnergyBolt(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x + 4, y, x + 8, y + 8, 0xFFE43131);
        guiGraphics.fill(x + 2, y + 7, x + 7, y + 11, 0xFFE43131);
        guiGraphics.fill(x + 5, y + 10, x + 10, y + 16, 0xFFE43131);
    }

    private void drawMaceratorProcess(GuiGraphics guiGraphics, int x, int y, int progress) {
        guiGraphics.fill(x + 7, y + 2, x + 11, y + 10, 0xFFF1F1F1);
        guiGraphics.fill(x + 8, y + 0, x + 10, y + 4, 0xFFF1F1F1);

        int fill = Math.max(0, Math.min(10, progress * 10 / 24));
        if (fill > 0) {
            guiGraphics.fill(x + 5, y + 15 - fill, x + 8, y + 15, 0xFFE5E5E5);
            guiGraphics.fill(x + 10, y + 15 - fill, x + 13, y + 15, 0xFFE5E5E5);
            guiGraphics.fill(x + 6, y + 14, x + 12, y + 16, 0xFFE5E5E5);
        } else {
            guiGraphics.fill(x + 8, y + 12, x + 10, y + 15, 0xFFA0A0A0);
        }
    }

    private void renderRecipeHints(GuiGraphics guiGraphics) {
        int panelX = leftPos - 158;
        int panelY = topPos + 10;
        int width = 150;
        int height = 76;
        guiGraphics.fill(panelX, panelY, panelX + width, panelY + height, 0xCC000000);
        guiGraphics.fill(panelX + 1, panelY + 1, panelX + width - 1, panelY + height - 1, 0xEE101010);
        guiGraphics.drawString(font, "Macerator recipes", panelX + 6, panelY + 6, 0xFF55FF55, false);
        guiGraphics.drawString(font, "Book icon toggles this help", panelX + 6, panelY + 14, 0xFFB8D8FF, false);

        for (int i = 0; i < RECIPE_HINTS.size(); i++) {
            guiGraphics.drawString(font, RECIPE_HINTS.get(i), panelX + 6, panelY + 26 + i * 8, 0xFFE0E0E0, false);
        }
    }

    private void renderContextTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int localX = mouseX - leftPos;
        int localY = mouseY - topPos;

        if (isInside(localX, localY, 14, 18, 18, 18)) {
            guiGraphics.renderTooltip(font, tooltipLines(
                    Component.literal("Recipe help"),
                    Component.literal("Shows macerator recipes")
            ), mouseX, mouseY);
            return;
        }

        if (isInside(localX, localY, 14, 56, 18, 18)) {
            guiGraphics.renderTooltip(font, tooltipLines(
                    Component.literal("Charge slot"),
                    Component.literal("Redstone dust fills internal energy")
            ), mouseX, mouseY);
            return;
        }

        if (isInside(localX, localY, 181, 8, 24, 72)) {
            guiGraphics.renderTooltip(font, tooltipLines(
                    Component.literal("Upgrade slots"),
                    Component.literal("Overclocker: faster, higher EU/t"),
                    Component.literal("Energy Storage: larger internal buffer"),
                    Component.literal("Transformer: raises safe input packet"),
                    Component.literal("Inverter: works only with redstone power")
            ), mouseX, mouseY);
        }
    }

    private boolean isInside(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private List<FormattedCharSequence> tooltipLines(Component... lines) {
        return java.util.Arrays.stream(lines)
                .map(Component::getVisualOrderText)
                .toList();
    }
}
