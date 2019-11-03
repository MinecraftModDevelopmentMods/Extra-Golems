package com.golems.renders;

import com.golems.entity.GolemColorized;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;

public class RenderColoredGolem extends RenderGolem<GolemColorized> {

	public RenderColoredGolem(final RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public void doRender(final GolemColorized golem, final double x, final double y, final double z, final float f0, final float f1) {
		final float colorRed = golem.getColorRed();
		final float colorGreen = golem.getColorGreen();
		final float colorBlue = golem.getColorBlue();
		final float colorAlpha = golem.getColorAlpha();

		// render first pass of golem texture (usually eyes and other opaque, pre-colored features)
		if (golem.hasBase()) {
			this.texture = golem.getTextureBase();
			if (this.texture != null) {
				super.doRender(golem, x, y, z, f0, f1);
			}
		}

		// prepare to render the complicated layer
		GlStateManager.pushMatrix();
		// enable transparency if needed
		GlStateManager.color(colorRed, colorGreen, colorBlue, colorAlpha);
		if (golem.hasTransparency()) {
			GlStateManager.enableNormalize();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
		}

		// render second pass of golem texture
		this.texture = golem.getTextureToColor();
		if (this.texture != null) {
			super.doRender(golem, x, y, z, f0, f1);
		}

		// return GL11 settings to normal
		if (golem.hasTransparency()) {
			GlStateManager.disableBlend();
			GlStateManager.disableNormalize();
		}
		GlStateManager.popMatrix();
	}
	
	protected void bindGolemTexture(final GolemColorized golem) {
		// do nothing
	}
}
