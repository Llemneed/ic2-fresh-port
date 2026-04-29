package ic2.core.client.screen;

import ic2.core.menu.CompressorMenu;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

public final class CompressorScreen extends AbstractIc2MachineScreen<CompressorMenu> {
    private static final List<Component> RECIPE_HINTS = List.of(
            Component.literal("4 Clay Balls -> Clay Block"),
            Component.literal("4 Bricks -> Brick Block"),
            Component.literal("4 Snowballs -> Snow Block"),
            Component.literal("Coal Ball -> Coal Block"),
            Component.literal("Carbon Mesh -> Carbon Plate")
    );
    private boolean showRecipeHints;

    public CompressorScreen(CompressorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = leftPos;
        int top = topPos;

        drawBasePanel(guiGraphics, left, top);
        drawInfoBadge(guiGraphics, left + 8, top + 8);
        drawSlot(guiGraphics, left + 55, top + 16);
        drawSlot(guiGraphics, left + 111, top + 30);
        drawSlot(guiGraphics, left + 55, top + 52);
        drawRecipeButton(guiGraphics, left + 80, top + 35);
        drawDecorativeUpgradeColumn(guiGraphics, left + 151, top + 7);
        drawPlayerInventory(guiGraphics, left, top);
        drawEnergyBar(guiGraphics, left + 59, top + 37, menu.getScaledEnergy());
        drawCompressionProgress(guiGraphics, left + 80, top + 35, menu.getScaledProgress());
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
        if (localX >= 80 && localX < 102 && localY >= 35 && localY < 50 && button == 0) {
            showRecipeHints = !showRecipeHints;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void drawDecorativeUpgradeColumn(GuiGraphics guiGraphics, int x, int y) {
        for (int i = 0; i < 4; i++) {
            drawSlot(guiGraphics, x, y + i * 18);
        }
    }

    private void drawRecipeButton(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y, x + 22, y + 15, 0xFF6C6C6C);
        guiGraphics.fill(x + 1, y + 1, x + 21, y + 14, 0xFFE0E0E0);
        guiGraphics.fill(x + 2, y + 2, x + 20, y + 13, 0xFF9D9D9D);
        guiGraphics.fill(x + 6, y + 4, x + 9, y + 11, 0xFF7A5A35);
        guiGraphics.fill(x + 10, y + 4, x + 16, y + 6, 0xFFF0F0F0);
        guiGraphics.fill(x + 10, y + 8, x + 15, y + 10, 0xFFF0F0F0);
    }

    private void drawEnergyBar(GuiGraphics guiGraphics, int x, int y, int fill) {
        guiGraphics.fill(x, y, x + 8, y + 34, 0xFF313131);
        guiGraphics.fill(x + 1, y + 1, x + 7, y + 33, 0xFF5A5A5A);
        if (fill > 0) {
            guiGraphics.fill(x + 3, y + 30 - fill, x + 5, y + 30, 0xFF44D95A);
        }
    }

    private void drawCompressionProgress(GuiGraphics guiGraphics, int x, int y, int progress) {
        int fill = Math.max(0, Math.min(24, progress));
        guiGraphics.fill(x + 11, y + 1, x + 13, y + 15, 0xFFF0F0F0);
        if (fill > 0) {
            guiGraphics.fill(x + 6, y + 9, x + 6 + fill, y + 11, 0xFFE6E6E6);
        }
    }

    private void renderRecipeHints(GuiGraphics guiGraphics) {
        int panelX = leftPos - 158;
        int panelY = topPos + 10;
        int width = 150;
        int height = 66;
        guiGraphics.fill(panelX, panelY, panelX + width, panelY + height, 0xCC000000);
        guiGraphics.fill(panelX + 1, panelY + 1, panelX + width - 1, panelY + height - 1, 0xEE101010);
        guiGraphics.drawString(font, "Compressor recipes", panelX + 6, panelY + 6, 0xFF55FF55, false);
        for (int i = 0; i < RECIPE_HINTS.size(); i++) {
            guiGraphics.drawString(font, RECIPE_HINTS.get(i), panelX + 6, panelY + 18 + i * 8, 0xFFE0E0E0, false);
        }
    }

    private void renderContextTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int localX = mouseX - leftPos;
        int localY = mouseY - topPos;

        if (isInside(localX, localY, 80, 35, 22, 15)) {
            guiGraphics.renderTooltip(font, tooltipLines(
                    Component.literal("Recipe help"),
                    Component.literal("Shows compressor recipes")
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
