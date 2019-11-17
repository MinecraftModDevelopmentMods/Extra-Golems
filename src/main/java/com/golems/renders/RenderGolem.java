package com.golems.renders;

import org.lwjgl.opengl.GL11;

import com.golems.entity.GolemBase;
import com.golems.main.ExtraGolems;
import com.golems.util.GolemNames;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;

/**
 * RenderGolem is the same as RenderIronGolem but with casting to GolemBase instead of
 * EntityIronGolem.
 */
public class RenderGolem<T extends GolemBase> extends RenderLiving<T> {
	
	protected static final ResourceLocation fallbackTexture = GolemBase.makeTexture(ExtraGolems.MODID, GolemNames.CLAY_GOLEM);
	protected ResourceLocation texture;
	
	protected static final ResourceLocation[] damageIndicators = {
			GolemBase.makeTexture(ExtraGolems.MODID, "damage/damaged_0"),
			GolemBase.makeTexture(ExtraGolems.MODID, "damage/damaged_1"),
			GolemBase.makeTexture(ExtraGolems.MODID, "damage/damaged_2") 
	};
	
	protected static final float DAMAGE_ALPHA = 0.45F;

	public RenderGolem(final RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelGolem(), 0.5F);
		this.addLayer(new LayerGolemFlower(this));
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
		texture = golem.getTextureType();
	}

	 protected void rotateCorpse(final T entityLiving, final float p_77043_2_, final float p_77043_3_, final float partialTicks)
	    {
	        super.rotateCorpse(entityLiving, p_77043_2_, p_77043_3_, partialTicks);

	        if ((double)entityLiving.limbSwingAmount >= 0.01D)
	        {
	            float f = 13.0F;
	            float f1 = entityLiving.limbSwing - entityLiving.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
	            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
	            GlStateManager.rotate(6.5F * f2, 0.0F, 0.0F, 1.0F);
	        }
	    }

	protected void renderDamage(final T golem, final double x, final double y, final double z, final float entityYaw,
			final float partialTicks) {
		// render damage indicator if necessary
		final int index = Math.min(getDamageTexture(golem), damageIndicators.length - 1);
		if (index > -1) {
			GlStateManager.pushMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, DAMAGE_ALPHA);
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
