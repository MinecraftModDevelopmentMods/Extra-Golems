package com.mcmoddev.golems.render;

import com.mcmoddev.golems.EGConfig;
import com.mcmoddev.golems.entity.GolemBase;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;

public class GolemModel<T extends GolemBase> extends IronGolemModel<T> implements ArmedModel {
  
  private final ModelPart kitty = createKittyLayer().bakeRoot();
  private final ModelPart tail;
  private final ModelPart tail1;
  private final ModelPart ears;

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
    partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F).texOffs(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F), PartPose.offset(0.0F, -7.0F, -2.0F));
    partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F).texOffs(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -7.0F, 0.0F));
    partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F), PartPose.offset(0.0F, -7.0F, 0.0F));
    partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(60, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F), PartPose.offset(0.0F, -7.0F, 0.0F));
    partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F), PartPose.offset(-4.0F, 11.0F, 0.0F));
    partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(60, 0).mirror().addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F), PartPose.offset(5.0F, 11.0F, 0.0F));
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
    if(EGConfig.aprilFirst()) {
      matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
    }
    // render with custom colors
    super.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alphaIn);
    matrixStackIn.popPose();
  }
  
  // COLOR HELPERS

  public void setColor(final float r, final float g, final float b) {
    red = r;
    green = g;
    blue = b;
  }
  

  public void resetColor() { red = green = blue = 1.0F; }
  public float red() { return red; }
  public float green() {  return green; }
  public float blue() { return blue;  }
  
  public void disableLayers(final boolean disable) { disableLayers = disable; }
  public boolean disableLayers() { return disableLayers; }
  
  // KITTY LAYER HELPERS
  
  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float netHeadYaw, float headPitch) {
    super.setupAnim(entity, limbSwing, limbSwingAmount, partialTicks, netHeadYaw, headPitch);
    final boolean earsAndTail = entity.hasCustomName() && "Kitty".equals(entity.getName().getContents());
    // animate tail
    if(earsAndTail) {
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
  
  public void renderKittyEars(T golem, PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, 
      int packedOverlayIn) {
    this.ears.copyFrom(this.head);
    this.ears.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
 
  public void renderKittyTail(T golem, PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, 
      int packedOverlayIn, float limbSwing, float limbSwingAmount) {
    // tail angles
    this.tail.copyFrom(this.root());
    this.tail.y = 2.0F;
    this.tail.z = 4.0F;
    // tail animation
    float idleSwing = Mth.cos((golem.tickCount) * 0.058F);
    float tailSwing = Mth.cos(limbSwing) * limbSwingAmount;
    tail.xRot = -2.4435F + 0.38F * tailSwing;
    tail1.xRot = 0.2618F + 0.48F * tailSwing;
    tail.zRot = 0.06F * idleSwing;
    tail1.zRot = -0.05F * idleSwing;
    this.tail.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
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
