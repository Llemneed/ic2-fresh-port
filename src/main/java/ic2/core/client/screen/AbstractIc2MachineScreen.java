package ic2.core.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

abstract class AbstractIc2MachineScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected static final int PANEL_OUTER = 0xFF2C2C2C;
    protected static final int PANEL_INNER = 0xFFC6C6C6;
    protected static final int PANEL_SHADOW = 0xFF7C7C7C;
    protected static final int SLOT_OUTER = 0xFF6C6C6C;
    protected static final int SLOT_LIGHT = 0xFFE0E0E0;
    protected static final int SLOT_INNER = 0xFF9D9D9D;
    protected static final int LABEL_COLOR = 0xFF404040;

    protected AbstractIc2MachineScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
        titleLabelY = 8;
        inventoryLabelX = 0;
        inventoryLabelY = 0;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, LABEL_COLOR, false);
    }

    protected void drawBasePanel(GuiGraphics guiGraphics, int left, int top) {
        guiGraphics.fill(left, top, left + imageWidth, top + imageHeight, PANEL_OUTER);
        guiGraphics.fill(left + 1, top + 1, left + imageWidth - 1, top + imageHeight - 1, PANEL_INNER);
        guiGraphics.fill(left + 2, top + 2, left + imageWidth - 2, top + imageHeight - 2, PANEL_INNER);
        guiGraphics.fill(left + 4, top + 74, left + imageWidth - 4, top + 76, PANEL_OUTER);
        guiGraphics.fill(left + 4, top + 76, left + imageWidth - 4, top + 77, PANEL_SHADOW);
    }

    protected void drawInfoBadge(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y, x + 12, y + 12, 0xFF0B6A97);
        guiGraphics.fill(x + 1, y + 1, x + 11, y + 11, 0xFF1D95D2);
        guiGraphics.drawString(font, "i", x + 4, y + 2, 0xFFFFFFFF, false);
    }

    protected void drawBookSlot(GuiGraphics guiGraphics, int x, int y) {
        drawSlot(guiGraphics, x, y);
        guiGraphics.renderItem(new ItemStack(Items.BOOK), x + 1, y + 1);
    }

    protected void drawSlot(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y, x + 18, y + 18, SLOT_OUTER);
        guiGraphics.fill(x + 1, y + 1, x + 17, y + 17, SLOT_LIGHT);
        guiGraphics.fill(x + 2, y + 2, x + 16, y + 16, SLOT_INNER);
    }

    protected void drawPlayerInventory(GuiGraphics guiGraphics, int left, int top) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlot(guiGraphics, left + 7 + column * 18, top + 83 + row * 18);
            }
        }

        for (int column = 0; column < 9; column++) {
            drawSlot(guiGraphics, left + 7 + column * 18, top + 141);
        }
    }

    protected void drawArrow(GuiGraphics guiGraphics, int x, int y, int width, int progress) {
        guiGraphics.fill(x, y + 5, x + width - 6, y + 9, PANEL_OUTER);
        guiGraphics.fill(x + width - 8, y + 2, x + width, y + 12, PANEL_OUTER);

        if (progress > 0) {
            guiGraphics.fill(x + 1, y + 6, x + 1 + Math.max(0, width - 8) * progress / 24, y + 8, 0xFFDDDDDD);
            guiGraphics.fill(x + width - 7, y + 3, x + width - 1, y + 11, 0xFFDDDDDD);
        }
    }

    protected void drawFlame(GuiGraphics guiGraphics, int x, int y, int height) {
        if (height <= 0) {
            return;
        }

        int top = y + 13 - height;
        guiGraphics.fill(x + 4, top, x + 8, y + 13, 0xFFFFAA00);
        guiGraphics.fill(x + 2, top + 2, x + 10, y + 13, 0xFFFF5A00);
    }
}
