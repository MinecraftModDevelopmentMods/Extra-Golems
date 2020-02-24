package com.mcmoddev.golems.renders;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.IronGolemModel;

public class GolemModel<T extends GolemBase> extends IronGolemModel<T> {

  private float red = 1.0f;
  private float green = 1.0f;
  private float blue = 1.0f;
  private float alpha = 1.0f;

  @Override
  public void render(final MatrixStack matrixStackIn, final IVertexBuilder vertexBuilder, final int i3, final int i4, final float redIn,
      final float greenIn, final float blueIn, final float alphaIn) {
    // scale as necessary
    if (isChild) {
      float scaleChild = 0.5F;
      matrixStackIn.scale(scaleChild, scaleChild, scaleChild);
      matrixStackIn.translate(0.0F, 1.5F, 0.0F);
    }
    // render with custom colors
    super.render(matrixStackIn, vertexBuilder, i3, i4, red, green, blue, alpha);
  }

  public void setColor(final float r, final float g, final float b, final float a) {
    red = r;
    green = g;
    blue = b;
    alpha = a;
  }

  public void setAlpha(final float a) {
    alpha = a;
  }

  public void resetColor() {
    red = green = blue = alpha = 1.0F;
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

  public float alpha() {
    return alpha;
  }
}
