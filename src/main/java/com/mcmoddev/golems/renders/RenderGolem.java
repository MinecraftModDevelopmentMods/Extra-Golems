package com.mcmoddev.golems.renders;

import org.lwjgl.opengl.GL11;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolemsEntities;
import com.mcmoddev.golems.util.GolemNames;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * RenderGolem is the same as RenderIronGolem but with casting to GolemBase instead of
 * EntityIronGolem.
 */
public class RenderGolem<T extends GolemBase> extends LivingRenderer<T, ModelGolem<T>> {
	
	protected static final ResourceLocation fallbackTexture = ExtraGolemsEntities.makeTexture(GolemNames.CLAY_GOLEM);
	protected ResourceLocation texture;
	
	protected static final ResourceLocation[] damageIndicators = {
			ExtraGolemsEntities.makeTexture("damage/damaged_0"),
			ExtraGolemsEntities.makeTexture("damage/damaged_1"),
			ExtraGolemsEntities.makeTexture("damage/damaged_2") 
	};
	
	protected static final float DAMAGE_ALPHA = 0.5F;

	public RenderGolem(final EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new ModelGolem<T>(), 0.5F);
	}

	@Override
	protected void applyRotations(final T golem, final float ageInTicks, final float rotationYaw,
			final float partialTicks) {
		super.applyRotations(golem, ageInTicks, rotationYaw, partialTicks);

		if ((double) golem.limbSwingAmount >= 0.01D) {
			final float f = 13.0F;
			final float f1 = golem.limbSwing - golem.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
			final float f2 = (Math.abs(f1 % f - f * 0.5F) - f * 0.25F) / (f * 0.25F);
			GlStateManager.rotatef(6.5F * f2, 0.0F, 0.0F, 1.0F);
		}
	}
	
	@Override
	public void doRender(final T golem, final double x, final double y, final double z, final float entityYaw,
			final float partialTicks) {
		// render everything else first
		this.bindGolemTexture(golem);
		super.doRender(golem, x, y, z, entityYaw, partialTicks);
		this.renderDamage(golem, x, y, z, entityYaw, partialTicks);
	}
	
	protected void bindGolemTexture(final T golem) {
		texture = golem.getTexture();
	}
	
	protected void renderDamage(final T golem, final double x, final double y, final double z, final float entityYaw,
			final float partialTicks) {
		// render damage indicator if necessary
		final int index = Math.min(getDamageTexture(golem), damageIndicators.length - 1);
		if (index > -1) {
			GlStateManager.pushMatrix();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, DAMAGE_ALPHA);
			GlStateManager.enableNormalize();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			//GlStateManager.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
			//GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_DST_COLOR);
			// actually render the damage texture
			this.texture = damageIndicators[index];
			super.doRender(golem, x, y, z, entityYaw, partialTicks);
			// return GL settings to normal
			GlStateManager.disableBlend();
			GlStateManager.disableNormalize();
			GlStateManager.popMatrix();
		}
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call
	 * Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(final T golem) {
		return this.texture != null ? this.texture : fallbackTexture;
	}
	
	/** @return a value between {@code -1} and {@code damageIndicators.length-1}, inclusive **/
	protected int getDamageTexture(final T golem) {
		final float percentHealth = golem.getHealth() / golem.getMaxHealth();
		return damageIndicators.length - (int)Math.ceil(percentHealth * 4.0F);
	}
}
