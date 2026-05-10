package com.cleanpvp.client;

import com.cleanpvp.CleanPVP;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class HudManager {
	private static final int TEXT_COLOR = 0xFFFFFFFF;
	private static final Identifier HUD_ID = Identifier.fromNamespaceAndPath(CleanPVP.MOD_ID, "hud_layer");
	private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(CleanPVP.MOD_ID, "general"));
	private static HudConfig config;
	private static KeyMapping openEditorKey;
	private static final ClickTracker clickTracker = new ClickTracker();

	private HudManager() {
	}

	public static void initialize() {
		config = HudConfigManager.load();
		openEditorKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.cleanpvp.open_editor",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_RIGHT_SHIFT,
			KEY_CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			clickTracker.tick(client);
			while (openEditorKey.consumeClick()) {
				client.setScreen(new HudEditorScreen());
			}
		});

		HudElementRegistry.attachElementAfter(VanillaHudElements.BOSS_BAR, HUD_ID, (graphics, deltaTracker) -> renderHud(graphics));
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> HudConfigManager.save(config));
	}

	private static void renderHud(GuiGraphicsExtractor graphics) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.options.hideGui) {
			return;
		}

		for (HudWidget widget : HudWidget.values()) {
			HudConfig.WidgetSettings settings = config.getWidgetSettings(widget);
			if (!settings.enabled) {
				continue;
			}
			WidgetBounds bounds = getWidgetBounds(client, widget);
			renderWidget(graphics, client, widget, bounds.x, bounds.y, false);
		}
	}

	public static HudConfig getConfig() {
		return config;
	}

	public static void persist() {
		HudConfigManager.save(config);
	}

	public static int getLeftCps() {
		return clickTracker.getLeftCps();
	}

	public static int getRightCps() {
		return clickTracker.getRightCps();
	}

	public static WidgetBounds getWidgetBounds(Minecraft client, HudWidget widget) {
		int scaledWidth = client.getWindow().getGuiScaledWidth();
		int scaledHeight = client.getWindow().getGuiScaledHeight();
		HudConfig.WidgetSettings settings = config.getWidgetSettings(widget);
		int x = (int) Math.round(settings.xNorm * scaledWidth);
		int y = (int) Math.round(settings.yNorm * scaledHeight);
		int width = getWidgetWidth(widget, client);
		int height = getWidgetHeight(widget, client);
		return new WidgetBounds(x, y, width, height);
	}

	public static void setWidgetPosition(HudWidget widget, int x, int y, int screenWidth, int screenHeight) {
		HudConfig.WidgetSettings settings = config.getWidgetSettings(widget);
		settings.xNorm = clampNorm((double) x / Math.max(1, screenWidth));
		settings.yNorm = clampNorm((double) y / Math.max(1, screenHeight));
	}

	public static void renderWidget(GuiGraphicsExtractor graphics, Minecraft client, HudWidget widget, int x, int y, boolean inEditor) {
		int width = getWidgetWidth(widget, client);
		int height = getWidgetHeight(widget, client);
		int outlineColor = currentOutlineColor();
		int fillColor = config.fillMode == HudConfig.FillMode.SOLID_BOX ? applyAlpha(outlineColor, 0x44) : 0x00000000;
		drawPanel(graphics, x, y, width, height, fillColor, outlineColor, inEditor);

		switch (widget) {
			case KEYSTROKES -> renderKeystrokes(graphics, client, x, y);
			case FPS -> graphics.text(client.font, "FPS: " + client.getFps(), x + 4, y + 5, TEXT_COLOR, true);
			case CPS -> graphics.text(client.font, "CPS L/R: " + getLeftCps() + " / " + getRightCps(), x + 4, y + 5, TEXT_COLOR, true);
			case ARMOR -> renderArmor(graphics, client, x, y);
			case POTIONS -> renderPotions(graphics, client, x, y);
		}
	}

	public static void cycleColorMode() {
		config.colorMode = config.colorMode == HudConfig.ColorMode.SOLID ? HudConfig.ColorMode.RAINBOW : HudConfig.ColorMode.SOLID;
	}

	public static void cycleFillMode() {
		config.fillMode = config.fillMode == HudConfig.FillMode.OUTLINE_ONLY ? HudConfig.FillMode.SOLID_BOX : HudConfig.FillMode.OUTLINE_ONLY;
	}

	public static void cycleSolidColor() {
		int[] presets = {
			0xFF00B7FF,
			0xFFFF4D4D,
			0xFF7BFF6A,
			0xFFFFD95E,
			0xFFC48BFF,
			0xFFFFFFFF
		};
		int nextIndex = 0;
		for (int i = 0; i < presets.length; i++) {
			if (presets[i] == config.solidColor) {
				nextIndex = (i + 1) % presets.length;
				break;
			}
		}
		config.solidColor = presets[nextIndex];
	}

	public static void cycleKeystrokesScale() {
		config.keystrokesScale = config.keystrokesScale.next();
	}

	private static void renderKeystrokes(GuiGraphicsExtractor graphics, Minecraft client, int x, int y) {
		float scale = config.keystrokesScale.factor();
		int keyWidth = scaled(16, scale);
		int keyHeight = scaled(14, scale);
		int gap = Math.max(1, scaled(2, scale));
		int margin = scaled(3, scale);
		int topPadding = scaled(4, scale);

		drawKey(graphics, client, "W", x + keyWidth + gap + margin, y + topPadding, keyWidth, keyHeight, client.options.keyUp.isDown());
		drawKey(graphics, client, "A", x + margin, y + keyHeight + gap + topPadding, keyWidth, keyHeight, client.options.keyLeft.isDown());
		drawKey(graphics, client, "S", x + keyWidth + gap + margin, y + keyHeight + gap + topPadding, keyWidth, keyHeight, client.options.keyDown.isDown());
		drawKey(graphics, client, "D", x + (keyWidth + gap) * 2 + margin, y + keyHeight + gap + topPadding, keyWidth, keyHeight, client.options.keyRight.isDown());

		String left = "L:" + getLeftCps();
		String right = "R:" + getRightCps();
		int mouseY = y + (keyHeight + gap) * 2 + scaled(6, scale);
		int mouseWidth = scaled(28, scale);
		drawKey(graphics, client, left, x + margin, mouseY, mouseWidth, keyHeight, client.options.keyAttack.isDown());
		drawKey(graphics, client, right, x + mouseWidth + gap + scaled(5, scale), mouseY, mouseWidth, keyHeight, client.options.keyUse.isDown());
	}

	private static void drawKey(GuiGraphicsExtractor graphics, Minecraft client, String label, int x, int y, int width, int height, boolean pressed) {
		int outlineColor = currentOutlineColor();
		int fillColor = pressed ? applyAlpha(outlineColor, 0x77) : 0x44000000;
		graphics.fill(x, y, x + width, y + height, fillColor);
		graphics.outline(x, y, width, height, outlineColor);
		int textWidth = client.font.width(label);
		graphics.text(client.font, label, x + Math.max(2, (width - textWidth) / 2), y + 4, TEXT_COLOR, true);
	}

	private static void renderArmor(GuiGraphicsExtractor graphics, Minecraft client, int x, int y) {
		EquipmentSlot[] armorOrder = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
		int lineY = y + 4;

		for (EquipmentSlot slot : armorOrder) {
			ItemStack stack = client.player.getItemBySlot(slot);
			graphics.item(stack, x + 4, lineY - 2);
			String text = stack.isEmpty() ? "Empty" : durabilityText(stack);
			graphics.text(client.font, text, x + 24, lineY + 2, TEXT_COLOR, true);
			lineY += 18;
		}
	}

	private static void renderPotions(GuiGraphicsExtractor graphics, Minecraft client, int x, int y) {
		List<MobEffectInstance> effects = new ArrayList<>(client.player.getActiveEffects());
		effects.sort(Comparator.comparingInt(MobEffectInstance::getDuration).reversed());
		int lineY = y + 4;
		if (effects.isEmpty()) {
			graphics.text(client.font, "No effects", x + 4, lineY, TEXT_COLOR, true);
			return;
		}

		int maxLines = Math.min(6, effects.size());
		for (int i = 0; i < maxLines; i++) {
			MobEffectInstance effect = effects.get(i);
			Component name = effect.getEffect().value().getDisplayName();
			String level = effect.getAmplifier() > 0 ? " " + (effect.getAmplifier() + 1) : "";
			String duration = formatDuration(effect);
			graphics.text(client.font, "\u2697 " + name.getString() + level + " " + duration, x + 4, lineY, TEXT_COLOR, true);
			lineY += 10;
		}
	}

	private static int getWidgetWidth(HudWidget widget, Minecraft client) {
		return switch (widget) {
			case KEYSTROKES -> scaled(66, config.keystrokesScale.factor());
			case FPS -> Math.max(65, client.font.width("FPS: 9999") + 8);
			case CPS -> Math.max(95, client.font.width("CPS L/R: 99 / 99") + 8);
			case ARMOR -> 92;
			case POTIONS -> 170;
		};
	}

	private static int getWidgetHeight(HudWidget widget, Minecraft client) {
		return switch (widget) {
			case KEYSTROKES -> scaled(52, config.keystrokesScale.factor());
			case FPS -> 18;
			case CPS -> 18;
			case ARMOR -> 76;
			case POTIONS -> Math.max(18, 10 * Math.min(6, client.player == null ? 1 : Math.max(1, client.player.getActiveEffects().size())) + 8);
		};
	}

	private static int currentOutlineColor() {
		if (config.colorMode == HudConfig.ColorMode.SOLID) {
			return config.solidColor;
		}
		float hue = (System.currentTimeMillis() % 5000L) / 5000f;
		return 0xFF000000 | Color.HSBtoRGB(hue, 0.95f, 1.0f) & 0x00FFFFFF;
	}

	private static String durabilityText(ItemStack stack) {
		if (!stack.isDamageableItem()) {
			return stack.getHoverName().getString();
		}
		int max = stack.getMaxDamage();
		int current = max - stack.getDamageValue();
		return current + "/" + max;
	}

	private static String formatDuration(MobEffectInstance effect) {
		if (effect.isInfiniteDuration()) {
			return "INF";
		}
		int totalSeconds = effect.getDuration() / 20;
		int minutes = totalSeconds / 60;
		int seconds = totalSeconds % 60;
		return String.format("%d:%02d", minutes, seconds);
	}

	private static int applyAlpha(int color, int alpha) {
		return (color & 0x00FFFFFF) | (alpha << 24);
	}

	private static int scaled(int value, float scale) {
		return Math.max(1, Math.round(value * scale));
	}

	private static double clampNorm(double value) {
		return Math.max(0.0, Math.min(1.0, value));
	}

	private static void drawPanel(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int fillColor, int outlineColor, boolean inEditor) {
		if (fillColor != 0) {
			graphics.fill(x, y, x + width, y + height, fillColor);
		}
		graphics.outline(x, y, width, height, outlineColor);
		if (inEditor) {
			graphics.outline(x - 1, y - 1, width + 2, height + 2, 0x88FFFFFF);
		}
	}

	public record WidgetBounds(int x, int y, int width, int height) {
		public boolean contains(double px, double py) {
			return px >= x && px <= x + width && py >= y && py <= y + height;
		}
	}
}
