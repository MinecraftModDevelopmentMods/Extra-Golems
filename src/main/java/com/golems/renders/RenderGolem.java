package com.golems.renders;

import com.golems.entity.GolemBase;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

/**
 * RenderGolem is the same as RenderIronGolem but with casting to GolemBase instead of
 * EntityIronGolem
 */
public class RenderGolem extends RenderLiving<GolemBase> {

	public RenderGolem(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelGolem(), 0.5F);
	}

	@Override
	protected void applyRotations(GolemBase golem, float p_77043_2_, float p_77043_3_,
			float partialTicks) {
		super.applyRotations(golem, p_77043_2_, p_77043_3_, partialTicks);

		if ((double) golem.limbSwingAmount >= 0.01D) {
			float f = 13.0F;
			float f1 = golem.limbSwing - golem.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
			float f2 = (Math.abs(f1 % f - f * 0.5F) - f * 0.25F) / (f * 0.25F);
			GlStateManager.rotate(6.5F * f2, 0.0F, 0.0F, 1.0F);
		}
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call
	 * Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(GolemBase golem) {
		return golem.getTextureType();
	}
}
