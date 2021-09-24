package com.mcmoddev.golems.render;

import com.mcmoddev.golems.EGConfig;
import com.mcmoddev.golems.entity.GolemBase;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.IVertexConsumer;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IronGolemModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class GolemModel<T extends GolemBase> extends IronGolemModel<T> implements IHasArm {
  
  private final ModelRenderer kitty;
  private final ModelRenderer tail;
  private final ModelRenderer tail1;
  private final ModelRenderer ears;

  private float red = 1.0f;
  private float green = 1.0f;
  private float blue = 1.0f;
  private boolean disableLayers;
  
  public GolemModel() {
    super();
	tail = new ModelRenderer(this, 0, 0).setTextureSize(32, 32);
	tail.setRotationPoint(0.0F, 10.0F, 4.0F);
	tail.rotateAngleX = -2.4435F;
	tail.addBox(-2.0F, -8.0F, 0.0F, 4.0F, 8.0F, 4.0F, 0.0F);


	tail1 = new ModelRenderer(this, 0, 16).setTextureSize(32, 32);
	tail1.setRotationPoint(0.0F, -8.0F, 3.0F);
	tail.addChild(tail1);
	tail1.rotateAngleX = 0.2618F;
	tail1.addBox(-1.0F, -8.0F, -2.0F, 2.0F, 10.0F, 2.0F, 0.0F);

	ears = new ModelRenderer(this).setTextureSize(32, 32);
	ears.setRotationPoint(0.0F, 0.0F, 0.0F);
	ears.setTextureOffset(9, 16).addBox(-5.0F, -16.0F, -4.0F, 10.0F, 6.0F, 1.0F, 0.0F);

	kitty = new ModelRenderer(this).setTextureSize(32, 32);
	kitty.addChild(tail);
	kitty.addChild(ears);
  }
  
  public ModelRenderer getKitty() {
    return this.kitty;
  }

  @Override
  public void render(final MatrixStack matrixStackIn, final IVertexBuilder vertexBuilder, final int packedLightIn, final int packedOverlayIn, final float redIn,
							 final float greenIn, final float blueIn, final float alphaIn) {
    matrixStackIn.push();
    // check for holiday tweaks
    if(EGConfig.aprilFirst()) {
      matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
    }
    // render with custom colors
    super.render(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alphaIn);
    matrixStackIn.pop();
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
  public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float netHeadYaw, float headPitch) {
    super.setRotationAngles(entity, limbSwing, limbSwingAmount, partialTicks, netHeadYaw, headPitch);
    final boolean earsAndTail = entity.hasCustomName() && "Kitty".equals(entity.getName().getUnformattedComponentText());
    // animate tail
    if(earsAndTail) {
      // ears
      this.ears.copyModelAngles(this.ironGolemHead);
      // tail
      this.tail.rotationPointY = 2.0F;
      this.tail.rotationPointZ = 4.0F;
      // tail animation
      float idleSwing = MathHelper.cos((entity.ticksExisted + partialTicks) * 0.058F);
      float tailSwing = MathHelper.cos(limbSwing) * limbSwingAmount;
      tail.rotateAngleX = -2.4435F + 0.38F * tailSwing;
      tail1.rotateAngleX = 0.2618F + 0.48F * tailSwing;
      tail.rotateAngleZ = 0.06F * idleSwing;
      tail1.rotateAngleZ = -0.05F * idleSwing;
    }
    // show or hide ears/tail
    this.kitty.showModel = earsAndTail;
  }
  
  public void renderKittyEars(T golem, MatrixStack matrixStackIn, IVertexConsumer bufferIn, int packedLightIn,
      int packedOverlayIn) {
    this.ears.copyModelAngles(this.ironGolemHead);
    this.ears.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }
 
  public void renderKittyTail(T golem, MatrixStack matrixStackIn, IVertexConsumer bufferIn, int packedLightIn,
      int packedOverlayIn, float limbSwing, float limbSwingAmount) {
    this.tail.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
  }

  @Override
  public void translateHand(HandSide hand, MatrixStack matrixStack) {
    getArmForSide(hand).translateRotate(matrixStack);
  }
  
  protected ModelRenderer getArmForSide(HandSide side) {
    if (side == HandSide.LEFT) {
      return this.ironGolemLeftArm;
    }
    return this.ironGolemRightArm;
  }

  public ModelRenderer getBody() {
	return ironGolemBody;
  }
}
