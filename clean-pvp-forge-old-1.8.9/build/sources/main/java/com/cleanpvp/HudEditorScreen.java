package com.cleanpvp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class HudEditorScreen extends GuiScreen {
    private HudWidget draggingWidget;
    private int dragOffsetX;
    private int dragOffsetY;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Draw a dark transparent background for the editor
        this.drawGradientRect(0, 0, width, height, 0x99101010, 0x99101010);
        
        fontRendererObj.drawStringWithShadow("CleanPVP HUD Editor", 10, 10, 0xFFFFFFFF);
        fontRendererObj.drawStringWithShadow("Left click + drag widgets. Click toggles on right. Press ESC to save.", 10, 24, 0xFFBFBFBF);

        try {
            renderTopControls(mouseX, mouseY);
            renderTogglePanel(mouseX, mouseY);
            renderWidgets(mouseX, mouseY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0 && handleTopControlClick(mouseX, mouseY)) {
            return;
        }
        if (mouseButton == 0 && handleToggleClick(mouseX, mouseY)) {
            return;
        }

        if (mouseButton == 0) {
            Minecraft client = Minecraft.getMinecraft();
            for (HudWidget widget : HudWidget.values()) {
                HudManager.WidgetBounds bounds = HudManager.getWidgetBounds(client, widget);
                if (bounds.contains(mouseX, mouseY)) {
                    draggingWidget = widget;
                    dragOffsetX = mouseX - bounds.x;
                    dragOffsetY = mouseY - bounds.y;
                    return;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0 && draggingWidget != null) {
            draggingWidget = null;
            HudManager.persist();
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (clickedMouseButton == 0 && draggingWidget != null) {
            int newX = mouseX - dragOffsetX;
            int newY = mouseY - dragOffsetY;
            HudManager.setWidgetPosition(draggingWidget, newX, newY, width, height);
        }
    }

    @Override
    public void onGuiClosed() {
        HudManager.persist();
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void renderWidgets(int mouseX, int mouseY) {
        Minecraft client = Minecraft.getMinecraft();
        HudConfig config = HudManager.getConfig();

        for (HudWidget widget : HudWidget.values()) {
            HudConfig.WidgetSettings settings = config.getWidgetSettings(widget);
            if (!settings.enabled) {
                continue;
            }
            HudManager.WidgetBounds bounds = HudManager.getWidgetBounds(client, widget);
            HudManager.renderWidget(client, widget, bounds.x, bounds.y, true);
            fontRendererObj.drawStringWithShadow(widget.getTitle(), bounds.x + 4, bounds.y - 10, 0xFFD6D6D6);
            if (bounds.contains(mouseX, mouseY)) {
                Gui.drawRect(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, 0x22000000);
            }
        }
    }

    private void renderTopControls(int mouseX, int mouseY) {
        int y = 42;
        int x = 10;
        
        // Color Mode Button
        x = drawTopButtonFixed(x, y, 140, topColorModeText(), isInside(mouseX, mouseY, x, y, 140, 16));
        x += 6;
        
        // Fill Mode Button
        x = drawTopButtonFixed(x, y, 130, topFillModeText(), isInside(mouseX, mouseY, x, y, 130, 16));
        x += 6;
        
        // Solid Color Picker Button
        x = drawTopButtonFixed(x, y, 96, "Solid Color", isInside(mouseX, mouseY, x, y, 96, 16));
        int colorPreviewX = x - 14;
        Gui.drawRect(colorPreviewX, y + 3, colorPreviewX + 10, y + 13, HudManager.getConfig().solidColor);
        x += 6;
        
        // Key Size Button
        drawTopButtonFixed(x, y, 138, keySizeText(), isInside(mouseX, mouseY, x, y, 138, 16));
    }

    private int drawTopButtonFixed(int x, int y, int buttonWidth, String text, boolean hovered) {
        int color = hovered ? 0x883A3A3A : 0x66272727;
        Gui.drawRect(x, y, x + buttonWidth, y + 16, color);
        HudManager.drawOutline(x, y, buttonWidth, 16, 0xFFFFFFFF);
        fontRendererObj.drawStringWithShadow(text, x + 4, y + 4, 0xFFFFFFFF);
        return x + buttonWidth;
    }

    private boolean handleTopControlClick(int mouseX, int mouseY) {
        int x = 10;
        int y = 42;
        int firstWidth = 140;
        int secondWidth = 130;
        int thirdWidth = 96;
        int fourthWidth = 138;

        if (isInside(mouseX, mouseY, x, y, firstWidth, 16)) {
            HudManager.cycleColorMode();
            HudManager.persist();
            return true;
        }
        x += firstWidth + 6;
        if (isInside(mouseX, mouseY, x, y, secondWidth, 16)) {
            HudManager.cycleFillMode();
            HudManager.persist();
            return true;
        }
        x += secondWidth + 6;
        if (isInside(mouseX, mouseY, x, y, thirdWidth, 16)) {
            HudManager.cycleSolidColor();
            HudManager.persist();
            return true;
        }
        x += thirdWidth + 6;
        if (isInside(mouseX, mouseY, x, y, fourthWidth, 16)) {
            HudManager.cycleKeystrokesScale();
            HudManager.persist();
            return true;
        }
        return false;
    }

    private void renderTogglePanel(int mouseX, int mouseY) {
        int panelWidth = 160;
        int panelX = width - panelWidth - 8;
        int panelY = 42;
        int panelHeight = 16 + HudWidget.values().length * 18;

        Gui.drawRect(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0x661A1A1A);
        HudManager.drawOutline(panelX, panelY, panelWidth, panelHeight, 0xFFFFFFFF);
        fontRendererObj.drawStringWithShadow("Widgets", panelX + 6, panelY + 5, 0xFFFFFFFF);

        HudConfig config = HudManager.getConfig();
        for (int i = 0; i < HudWidget.values().length; i++) {
            HudWidget widget = HudWidget.values()[i];
            int rowY = panelY + 18 + i * 18;
            boolean enabled = config.getWidgetSettings(widget).enabled;
            int rowColor = isInside(mouseX, mouseY, panelX + 4, rowY, panelWidth - 8, 14) ? 0x55353535 : 0x33242424;
            Gui.drawRect(panelX + 4, rowY, panelX + panelWidth - 4, rowY + 14, rowColor);
            fontRendererObj.drawStringWithShadow(widget.getTitle(), panelX + 8, rowY + 3, 0xFFFFFFFF);
            fontRendererObj.drawStringWithShadow(enabled ? "ON" : "OFF", panelX + panelWidth - 26, rowY + 3, enabled ? 0xFF78FF78 : 0xFFFF7878);
        }
    }

    private boolean handleToggleClick(int mouseX, int mouseY) {
        int panelWidth = 160;
        int panelX = width - panelWidth - 8;
        int panelY = 42;
        HudConfig config = HudManager.getConfig();

        for (int i = 0; i < HudWidget.values().length; i++) {
            HudWidget widget = HudWidget.values()[i];
            int rowY = panelY + 18 + i * 18;
            if (isInside(mouseX, mouseY, panelX + 4, rowY, panelWidth - 8, 14)) {
                HudConfig.WidgetSettings settings = config.getWidgetSettings(widget);
                settings.enabled = !settings.enabled;
                HudManager.persist();
                return true;
            }
        }
        return false;
    }

    private String topColorModeText() {
        return "Color: " + HudManager.getConfig().colorMode.name();
    }

    private String topFillModeText() {
        return "Fill: " + HudManager.getConfig().fillMode.name();
    }

    private String keySizeText() {
        return "Key Size: " + HudManager.getConfig().keystrokesScale.name();
    }

    private boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
