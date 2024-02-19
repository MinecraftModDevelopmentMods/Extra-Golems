package com.mcmoddev.golems.client.entity;

import com.mcmoddev.golems.entity.GolemBase;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class GolemModel<T extends GolemBase> extends IronGolemModel<T> implements ArmedModel {

	protected final ModelPart kitty = createKittyLayer().bakeRoot();
	protected final ModelPart tail;
	protected final ModelPart tail1;
	protected final ModelPart ears;

	private float red = 1.0F;
	private float green = 1.0F;
	private float blue = 1.0F;

	public GolemModel(ModelPart rootIn) {
		super(rootIn);
		this.tail = kitty.getChild("tail");
		this.tail1 = tail.getChild("tail1");
		this.ears = kitty.getChild("ears");
	}

	//// MESH DEFINITION ////

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		final CubeDeformation layerDeformation = new CubeDeformation(0.25F);

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F, CubeDeformation.NONE)
				.texOffs(0, 85).addBox(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F, layerDeformation)
				.texOffs(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, CubeDeformation.NONE)
				.texOffs(0, 109).addBox(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, layerDeformation), PartPose.offset(0.0F, -7.0F, 0.0F));

		PartDefinition bodyBackDecor = body.addOrReplaceChild("body_back_decor", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 3.0F, 5.0F, 0.4363F, 0.0F, 0.0F));
		bodyBackDecor.addOrReplaceChild("body_back_decor1", CubeListBuilder.create().texOffs(106, 32).addBox(-5.5F, -6.0F, 0.0F, 10.0F, 8.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, -1.5708F, 0.0F, -0.7854F));
		bodyBackDecor.addOrReplaceChild("body_back_decor2", CubeListBuilder.create().texOffs(106, 32).addBox(-5.5F, -6.0F, 0.0F, 10.0F, 8.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, -1.5708F, 0.0F, 0.7854F));

		PartDefinition leftLeg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
				.texOffs(60, 0).mirror().addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, CubeDeformation.NONE).mirror(false)
				.texOffs(106, 0).mirror().addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, layerDeformation).mirror(false), PartPose.offset(5.0F, 11.0F, 0.0F));

		PartDefinition rightLeg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
				.texOffs(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, CubeDeformation.NONE)
				.texOffs(83, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, layerDeformation), PartPose.offset(-4.0F, 11.0F, 0.0F));

		PartDefinition leftArm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
				.texOffs(60, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, CubeDeformation.NONE)
				.texOffs(82, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, layerDeformation)
				.texOffs(61, 110).addBox(8.0F, 17.51F, -4.0F, 6.0F, 10.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -7.0F, 0.0F));

		PartDefinition leftArmCross = leftArm.addOrReplaceChild("left_arm_cross", CubeListBuilder.create(), PartPose.offsetAndRotation(13.0F, -2.0F, 0.0F, -0.1745F, 0.0F, 0.5236F));
		leftArmCross.addOrReplaceChild("left_arm_cross1", CubeListBuilder.create().texOffs(106, 42).addBox(-5.5F, -5.0F, 0.0F, 10.0F, 8.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
		leftArmCross.addOrReplaceChild("left_arm_cross2", CubeListBuilder.create().texOffs(106, 42).addBox(-5.5F, -5.0F, 0.0F, 10.0F, 8.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition rightArm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
				.texOffs(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, CubeDeformation.NONE)
				.texOffs(82, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, layerDeformation)
				.texOffs(32, 110).addBox(-14.0F, 17.51F, -4.0F, 6.0F, 10.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -7.0F, 0.0F));

		PartDefinition rightArmCross = rightArm.addOrReplaceChild("right_arm_cross", CubeListBuilder.create(), PartPose.offsetAndRotation(-13.0F, -2.0F, 0.0F, -0.1745F, 0.0F, -0.5236F));
		rightArmCross.addOrReplaceChild("right_arm_cross1", CubeListBuilder.create().texOffs(106, 42).addBox(-4.5F, -5.0F, 0.0F, 10.0F, 8.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
		rightArmCross.addOrReplaceChild("right_arm_cross2", CubeListBuilder.create().texOffs(106, 42).addBox(-5.5F, -5.0F, -1.0F, 10.0F, 8.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, CubeDeformation.NONE)
				.texOffs(0, 20).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, layerDeformation)
				.texOffs(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F, CubeDeformation.NONE)
				.texOffs(24, 20).addBox(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F, layerDeformation), PartPose.offset(0.0F, -7.0F, -2.0F));

		PartDefinition ears = head.addOrReplaceChild("ears", CubeListBuilder.create()
				.texOffs(106, 52).mirror().addBox(4.0F, -46.0F, -3.5F, 10.0F, 8.0F, 1.0F, CubeDeformation.NONE).mirror(false)
				.texOffs(106, 52).addBox(-14.0F, -46.0F, -3.5F, 10.0F, 8.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 31.0F, 2.0F));

		PartDefinition rod = head.addOrReplaceChild("rod", CubeListBuilder.create()
				.texOffs(17, 123).addBox(-1.0F, -15.0F, -2.5F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE)
				.texOffs(0, 121).addBox(-2.0F, -18.0F, -3.5F, 4.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));
		
		PartDefinition headCross = head.addOrReplaceChild("head_cross", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -12.0F, -2.0F, -0.1745F, 0.0F, 0.0F));
		headCross.addOrReplaceChild("head_cross1", CubeListBuilder.create().texOffs(106, 22).addBox(-5.5F, -8.0F, 0.0F, 10.0F, 8.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
		headCross.addOrReplaceChild("head_cross2", CubeListBuilder.create().texOffs(106, 22).addBox(-5.0F, -8.0F, 0.5F, 10.0F, 8.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	public static LayerDefinition createKittyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition root = meshdefinition.getRoot();
		PartDefinition ears = root.addOrReplaceChild("ears", CubeListBuilder.create().texOffs(9, 16).addBox(-5.0F, -16.0F, -4.0F, 10.0F, 6.0F, 1.0F), PartPose.ZERO);
		PartDefinition tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 4.0F), PartPose.offsetAndRotation(0.0F, 10.0F, 4.0F, -2.4435F, 0.0F, 0.0F));
		tail.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 10.0F, 2.0F), PartPose.offsetAndRotation(0.0F, -8.0F, 3.0F, 0.2618F, 0.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	public ModelPart getKitty() {
		return this.kitty;
	}

	//// RENDER ////

	@Override
	public void renderToBuffer(final PoseStack poseStack, final VertexConsumer vertexConsumer, final int packedLight, final int packedOverlay, final float red,
							   final float green, final float blue, final float alpha) {
		// render with custom colors
		super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, this.red, this.green, this.blue, alpha);
	}

	public void renderKittyLayer(final PoseStack poseStack, final VertexConsumer vertexConsumer, final int packedLight, final int packedOverlay) {
		getKitty().render(poseStack, vertexConsumer, packedLight, packedOverlay);
	}

	//// ANIMATIONS ////

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, partialTicks, netHeadYaw, headPitch);
	}

	public void setupKittyAnim(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float netHeadYaw, float headPitch) {
		// ears
		this.ears.copyFrom(this.head);
		// tail
		this.tail.y = 2.0F;
		this.tail.z = 4.0F;
		// tail animation
		float idleSwing = Mth.cos((entity.tickCount + entity.getId() + partialTicks) * 0.058F);
		float tailSwing = Mth.cos(limbSwing) * limbSwingAmount;
		tail.xRot = -2.4435F + 0.38F * tailSwing;
		tail1.xRot = 0.2618F + 0.48F * tailSwing;
		tail.zRot = 0.06F * idleSwing;
		tail1.zRot = -0.05F * idleSwing;
	}

	//// COLOR ////

	public void setColor(final float red, final float green, final float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public void resetColor() {
		red = green = blue = 1.0F;
	}

	//// ARMED MODEL ////

	@Override
	public void translateToHand(HumanoidArm hand, PoseStack matrixStack) {
		getArmForSide(hand).translateAndRotate(matrixStack);
	}

	protected ModelPart getArmForSide(HumanoidArm side) {
		if (side == HumanoidArm.LEFT) {
			return this.leftArm;
		}
		return this.rightArm;
	}
}
