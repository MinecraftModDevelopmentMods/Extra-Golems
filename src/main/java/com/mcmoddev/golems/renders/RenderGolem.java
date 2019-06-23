package com.mcmoddev.golems.renders;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * RenderGolem is the same as RenderIronGolem but with casting to GolemBase instead of
 * EntityIronGolem.
 */
public class RenderGolem extends LivingRenderer<GolemBase, ModelGolem> {

	public RenderGolem(final EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new ModelGolem(), 0.5F);
	}

	@Override
	protected void applyRotations(final GolemBase golem, final float ageInTicks, final float rotationYaw,
				      final float partialTicks) {
		super.applyRotations(golem, ageInTicks, rotationYaw, partialTicks);

		if ((double) golem.limbSwingAmount >= 0.01D) {
			final float f = 13.0F;
			final float f1 = golem.limbSwing - golem.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
			final float f2 = (Math.abs(f1 % f - f * 0.5F) - f * 0.25F) / (f * 0.25F);
			GlStateManager.rotatef(6.5F * f2, 0.0F, 0.0F, 1.0F);
		}
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call
	 * Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(final GolemBase golem) {
		return golem.getTextureType();
	}


}
