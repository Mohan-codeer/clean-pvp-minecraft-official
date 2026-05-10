package com.cleanpvp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.List;

public class ThemeManager {
    public static void initialize() {
        MinecraftForge.EVENT_BUS.register(new ThemeManager());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        GuiScreen screen = event.gui;
        if (screen == null) return;

        Minecraft mc = Minecraft.getMinecraft();

        // 1. Cover Panorama and Logo on Main Menu
        if (screen instanceof GuiMainMenu) {
            drawGradientRect(0, 0, screen.width, screen.height, CleanPvpTheme.TOP_GRADIENT, CleanPvpTheme.BOTTOM_GRADIENT);
            drawCustomTitle(screen);
        }

        // 2. Render Modern Buttons over vanilla ones (to avoid breaking field references)
        try {
            List<GuiButton> buttonList = ReflectionHelper.getPrivateValue(GuiScreen.class, screen, "buttonList", "field_146292_n");
            for (GuiButton button : buttonList) {
                if (button.visible) {
                    drawModernButton(mc, button, event.mouseX, event.mouseY);
                }
            }
        } catch (Exception e) {
            // Fallback if reflection fails
        }
    }

    private void drawCustomTitle(GuiScreen screen) {
        String title = "CLEAN PVP";
        float scale = 3.2f;
        int textWidth = screen.mc.fontRendererObj.getStringWidth(title);

        GlStateManager.pushMatrix();
        GlStateManager.translate(screen.width / 2f, 18f, 0);
        GlStateManager.scale(scale, scale, 1.0f);
        screen.mc.fontRendererObj.drawStringWithShadow(title, -textWidth / 2, 0, 0xFFFFFFFF);
        GlStateManager.popMatrix();
    }

    private void drawModernButton(Minecraft mc, GuiButton button, int mouseX, int mouseY) {
        boolean hovered = mouseX >= button.xPosition && mouseY >= button.yPosition && mouseX < button.xPosition + button.width && mouseY < button.yPosition + button.height;
        
        int top = hovered ? CleanPvpTheme.BUTTON_HOVER_TOP : CleanPvpTheme.BUTTON_TOP;
        int bottom = hovered ? CleanPvpTheme.BUTTON_HOVER_BOTTOM : CleanPvpTheme.BUTTON_BOTTOM;
        
        if (!button.enabled) {
            top = 0xFF3A5A4A;
            bottom = 0xFF2A4C66;
        }

        // Draw the modern background over the vanilla one
        drawGradientRect(button.xPosition, button.yPosition, button.xPosition + button.width, button.yPosition + button.height, top, bottom);
        
        // Draw the outline
        HudManager.drawOutline(button.xPosition, button.yPosition, button.width, button.height, CleanPvpTheme.BUTTON_BORDER);
        
        // Re-draw the text on top of our new background
        int textColor = 0xE0E0E0;
        if (!button.enabled) {
            textColor = 0xA0A0A0;
        } else if (hovered) {
            textColor = 0xFFFFA0;
        }

        String text = button.displayString;
        mc.fontRendererObj.drawStringWithShadow(text, button.xPosition + button.width / 2 - mc.fontRendererObj.getStringWidth(text) / 2, button.yPosition + (button.height - 8) / 2, textColor);
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double)right, (double)top, 0.0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)top, 0.0).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos((double)left, (double)bottom, 0.0).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos((double)right, (double)bottom, 0.0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
