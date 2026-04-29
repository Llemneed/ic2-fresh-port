package ic2.core.client.screen;

import ic2.core.menu.RecyclerMenu;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

public final class RecyclerScreen extends AbstractIc2MachineScreen<RecyclerMenu> {
    private static final List<Component> RECIPE_HINTS = List.of(
            Component.literal("Recycler accepts almost any junk item"),
            Component.literal("Each operation has a chance to make Scrap"),
            Component.literal("Scrap can be boxed into a Scrap Box"),
            Component.literal("Scrap Box gives a random reward"),
            Component.literal("Overclockers: faster, but higher EU/t")
    );
    private boolean showRecipeHints;

    public RecyclerScreen(RecyclerMenu menu, Inventory playerInventory, Component title) {
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
        drawRecyclerProcess(guiGraphics, left + 74, top + 35, menu.getScaledProgress());
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

    private void drawRecyclerProcess(GuiGraphics guiGraphics, int x, int y, int progress) {
        int fill = Math.max(0, Math.min(24, progress));
        guiGraphics.fill(x + 2, y + 5, x + 22, y + 7, 0xFFF1F1F1);
        if (fill > 0) {
            guiGraphics.fill(x + 2, y + 8, x + 2 + fill, y + 12, 0xFFE5E5E5);
        }
        guiGraphics.fill(x + 23, y + 6, x + 25, y + 14, 0xFFF1F1F1);
    }

    private void renderRecipeHints(GuiGraphics guiGraphics) {
        int panelX = leftPos - 158;
        int panelY = topPos + 10;
        int width = 150;
        int height = 66;
        guiGraphics.fill(panelX, panelY, panelX + width, panelY + height, 0xCC000000);
        guiGraphics.fill(panelX + 1, panelY + 1, panelX + width - 1, panelY + height - 1, 0xEE101010);
        guiGraphics.drawString(font, "Recycler help", panelX + 6, panelY + 6, 0xFF55FF55, false);
        for (int i = 0; i < RECIPE_HINTS.size(); i++) {
            guiGraphics.drawString(font, RECIPE_HINTS.get(i), panelX + 6, panelY + 18 + i * 8, 0xFFE0E0E0, false);
        }
    }

    private void renderContextTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int localX = mouseX - leftPos;
        int localY = mouseY - topPos;

        if (isInside(localX, localY, 14, 18, 18, 18)) {
            guiGraphics.renderTooltip(font, tooltipLines(
                    Component.literal("Recycler help"),
                    Component.literal("Shows recycler hints")
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
