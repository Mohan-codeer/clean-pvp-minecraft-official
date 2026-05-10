package com.cleanpvp.client.mixin;

import com.cleanpvp.client.CleanPvpTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
	private TitleScreenMixin(net.minecraft.network.chat.Component title) {
		super(title);
	}

	@Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
	private void cleanpvp$gradientTitleBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		graphics.fillGradient(0, 0, width, height, CleanPvpTheme.TOP_GRADIENT, CleanPvpTheme.BOTTOM_GRADIENT);
		ci.cancel();
	}

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void cleanpvp$drawCustomTitle(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		String title = "CLEAN PVP";
		float scale = 3.2f;
		int textWidth = font.width(title);

		graphics.pose().pushMatrix();
		graphics.pose().translate(width / 2f, 18f);
		graphics.pose().scale(scale, scale);
		graphics.text(font, title, -textWidth / 2, 0, 0xFFFFFFFF, true);
		graphics.pose().popMatrix();
	}

	@Redirect(
		method = "extractRenderState",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/LogoRenderer;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IF)V"
		)
	)
	private void cleanpvp$hideVanillaLogo(LogoRenderer instance, GuiGraphicsExtractor graphics, int screenWidth, float fade) {
		// Intentionally no-op: custom CLEAN PVP title is rendered in the tail injector.
	}
}
