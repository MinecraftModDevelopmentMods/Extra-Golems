package com.mcmoddev.golems.render;

import com.mcmoddev.golems.EGConfig;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.render.GolemRenderSettings;
import com.mcmoddev.golems.entity.GolemBase;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class GolemModel<T extends GolemBase> extends IronGolemModel<T> implements ArmedModel {

	private static final Vector3f ONE = new Vector3f(1.0F, 1.0F, 1.0F);

	private final ModelPart kitty = createKittyLayer().bakeRoot();
	private final ModelPart tail;
	private final ModelPart tail1;
	private final ModelPart ears;

	private GolemRenderSettings settings = GolemRenderSettings.EMPTY;
	private float red = 1.0f;
	private float green = 1.0f;
	private float blue = 1.0f;
	private boolean disableLayers;

	public GolemModel(ModelPart rootIn) {
		super(rootIn);
		tail = kitty.getChild("tail");
		tail1 = tail.getChild("tail1");
		ears = kitty.getChild("ears");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F, new CubeDeformation(0.0F))
				.texOffs(0, 85).addBox(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F, new CubeDeformation(0.25F))
				.texOffs(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(0, 109).addBox(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -7.0F, 0.0F));

		PartDefinition body_back_decor = body.addOrReplaceChild("body_back_decor", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 3.0F, 5.0F, 0.4363F, 0.0F, 0.0F));
		body_back_decor.addOrReplaceChild("body_back_decor1", CubeListBuilder.create().texOffs(106, 32).addBox(-5.5F, -6.0F, 0.0F, 10.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, -1.5708F, 0.0F, -0.7854F));
		body_back_decor.addOrReplaceChild("body_back_decor2", CubeListBuilder.create().texOffs(106, 32).addBox(-5.5F, -6.0F, 0.0F, 10.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, -1.5708F, 0.0F, 0.7854F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
				.texOffs(60, 0).mirror().addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(106, 0).mirror().addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(5.0F, 11.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
				.texOffs(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(83, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offset(-4.0F, 11.0F, 0.0F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
				.texOffs(60, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(82, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, new CubeDeformation(0.25F))
				.texOffs(61, 110).addBox(8.0F, 17.51F, -4.0F, 6.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

		PartDefinition left_arm_cross = left_arm.addOrReplaceChild("left_arm_cross", CubeListBuilder.create(), PartPose.offsetAndRotation(13.0F, -2.0F, 0.0F, -0.1745F, 0.0F, 0.5236F));
		left_arm_cross.addOrReplaceChild("left_arm_cross1", CubeListBuilder.create().texOffs(106, 42).addBox(-5.5F, -5.0F, 0.0F, 10.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
		left_arm_cross.addOrReplaceChild("left_arm_cross2", CubeListBuilder.create().texOffs(106, 42).addBox(-5.5F, -5.0F, 0.0F, 10.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
				.texOffs(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(82, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, new CubeDeformation(0.25F))
				.texOffs(32, 110).addBox(-14.0F, 17.51F, -4.0F, 6.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

		PartDefinition right_arm_cross = right_arm.addOrReplaceChild("right_arm_cross", CubeListBuilder.create(), PartPose.offsetAndRotation(-13.0F, -2.0F, 0.0F, -0.1745F, 0.0F, -0.5236F));
		right_arm_cross.addOrReplaceChild("right_arm_cross1", CubeListBuilder.create().texOffs(106, 42).addBox(-4.5F, -5.0F, 0.0F, 10.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
		right_arm_cross.addOrReplaceChild("right_arm_cross2", CubeListBuilder.create().texOffs(106, 42).addBox(-5.5F, -5.0F, -1.0F, 10.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(0, 20).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.25F))
				.texOffs(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(24, 20).addBox(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.25F))
				.texOffs(17, 123).addBox(-1.0F, -15.0F, -2.5F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 121).addBox(-2.0F, -18.0F, -3.5F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(106, 52).mirror().addBox(4.0F, -15.0F, -1.5F, 10.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
				.texOffs(106, 52).addBox(-15.0F, -15.0F, -1.5F, 10.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, -2.0F));

		PartDefinition head_cross = head.addOrReplaceChild("head_cross", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -12.0F, -2.0F, -0.1745F, 0.0F, 0.0F));
		head_cross.addOrReplaceChild("head_cross1", CubeListBuilder.create().texOffs(106, 22).addBox(-5.5F, -8.0F, 0.0F, 10.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
		head_cross.addOrReplaceChild("head_cross2", CubeListBuilder.create().texOffs(106, 22).addBox(-5.0F, -8.0F, 0.5F, 10.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	public static LayerDefinition createKittyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("ears", CubeListBuilder.create().texOffs(9, 16).addBox(-5.0F, -16.0F, -4.0F, 10.0F, 6.0F, 1.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
		PartDefinition taildefinition = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 4.0F), PartPose.offsetAndRotation(0.0F, 10.0F, 4.0F, -2.4435F, 0.0F, 0.0F));
		taildefinition.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 10.0F, 2.0F), PartPose.offsetAndRotation(0.0F, -8.0F, 3.0F, 0.2618F, 0.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	public ModelPart getKitty() {
		return this.kitty;
	}

	@Override
	public void renderToBuffer(final PoseStack matrixStackIn, final VertexConsumer vertexBuilder, final int packedLightIn, final int packedOverlayIn, final float redIn,
							   final float greenIn, final float blueIn, final float alphaIn) {
		matrixStackIn.pushPose();
		// check for holiday tweaks
		if (ExtraGolems.CONFIG.aprilFirst()) {
			matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		}
		// render with custom colors
		super.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alphaIn);
		matrixStackIn.popPose();
	}

	// SETTINGS AND COLORS

	public void setSettings(final GolemRenderSettings settings, final GolemBase entity) {
		this.settings = settings;
		// colors
		final Vector3f colors;
		if (settings.getBaseColor().isPresent() && settings.getBaseColor().get() > 0) {
			colors = GolemRenderSettings.unpackColor(settings.getBaseColor().get());
		} else if (settings.useBiomeColor()) {
			colors = GolemRenderSettings.unpackColor(entity.getBiomeColor());
		} else {
			colors = ONE;
		}
		this.setColor(colors.x(), colors.y(), colors.z());
	}

	public GolemRenderSettings getSettings() {
		return settings;
	}

	public void setColor(final float r, final float g, final float b) {
		red = r;
		green = g;
		blue = b;
	}

	public void resetSettings() {
		this.settings = GolemRenderSettings.EMPTY;
	}

	public void resetColor() {
		red = green = blue = 1.0F;
	}

	public float red() {
		return red;
	}

	public float green() {
		return green;
	}

	public float blue() {
		return blue;
	}

	public void disableLayers(final boolean disable) {
		disableLayers = disable;
	}

	public boolean disableLayers() {
		return disableLayers;
	}

	// KITTY LAYER HELPERS

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, partialTicks, netHeadYaw, headPitch);
		final String name = ChatFormatting.stripFormatting(entity.getName().getString());
		final boolean earsAndTail = entity.hasCustomName() && "kitty".equalsIgnoreCase(name);
		// animate tail
		if (earsAndTail) {
			// ears
			this.ears.copyFrom(this.head);
			// tail
			this.tail.y = 2.0F;
			this.tail.z = 4.0F;
			// tail animation
			float idleSwing = Mth.cos((entity.tickCount + partialTicks) * 0.058F);
			float tailSwing = Mth.cos(limbSwing) * limbSwingAmount;
			tail.xRot = -2.4435F + 0.38F * tailSwing;
			tail1.xRot = 0.2618F + 0.48F * tailSwing;
			tail.zRot = 0.06F * idleSwing;
			tail1.zRot = -0.05F * idleSwing;
		}
		// show or hide ears/tail
		this.kitty.visible = earsAndTail;
	}

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
