package com.mcmoddev.golems.renders;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelGolem extends EntityModel<GolemBase> {

	/**
	 * The head model for the iron golem.
	 */
	public final RendererModel golemHead;
	/**
	 * The body model for the iron golem.
	 */
	public final RendererModel golemBody;
	/**
	 * The right arm model for the iron golem.
	 */
	public final RendererModel golemRightArm;
	/**
	 * The left arm model for the iron golem.
	 */
	public final RendererModel golemLeftArm;
	/**
	 * The left leg model for the Iron Golem.
	 */
	public final RendererModel golemLeftLeg;
	/**
	 * The right leg model for the Iron Golem.
	 */
	public final RendererModel golemRightLeg;

	public ModelGolem() {
		this(0.0F);
	}

	public ModelGolem(final float f1) {
		this(f1, -7.0F);
	}

	public ModelGolem(final float f1, final float f2) {
		short short1 = 128;
		short short2 = 128;
		this.golemHead = (new RendererModel(this)).setTextureSize(short1, short2);
		this.golemHead.setRotationPoint(0.0F, 0.0F + f2, -2.0F);
		this.golemHead.setTextureOffset(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8, 10, 8, f1);
		this.golemHead.setTextureOffset(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2, 4, 2, f1);
		this.golemBody = (new RendererModel(this)).setTextureSize(short1, short2);
		this.golemBody.setRotationPoint(0.0F, 0.0F + f2, 0.0F);
		this.golemBody.setTextureOffset(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18, 12, 11, f1);
		this.golemBody.setTextureOffset(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9, 5, 6, f1 + 0.5F);
		this.golemRightArm = (new RendererModel(this)).setTextureSize(short1, short2);
		this.golemRightArm.setRotationPoint(0.0F, -7.0F, 0.0F);
		this.golemRightArm.setTextureOffset(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4, 30, 6, f1);
		this.golemLeftArm = (new RendererModel(this)).setTextureSize(short1, short2);
		this.golemLeftArm.setRotationPoint(0.0F, -7.0F, 0.0F);
		this.golemLeftArm.setTextureOffset(60, 58).addBox(9.0F, -2.5F, -3.0F, 4, 30, 6, f1);
		this.golemLeftLeg = (new RendererModel(this, 0, 22)).setTextureSize(short1, short2);
		this.golemLeftLeg.setRotationPoint(-4.0F, 18.0F + f2, 0.0F);
		this.golemLeftLeg.setTextureOffset(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, f1);
		this.golemRightLeg = (new RendererModel(this, 0, 22)).setTextureSize(short1, short2);
		this.golemRightLeg.mirror = true;
		this.golemRightLeg.setTextureOffset(60, 0).setRotationPoint(5.0F, 18.0F + f2, 0.0F);
		this.golemRightLeg.addBox(-3.5F, -3.0F, -3.0F, 6, 16, 5, f1);
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	@Override
	public void render(final GolemBase entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks,
					   final float netHeadYaw, final float headPitch, final float scale) {

		this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

		float scaleChild = 0.5F;
		GlStateManager.pushMatrix();

		if (this.isChild) {
			GlStateManager.scalef(scaleChild, scaleChild, scaleChild);
			GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
		}

		this.golemHead.render(scale);
		this.golemBody.render(scale);
		this.golemLeftLeg.render(scale);
		this.golemRightLeg.render(scale);
		this.golemRightArm.render(scale);
		this.golemLeftArm.render(scale);

		GlStateManager.popMatrix();
	}

	/**
	 * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating
	 * the movement of arms and legs, where par1 represents the time(so that arms and legs swing
	 * back and forth) and par2 represents how "far" arms and legs can swing at most.
	 */
	@Override
	public void setRotationAngles(final GolemBase entity, final float limbSwing, final float limbSwingAmount, final float ageInTicks,
								  final float netHeadYaw, final float headPitch, final float scaleFactor) {
		this.golemHead.rotateAngleY = netHeadYaw / (180F / (float) Math.PI);
		this.golemHead.rotateAngleX = headPitch / (180F / (float) Math.PI);
		this.golemLeftLeg.rotateAngleX = -1.5F * triangleWave(limbSwing, 13.0F)
			* limbSwingAmount;
		this.golemRightLeg.rotateAngleX = 1.5F * triangleWave(limbSwing, 13.0F)
			* limbSwingAmount;
		this.golemLeftLeg.rotateAngleY = 0.0F;
		this.golemRightLeg.rotateAngleY = 0.0F;
	}

	/**
	 * Used for easily adding entity-dependent animations. The second and third float params here
	 * are the same second and third as in the setRotationAngles method.
	 */
	@Override
	public void setLivingAnimations(final GolemBase entity, final float limbSwing,
									final float limbSwingAmount, final float partialTickTime) {
		int i = entity.getAttackTimer();

		if (i > 0) {
			this.golemRightArm.rotateAngleX = -2.0F
				+ 1.5F * triangleWave((float) i - partialTickTime, 10.0F);
			this.golemLeftArm.rotateAngleX = -2.0F
				+ 1.5F * triangleWave((float) i - partialTickTime, 10.0F);
		} else {
			this.golemRightArm.rotateAngleX = (-0.2F
				+ 1.5F * triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
			this.golemLeftArm.rotateAngleX = (-0.2F
				- 1.5F * triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
		}
	}

	private static float triangleWave(final float f1, final float f2) {
		return (Math.abs(f1 % f2 - f2 * 0.5F) - f2 * 0.25F) / (f2 * 0.25F);
	}
}
