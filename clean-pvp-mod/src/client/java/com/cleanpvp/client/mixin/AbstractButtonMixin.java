package com.cleanpvp.client.mixin;

import com.cleanpvp.client.CleanPvpTheme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractButton.class)
public abstract class AbstractButtonMixin extends AbstractWidget {
	@Shadow
	protected abstract void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta);

	private AbstractButtonMixin(int x, int y, int width, int height, net.minecraft.network.chat.Component message) {
		super(x, y, width, height, message);
	}

	@Inject(method = "extractWidgetRenderState", at = @At("HEAD"), cancellable = true)
	private void cleanpvp$renderGradientButton(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		int top = isHoveredOrFocused() ? CleanPvpTheme.BUTTON_HOVER_TOP : CleanPvpTheme.BUTTON_TOP;
		int bottom = isHoveredOrFocused() ? CleanPvpTheme.BUTTON_HOVER_BOTTOM : CleanPvpTheme.BUTTON_BOTTOM;
		if (!active) {
			top = 0xFF3A5A4A;
			bottom = 0xFF2A4C66;
		}

		graphics.fillGradient(getX(), getY(), getX() + width, getY() + height, top, bottom);
		graphics.outline(getX(), getY(), width, height, CleanPvpTheme.BUTTON_BORDER);
		extractContents(graphics, mouseX, mouseY, delta);
		ci.cancel();
	}
}
