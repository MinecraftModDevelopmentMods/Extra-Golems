package com.mcmoddev.golems.renders;

import com.mcmoddev.golems.entity.FurnaceGolem;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class LayerInertGolem<T extends GolemBase> extends LayerRenderer<T, ModelGolem<T>> {

	public LayerInertGolem(IEntityRenderer<T, ModelGolem<T>> ientityrenderer) {
		super(ientityrenderer);
	}

	@Override
	public void render(final T entity, final float limbSwing, final float limbSwingAmount, final float partialTicks,
			final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleIn) {
		if(entity instanceof FurnaceGolem && !((FurnaceGolem)entity).hasFuel()) {
			this.getEntityModel().golemHead.rotateAngleY = (float)Math.toRadians(60D);
			this.getEntityModel().golemHead.rotateAngleX = (float)Math.toRadians(60D);
			this.getEntityModel().golemLeftLeg.rotateAngleX = 0F;
			this.getEntityModel().golemRightLeg.rotateAngleX = 0F;
			this.getEntityModel().golemLeftLeg.rotateAngleY = 0.0F;
			this.getEntityModel().golemRightLeg.rotateAngleY = 0.0F;
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
