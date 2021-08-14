package com.mcmoddev.golems.renders.model;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;

public class GolemModel<T extends GolemBase> extends IronGolemModel<T> implements ArmedModel {
  
  private final ModelPart tail;
  private final ModelPart tail1;
  private final ModelPart ears;

  private float red = 1.0f;
  private float green = 1.0f;
  private float blue = 1.0f;
  
  public GolemModel() {
    super();
    tail = new ModelPart(this, 0, 0).setTexSize(32, 32);
    tail.setPos(0.0F, 10.0F, 4.0F);
    tail.xRot = -2.4435F;
    tail.addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 4.0F, 0.0F);

    tail1 = new ModelPart(this, 0, 16).setTexSize(32, 32);
    tail1.setPos(0.0F, -8.0F, 3.0F);
    tail.addChild(tail1);
    tail1.xRot = 0.2618F;
    tail1.addBox(-1.0F, -8.0F, -2.0F, 2.0F, 10.0F, 2.0F, 0.0F);
    
    ears = new ModelPart(this).setTexSize(32, 32);
    ears.setPos(0.0F, 0.0F, 0.0F);
    ears.texOffs(9, 16).addBox(-5.0F, -16.0F, -4.0F, 10.0F, 6.0F, 1.0F, 0.0F);
  }

  @Override
  public void renderToBuffer(final PoseStack matrixStackIn, final VertexConsumer vertexBuilder, final int packedLightIn, final int packedOverlayIn, final float redIn,
      final float greenIn, final float blueIn, final float alphaIn) {
    matrixStackIn.pushPose();
    // check for holiday tweaks
    if(ExtraGolemsConfig.aprilFirst()) {
      matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
    }
    // render with custom colors
    super.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, 1.0F);
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
  
  // KITTY LAYER HELPERS
  
  public void renderKittyEars(T golem, PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, 
      int packedOverlayIn) {
    this.ears.copyFrom(this.head);
    this.ears.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
 
  public void renderKittyTail(T golem, PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, 
      int packedOverlayIn, float limbSwing, float limbSwingAmount) {
    // tail angles
    this.tail.copyFrom(this.body);
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
      return this.arm1;
    }
    return this.arm0;
  }
}
