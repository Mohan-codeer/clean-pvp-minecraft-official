package com.cleanpvp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class HudManager {
    private static HudConfig config;
    private static KeyBinding openEditorKey;
    private static final ClickTracker clickTracker = new ClickTracker();

    private HudManager() {
    }

    public static void initialize() {
        config = HudConfigManager.load();
        openEditorKey = new KeyBinding("key.cleanpvp.open_editor", Keyboard.KEY_RSHIFT, "CleanPVP");
        ClientRegistry.registerKeyBinding(openEditorKey);
        MinecraftForge.EVENT_BUS.register(new HudManager());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft client = Minecraft.getMinecraft();
            clickTracker.tick(client);
            if (openEditorKey.isPressed()) {
                client.displayGuiScreen(new HudEditorScreen());
            }
        }
    }

    @SubscribeEvent
    public void onRenderHud(RenderGameOverlayEvent.Text event) {
        Minecraft client = Minecraft.getMinecraft();
        if (client.thePlayer == null || client.gameSettings.hideGUI || client.currentScreen instanceof HudEditorScreen) {
            return;
        }

        for (HudWidget widget : HudWidget.values()) {
            HudConfig.WidgetSettings settings = config.getWidgetSettings(widget);
            if (settings != null && settings.enabled) {
                WidgetBounds bounds = getWidgetBounds(client, widget);
                renderWidget(client, widget, bounds.x, bounds.y, false);
            }
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
        ScaledResolution sr = new ScaledResolution(client);
        HudConfig.WidgetSettings settings = config.getWidgetSettings(widget);
        int x = (int) (settings.xNorm * sr.getScaledWidth());
        int y = (int) (settings.yNorm * sr.getScaledHeight());
        return new WidgetBounds(x, y, getWidgetWidth(widget, client), getWidgetHeight(widget, client));
    }

    public static void setWidgetPosition(HudWidget widget, int x, int y, int screenWidth, int screenHeight) {
        HudConfig.WidgetSettings settings = config.getWidgetSettings(widget);
        settings.xNorm = (double) x / Math.max(1, screenWidth);
        settings.yNorm = (double) y / Math.max(1, screenHeight);
    }

    public static void renderWidget(Minecraft client, HudWidget widget, int x, int y, boolean inEditor) {
        int width = getWidgetWidth(widget, client);
        int height = getWidgetHeight(widget, client);
        int color = currentOutlineColor();
        
        // SIMPLE BACKGROUND
        if (config.fillMode == HudConfig.FillMode.SOLID_BOX) {
            Gui.drawRect(x, y, x + width, y + height, (color & 0x00FFFFFF) | 0x44000000);
        }
        
        // SIMPLE OUTLINE
        Gui.drawRect(x, y, x + width, y + 1, color);
        Gui.drawRect(x, y + height - 1, x + width, y + height, color);
        Gui.drawRect(x, y, x + 1, y + height, color);
        Gui.drawRect(x + width - 1, y, x + width, y + height, color);

        if (inEditor) {
            // Extra white outline in editor
            drawOutline(x - 1, y - 1, width + 2, height + 2, 0x88FFFFFF);
        }

        FontRenderer fr = client.fontRendererObj;
        switch (widget) {
            case KEYSTROKES:
                renderKeystrokes(client, x, y);
                break;
            case FPS:
                fr.drawStringWithShadow("FPS: " + Minecraft.getDebugFPS(), x + 4, y + 5, 0xFFFFFFFF);
                break;
            case CPS:
                fr.drawStringWithShadow("CPS: " + getLeftCps() + " | " + getRightCps(), x + 4, y + 5, 0xFFFFFFFF);
                break;
            case ARMOR:
                renderArmor(client, x, y);
                break;
            case POTIONS:
                renderPotions(client, x, y);
                break;
        }
    }

    private static void renderKeystrokes(Minecraft client, int x, int y) {
        float scale = config.keystrokesScale.factor();
        int kw = (int)(16 * scale);
        int kh = (int)(14 * scale);
        int gap = (int)(2 * scale);

        drawSimpleKey(client, "W", x + kw + gap + 3, y + 4, kw, kh, client.gameSettings.keyBindForward.isKeyDown());
        drawSimpleKey(client, "A", x + 3, y + kh + gap + 4, kw, kh, client.gameSettings.keyBindLeft.isKeyDown());
        drawSimpleKey(client, "S", x + kw + gap + 3, y + kh + gap + 4, kw, kh, client.gameSettings.keyBindBack.isKeyDown());
        drawSimpleKey(client, "D", x + (kw + gap) * 2 + 3, y + kh + gap + 4, kw, kh, client.gameSettings.keyBindRight.isKeyDown());
    }

    private static void drawSimpleKey(Minecraft client, String text, int x, int y, int w, int h, boolean down) {
        int color = currentOutlineColor();
        Gui.drawRect(x, y, x + w, y + h, down ? (color & 0x00FFFFFF) | 0x77000000 : 0x44000000);
        drawOutline(x, y, w, h, color);
        client.fontRendererObj.drawStringWithShadow(text, x + w/2 - client.fontRendererObj.getStringWidth(text)/2, y + h/2 - 4, 0xFFFFFFFF);
    }

    private static void renderArmor(Minecraft client, int x, int y) {
        int ly = y + 4;
        for (int i = 3; i >= 0; i--) {
            ItemStack s = client.thePlayer.inventory.armorInventory[i];
            if (s != null) {
                RenderHelper.enableGUIStandardItemLighting();
                client.getRenderItem().renderItemAndEffectIntoGUI(s, x + 4, ly - 2);
                RenderHelper.disableStandardItemLighting();
                client.fontRendererObj.drawStringWithShadow(durabilityText(s), x + 24, ly + 4, 0xFFFFFFFF);
            } else {
                client.fontRendererObj.drawStringWithShadow("None", x + 24, ly + 4, 0xFFFFFFFF);
            }
            ly += 18;
        }
    }

    private static void renderPotions(Minecraft client, int x, int y) {
        int ly = y + 4;
        Collection<PotionEffect> effects = client.thePlayer.getActivePotionEffects();
        if (effects.isEmpty()) {
            client.fontRendererObj.drawStringWithShadow("No Effects", x + 4, ly, 0xFFFFFFFF);
            return;
        }
        for (PotionEffect e : effects) {
            String s = StatCollector.translateToLocal(e.getEffectName()) + " " + Potion.getDurationString(e);
            client.fontRendererObj.drawStringWithShadow(s, x + 4, ly, 0xFFFFFFFF);
            ly += 10;
        }
    }

    public static int getWidgetWidth(HudWidget widget, Minecraft client) {
        switch (widget) {
            case KEYSTROKES: return (int)(66 * config.keystrokesScale.factor());
            case FPS: return 70;
            case CPS: return 90;
            case ARMOR: return 92;
            case POTIONS: return 150;
            default: return 0;
        }
    }

    public static int getWidgetHeight(HudWidget widget, Minecraft client) {
        switch (widget) {
            case KEYSTROKES: return (int)(52 * config.keystrokesScale.factor());
            case FPS: return 18;
            case CPS: return 18;
            case ARMOR: return 76;
            case POTIONS: return 70;
            default: return 0;
        }
    }

    private static int currentOutlineColor() {
        if (config.colorMode == HudConfig.ColorMode.RAINBOW) {
            return Color.HSBtoRGB((System.currentTimeMillis() % 5000L) / 5000f, 0.8f, 1f);
        }
        return config.solidColor;
    }

    private static String durabilityText(ItemStack stack) {
        if (!stack.isItemStackDamageable()) return stack.getDisplayName();
        return (stack.getMaxDamage() - stack.getItemDamage()) + "/" + stack.getMaxDamage();
    }

    public static void drawOutline(int x, int y, int w, int h, int color) {
        Gui.drawRect(x, y, x + w, y + 1, color);
        Gui.drawRect(x, y + h - 1, x + w, y + h, color);
        Gui.drawRect(x, y, x + 1, y + h, color);
        Gui.drawRect(x + w - 1, y, x + w, y + h, color);
    }

    public static void cycleColorMode() { config.colorMode = config.colorMode == HudConfig.ColorMode.SOLID ? HudConfig.ColorMode.RAINBOW : HudConfig.ColorMode.SOLID; }
    public static void cycleFillMode() { config.fillMode = config.fillMode == HudConfig.FillMode.OUTLINE_ONLY ? HudConfig.FillMode.SOLID_BOX : HudConfig.FillMode.OUTLINE_ONLY; }
    public static void cycleSolidColor() {
        int[] p = {0xFF00B7FF, 0xFFFF4D4D, 0xFF7BFF6A, 0xFFFFD95E, 0xFFC48BFF, 0xFFFFFFFF};
        int idx = 0;
        for (int i=0; i<p.length; i++) if (p[i] == config.solidColor) { idx = (i+1)%p.length; break; }
        config.solidColor = p[idx];
    }
    public static void cycleKeystrokesScale() { config.keystrokesScale = config.keystrokesScale.next(); }

    public static class WidgetBounds {
        public final int x, y, width, height;
        public WidgetBounds(int x, int y, int w, int h) { this.x = x; this.y = y; this.width = w; this.height = h; }
        public boolean contains(double px, double py) { return px >= x && px <= x + width && py >= y && py <= y + height; }
    }
}
