package com.mcmoddev.golems.renders;

import javax.annotation.Nullable;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolemsEntities;
import com.mcmoddev.golems.util.GolemNames;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.IronGolemModel;
import net.minecraft.util.ResourceLocation;

/**
 * RenderGolem is the same as RenderIronGolem but with casting to GolemBase instead of
 * EntityIronGolem.
 */
public class RenderGolem<T extends GolemBase> extends LivingRenderer<T, GolemModel<T>> {
	
	protected static final ResourceLocation fallbackTexture = ExtraGolemsEntities.makeTexture(GolemNames.CLAY_GOLEM);
	protected ResourceLocation texture;
	
	protected static final ResourceLocation[] damageIndicators = {
			ExtraGolemsEntities.makeTexture("damage/damaged_0"),
			ExtraGolemsEntities.makeTexture("damage/damaged_1"),
			ExtraGolemsEntities.makeTexture("damage/damaged_2") 
	};
	
	protected static final float DAMAGE_ALPHA = 0.55F;

	public RenderGolem(final EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new GolemModel<T>(), 0.5F);
	}

	@Override
	public void render(final T golem, final float entityYaw, final float partialTicks, 
			final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn) {
		// render everything else first
		this.bindGolemTexture(golem);
		this.resetColor();
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
			RenderSystem.enableAlphaTest();
			RenderSystem.defaultAlphaFunc();
			RenderSystem.enableBlend();
			// actually render the damage texture
			this.texture = damageIndicators[index];
			super.render(golem, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
			// return GL settings to normal
			RenderSystem.disableAlphaTest();
			RenderSystem.disableBlend();
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

	@Override
	@Nullable
	protected RenderType func_230042_a_(final T golem, boolean isVisible, boolean isVisibleToPlayer) {
		ResourceLocation tex = this.getEntityTexture(golem);
		if (isVisibleToPlayer) {
			return RenderType.entityTranslucent(tex);
		} else if (isVisible) {
			return golem.hasTransparency() ? RenderType.entityTranslucent(tex) : RenderType.entityCutout(tex);
		} else {
			return golem.isGlowing() ? RenderType.outline(tex) : null;
		}
	}
	
	/** @return a value between {@code -1} and {@code damageIndicators.length-1}, inclusive **/
	protected int getDamageTexture(final T golem) {
		final float percentHealth = golem.getHealth() / golem.getMaxHealth();
		return damageIndicators.length - (int)Math.ceil(percentHealth * 4.0F);
	}
	
	protected void resetColor() {
		this.entityModel.resetColor();
	}
}
