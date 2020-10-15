package com.mcmoddev.golems.renders.model;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.IronGolemModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class GolemModel<T extends GolemBase> extends IronGolemModel<T> {
  
  private final ModelRenderer tail;
  private final ModelRenderer tail1;
  private final ModelRenderer ears;

  private float red = 1.0f;
  private float green = 1.0f;
  private float blue = 1.0f;
  
  public GolemModel() {
    super();
    tail = new ModelRenderer(this, 0, 0).setTextureSize(32, 32);
    tail.setRotationPoint(0.0F, 10.0F, 4.0F);
    tail.rotateAngleX = -2.4435F;
    tail.addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);

    tail1 = new ModelRenderer(this, 0, 16).setTextureSize(32, 32);
    tail1.setRotationPoint(0.0F, -8.0F, 3.0F);
    tail.addChild(tail1);
    tail1.rotateAngleX = 0.2618F;
    tail1.addBox(-1.0F, -8.0F, -2.0F, 2.0F, 10.0F, 2.0F, 0.0F, false);
    
    ears = new ModelRenderer(this).setTextureSize(32, 32);
    ears.setRotationPoint(0.0F, 0.0F, 0.0F);
    ears.setTextureOffset(9, 16).addBox(-5.0F, -16.0F, -2.0F, 10.0F, 5.0F, 0.0F, 0.0F, false);
  }

  @Override
  public void render(final MatrixStack matrixStackIn, final IVertexBuilder vertexBuilder, final int packedLightIn, final int packedOverlayIn, final float redIn,
      final float greenIn, final float blueIn, final float alphaIn) {
    // scale as necessary
    if (isChild) {
      float scaleChild = 0.5F;
      matrixStackIn.scale(scaleChild, scaleChild, scaleChild);
      matrixStackIn.translate(0.0F, 1.5F, 0.0F);
    }
    // check for holiday tweaks
    if(ExtraGolemsConfig.aprilFirst()) {
      matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
    }
    // render with custom colors
    super.render(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, 1.0F);
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
  
  public void renderKittyEars(T golem, MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, 
      int packedOverlayIn) {
    this.ears.copyModelAngles(this.ironGolemHead);
    this.ears.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
 
  public void renderKittyTail(T golem, MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, 
      int packedOverlayIn, float limbSwing, float limbSwingAmount) {
    // tail angles
    this.tail.copyModelAngles(this.ironGolemBody);
    this.tail.rotationPointY = 2.0F;
    this.tail.rotationPointZ = 4.0F;
    // tail animation
    float idleSwing = MathHelper.cos((golem.ticksExisted) * 0.08F);
    float tailSwing = MathHelper.cos(limbSwing) * limbSwingAmount;
    tail.rotateAngleX = -2.4435F + 0.38F * tailSwing;
    tail1.rotateAngleX = 0.2618F + 0.48F * tailSwing;
    tail.rotateAngleZ = 0.1F * idleSwing;
    tail1.rotateAngleZ = 0.18F * idleSwing;
    this.tail.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
}
