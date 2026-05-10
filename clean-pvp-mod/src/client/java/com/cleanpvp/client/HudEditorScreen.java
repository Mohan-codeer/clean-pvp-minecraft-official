package com.cleanpvp.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class HudEditorScreen extends Screen {
	private HudWidget draggingWidget;
	private int dragOffsetX;
	private int dragOffsetY;

	protected HudEditorScreen() {
		super(Component.literal("CleanPVP HUD Editor"));
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		this.extractTransparentBackground(graphics);
		graphics.text(font, "CleanPVP HUD Editor", 10, 10, 0xFFFFFFFF, true);
		graphics.text(font, "Left click + drag widgets. Click toggles on right. Press ESC to save.", 10, 24, 0xFFBFBFBF, true);

		renderTopControls(graphics, mouseX, mouseY);
		renderTogglePanel(graphics, mouseX, mouseY);
		renderWidgets(graphics, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean dblClick) {
		if (event.button() == 0 && handleTopControlClick(event.x(), event.y())) {
			return true;
		}
		if (event.button() == 0 && handleToggleClick(event.x(), event.y())) {
			return true;
		}

		if (event.button() == 0) {
			Minecraft client = Minecraft.getInstance();
			for (HudWidget widget : HudWidget.values()) {
				HudManager.WidgetBounds bounds = HudManager.getWidgetBounds(client, widget);
				if (bounds.contains(event.x(), event.y())) {
					draggingWidget = widget;
					dragOffsetX = (int) event.x() - bounds.x();
					dragOffsetY = (int) event.y() - bounds.y();
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (event.button() == 0 && draggingWidget != null) {
			draggingWidget = null;
			HudManager.persist();
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
		if (event.button() == 0 && draggingWidget != null) {
			int newX = (int) event.x() - dragOffsetX;
			int newY = (int) event.y() - dragOffsetY;
			HudManager.setWidgetPosition(draggingWidget, newX, newY, width, height);
			return true;
		}
		return false;
	}

	@Override
	public void onClose() {
		HudManager.persist();
		super.onClose();
	}

	private void renderWidgets(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		Minecraft client = Minecraft.getInstance();
		HudConfig config = HudManager.getConfig();

		for (HudWidget widget : HudWidget.values()) {
			HudConfig.WidgetSettings settings = config.getWidgetSettings(widget);
			if (!settings.enabled) {
				continue;
			}
			HudManager.WidgetBounds bounds = HudManager.getWidgetBounds(client, widget);
			HudManager.renderWidget(graphics, client, widget, bounds.x(), bounds.y(), true);
			graphics.text(font, widget.getTitle(), bounds.x() + 4, bounds.y() - 10, 0xFFD6D6D6, true);
			if (bounds.contains(mouseX, mouseY)) {
				graphics.fill(bounds.x(), bounds.y(), bounds.x() + bounds.width(), bounds.y() + bounds.height(), 0x22000000);
			}
		}
	}

	private void renderTopControls(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		int y = 42;
		int x = 10;
		x = drawTopButtonFixed(graphics, x, y, 140, topColorModeText(), isInside(mouseX, mouseY, x, y, 140, 16));
		x += 6;
		x = drawTopButtonFixed(graphics, x, y, 130, topFillModeText(), isInside(mouseX, mouseY, x, y, 130, 16));
		x += 6;
		x = drawTopButtonFixed(graphics, x, y, 96, "Solid Color", isInside(mouseX, mouseY, x, y, 96, 16));
		int colorPreviewX = x - 14;
		graphics.fill(colorPreviewX, y + 3, colorPreviewX + 10, y + 13, HudManager.getConfig().solidColor);
		x += 6;
		drawTopButtonFixed(graphics, x, y, 138, keySizeText(), isInside(mouseX, mouseY, x, y, 138, 16));
	}

	private int drawTopButtonFixed(GuiGraphicsExtractor graphics, int x, int y, int buttonWidth, String text, boolean hovered) {
		int color = hovered ? 0x883A3A3A : 0x66272727;
		graphics.fill(x, y, x + buttonWidth, y + 16, color);
		graphics.outline(x, y, buttonWidth, 16, 0xFFFFFFFF);
		graphics.text(font, text, x + 4, y + 4, 0xFFFFFFFF, true);
		return x + buttonWidth;
	}

	private boolean handleTopControlClick(double mouseX, double mouseY) {
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

	private void renderTogglePanel(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		int panelWidth = 160;
		int panelX = width - panelWidth - 8;
		int panelY = 42;
		int panelHeight = 16 + HudWidget.values().length * 18;

		graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0x661A1A1A);
		graphics.outline(panelX, panelY, panelWidth, panelHeight, 0xFFFFFFFF);
		graphics.text(font, "Widgets", panelX + 6, panelY + 5, 0xFFFFFFFF, true);

		HudConfig config = HudManager.getConfig();
		for (int i = 0; i < HudWidget.values().length; i++) {
			HudWidget widget = HudWidget.values()[i];
			int rowY = panelY + 18 + i * 18;
			boolean enabled = config.getWidgetSettings(widget).enabled;
			int rowColor = isInside(mouseX, mouseY, panelX + 4, rowY, panelWidth - 8, 14) ? 0x55353535 : 0x33242424;
			graphics.fill(panelX + 4, rowY, panelX + panelWidth - 4, rowY + 14, rowColor);
			graphics.text(font, widget.getTitle(), panelX + 8, rowY + 3, 0xFFFFFFFF, true);
			graphics.text(font, enabled ? "ON" : "OFF", panelX + panelWidth - 26, rowY + 3, enabled ? 0xFF78FF78 : 0xFFFF7878, true);
		}
	}

	private boolean handleToggleClick(double mouseX, double mouseY) {
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
