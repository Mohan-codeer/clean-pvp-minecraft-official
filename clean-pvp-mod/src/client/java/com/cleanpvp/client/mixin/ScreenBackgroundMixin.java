package com.cleanpvp.client.mixin;

import com.cleanpvp.client.CleanPvpTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenBackgroundMixin {
	@Shadow
	public int width;
	@Shadow
	public int height;

	@Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
	private void cleanpvp$replaceBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		graphics.fillGradient(0, 0, width, height, CleanPvpTheme.TOP_GRADIENT, CleanPvpTheme.BOTTOM_GRADIENT);
		ci.cancel();
	}

	@Inject(method = "extractTransparentBackground", at = @At("HEAD"), cancellable = true)
	private void cleanpvp$replaceTransparentBackground(GuiGraphicsExtractor graphics, CallbackInfo ci) {
		graphics.fillGradient(0, 0, width, height, CleanPvpTheme.TOP_GRADIENT, CleanPvpTheme.BOTTOM_GRADIENT);
		ci.cancel();
	}

	@Inject(method = "extractMenuBackground", at = @At("HEAD"), cancellable = true)
	private void cleanpvp$replaceMenuBackground(GuiGraphicsExtractor graphics, CallbackInfo ci) {
		graphics.fillGradient(0, 0, width, height, CleanPvpTheme.TOP_GRADIENT, CleanPvpTheme.BOTTOM_GRADIENT);
		ci.cancel();
	}

	@Inject(method = "extractMenuBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIII)V", at = @At("HEAD"), cancellable = true)
	private void cleanpvp$replaceMenuBackgroundRegion(GuiGraphicsExtractor graphics, int y, int height, int alphaTop, int alphaBottom, CallbackInfo ci) {
		graphics.fillGradient(0, y, width, y + height, CleanPvpTheme.TOP_GRADIENT, CleanPvpTheme.BOTTOM_GRADIENT);
		ci.cancel();
	}
}
