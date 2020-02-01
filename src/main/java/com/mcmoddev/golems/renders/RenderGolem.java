package com.mcmoddev.golems.renders;

import org.lwjgl.opengl.GL11;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolemsEntities;
import com.mcmoddev.golems.util.GolemNames;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.IronGolemModel;
import net.minecraft.util.ResourceLocation;

/**
 * RenderGolem is the same as RenderIronGolem but with casting to GolemBase instead of
 * EntityIronGolem.
 */
public class RenderGolem<T extends GolemBase> extends LivingRenderer<T, IronGolemModel<T>> {
	
	protected static final ResourceLocation fallbackTexture = ExtraGolemsEntities.makeTexture(GolemNames.CLAY_GOLEM);
	protected ResourceLocation texture;
	
	protected static final ResourceLocation[] damageIndicators = {
			ExtraGolemsEntities.makeTexture("damage/damaged_0"),
			ExtraGolemsEntities.makeTexture("damage/damaged_1"),
			ExtraGolemsEntities.makeTexture("damage/damaged_2") 
	};
	
	protected static final float DAMAGE_ALPHA = 0.55F;

	public RenderGolem(final EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new IronGolemModel<T>(), 0.5F);
	}

	@Override
	public void render(final T golem, final float entityYaw, final float partialTicks, 
			final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn) {
		// render everything else first
		this.bindGolemTexture(golem);
		super.render(golem, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		this.renderDamage(golem, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}
	
	protected void bindGolemTexture(final T golem) {
		texture = golem.getTexture();
	}
	
	protected void renderDamage(final T golem, final float entityYaw, final float partialTicks, 
			final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn) {
		// render damage indicator if necessary
		final int index = Math.min(getDamageTexture(golem), damageIndicators.length - 1);
		if (index > -1) {
			RenderSystem.pushMatrix();
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, DAMAGE_ALPHA);
			RenderSystem.enableRescaleNormal();
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			//RenderSystem.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR);
			//RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_DST_COLOR);
			// actually render the damage texture
			this.texture = damageIndicators[index];
			super.render(golem, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
			// return GL settings to normal
			RenderSystem.disableBlend();
			RenderSystem.disableRescaleNormal();
			RenderSystem.popMatrix();
		}
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call
	 * Render.bindEntityTexture.
	 */
	@Override
	public ResourceLocation getEntityTexture(final T golem) {
		return this.texture != null ? this.texture : fallbackTexture;
	}
	
	/** @return a value between {@code -1} and {@code damageIndicators.length-1}, inclusive **/
	protected int getDamageTexture(final T golem) {
		final float percentHealth = golem.getHealth() / golem.getMaxHealth();
		return damageIndicators.length - (int)Math.ceil(percentHealth * 4.0F);
	}
}
