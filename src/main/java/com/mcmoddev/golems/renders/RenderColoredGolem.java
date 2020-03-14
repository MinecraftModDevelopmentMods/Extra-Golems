package com.mcmoddev.golems.renders;

import com.mcmoddev.golems.entity.base.GolemColorized;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;

/**
 * RenderColoredGolem is the same as RenderGolem but applies multiple specially
 * rendered layers
 */
public class RenderColoredGolem extends RenderGolem<GolemColorized> {

  public RenderColoredGolem(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn);
  }

  @Override
  public void render(final GolemColorized golem, final float entityYaw, final float partialTicks, final MatrixStack matrixStackIn,
      final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    final float colorRed = golem.getColorRed();
    final float colorGreen = golem.getColorGreen();
    final float colorBlue = golem.getColorBlue();
    final float colorAlpha = golem.getColorAlpha();

    // render first pass of golem texture (usually eyes and other opaque,
    // pre-colored features)
    if (golem.hasBase()) {
      this.entityModel.resetColor();
      this.texture = golem.getTextureBase();
      this.isAlphaLayer = false;
      super.render(golem, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    // prepare to render the complicated layer
    matrixStackIn.push();
    // recolor
    this.entityModel.setColor(colorRed, colorGreen, colorBlue, colorAlpha);
    // enable transparency if needed
    this.isAlphaLayer = isAlphaLayer(golem);
    if (isAlphaLayer) {
      RenderSystem.enableAlphaTest();
      RenderSystem.defaultAlphaFunc();
      RenderSystem.enableBlend();
    }

    // render second pass of golem texture
    if (golem.hasOverlay()) {
      this.texture = golem.getTextureToColor();
      super.render(golem, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    // return GL11 settings to normal
    if (isAlphaLayer) {
      RenderSystem.disableAlphaTest();
      RenderSystem.disableBlend();
    }
    matrixStackIn.pop();
  }

  @Override
  public void bindGolemTexture(final GolemColorized golem) {
    // do nothing
  }

  @Override
  protected void resetColor() {
    // do nothing
  }
}
