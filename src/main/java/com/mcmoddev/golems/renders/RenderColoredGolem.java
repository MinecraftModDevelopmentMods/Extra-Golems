package com.mcmoddev.golems.renders;

import com.mcmoddev.golems.entity.base.GolemColorized;
import com.mojang.blaze3d.platform.GlStateManager;

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
  public void doRender(final GolemColorized golem, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
    final float colorRed = golem.getColorRed();
    final float colorGreen = golem.getColorGreen();
    final float colorBlue = golem.getColorBlue();
    final float colorAlpha = golem.getColorAlpha();

    // render first pass of golem texture (usually eyes and other opaque,
    // pre-colored features)
    if (golem.hasBase()) {
      this.texture = golem.getTextureBase();
      super.doRender(golem, x, y, z, entityYaw, partialTicks);
    }

    // prepare to render the complicated layer
    GlStateManager.pushMatrix();
    // enable transparency if needed
    GlStateManager.color4f(colorRed, colorGreen, colorBlue, colorAlpha);
    if (golem.hasTransparency()) {
      GlStateManager.enableNormalize();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
    }

    // render second pass of golem texture
    if (golem.hasOverlay()) {
      this.texture = golem.getTextureToColor();
      super.doRender(golem, x, y, z, entityYaw, partialTicks);
    }

    // return GL11 settings to normal
    if (golem.hasTransparency()) {
      GlStateManager.disableBlend();
      GlStateManager.disableNormalize();
    }
    GlStateManager.popMatrix();
  }

  @Override
  protected void bindGolemTexture(final GolemColorized golem) {
    // do nothing
  }
}
