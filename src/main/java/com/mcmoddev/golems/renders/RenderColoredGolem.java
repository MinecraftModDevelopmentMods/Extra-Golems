package com.mcmoddev.golems.renders;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemColorized;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

/**
 * RenderColoredGolem is the same as RenderGolem but with casting to GolemColorized instead of
 * GolemBase.
 */
public class RenderColoredGolem extends RenderGolem {

	private static final ResourceLocation fallbackTexture = GolemBase.makeTexture(ExtraGolems.MODID, GolemNames.CLAY_GOLEM);
	private ResourceLocation texture;
	
	public RenderColoredGolem(final EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public void doRender(final GolemBase entity, final double x, final double y, final double z, final float f0, final float f1) {
		final GolemColorized golem = (GolemColorized) entity;
		final float colorRed = golem.getColorRed();
		final float colorGreen = golem.getColorGreen();
		final float colorBlue = golem.getColorBlue();
		final float colorAlpha = golem.getColorAlpha();

		// render first pass of golem texture (usually eyes and other opaque, pre-colored features)
		if (golem.hasBase()) {
			this.texture = golem.getTextureBase();
			super.doRender(golem, x, y, z, f0, f1);
		}

		// prepare to render the complicated layer
		GlStateManager.pushMatrix();
		// enable transparency if needed
		GlStateManager.color4f(colorRed, colorGreen, colorBlue, colorAlpha);
		if (golem.hasTransparency()) {
			GlStateManager.enableNormalize();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
		}

		// render second pass of golem texture
		if (golem.hasOverlay()) {
			this.texture = golem.getTextureToColor();
			super.doRender(golem, x, y, z, f0, f1);
		}

		// return GL11 settings to normal
		if (golem.hasTransparency()) {
			GlStateManager.disableBlend();
			GlStateManager.disableNormalize();
		}
		GlStateManager.popMatrix();
	}

//	@Override
//	protected void applyRotations(final GolemBase golem, final float ageInTicks, final float rotationYaw,
//			final float partialTicks) {
//		super.applyRotations(golem, ageInTicks, rotationYaw, partialTicks);
//
//		if ((double) golem.limbSwingAmount >= 0.01D) {
//			final float f = 13.0F;
//			final float f1 = golem.limbSwing - golem.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
//			final float f2 = (Math.abs(f1 % f - f * 0.5F) - f * 0.25F) / (f * 0.25F);
//			GlStateManager.rotatef(6.5F * f2, 0.0F, 0.0F, 1.0F);
//		}
//	}

	@Override
	protected ResourceLocation getEntityTexture(final GolemBase golem) {
		return this.texture != null ? this.texture : fallbackTexture;
	}
}
