package com.golems.renders;

import com.golems.entity.GolemBase;
import com.golems.entity.GolemColorized;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

/**
 * RenderColoredGolem is the same as RenderGolem but with casting to GolemColorized instead of
 * GolemBase.
 */
public class RenderColoredGolem extends RenderLiving<GolemColorized> {

    private static final ResourceLocation fallbackTexture = GolemBase.makeGolemTexture("clay");
    private ResourceLocation texture;

    public RenderColoredGolem(final RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelGolem(), 0.5F);
    }

    @Override
    public void doRender(final GolemColorized golem, final double x, final double y, final double z, final float f0, final float f1) {
        final float colorRed = golem.getColorRed();
        final float colorGreen = golem.getColorGreen();
        final float colorBlue = golem.getColorBlue();
        final float colorAlpha = golem.getColorAlpha();

        // render first pass of golem texture (usually eyes and other opaque, pre-colored features)
        if (golem.hasBase()) {
            this.texture = golem.getTextureBase();
            if (this.texture != null) {
                super.doRender(golem, x, y, z, f0, f1);
            }
        }

        // prepare to render the complicated layer
        GlStateManager.pushMatrix();
        // enable transparency if needed
        GlStateManager.color(colorRed, colorGreen, colorBlue, colorAlpha);
        if (golem.hasTransparency()) {
            GlStateManager.enableNormalize();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
        }

        // render second pass of golem texture
        this.texture = golem.getTextureToColor();
        if (this.texture != null) {
            super.doRender(golem, x, y, z, f0, f1);
        }

        // return GL11 settings to normal
        if (golem.hasTransparency()) {
            GlStateManager.disableBlend();
            GlStateManager.disableNormalize();
        }
        GlStateManager.popMatrix();
    }

    @Override
    protected void applyRotations(final GolemColorized golem, final float p_77043_2_, final float rotationYaw,
                                  final float partialTicks) {
        super.applyRotations(golem, p_77043_2_, rotationYaw, partialTicks);

        if ((double) golem.limbSwingAmount >= 0.01D) {
            float f = 13.0F;
            float f1 = golem.limbSwing - golem.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float f2 = (Math.abs(f1 % f - f * 0.5F) - f * 0.25F) / (f * 0.25F);
            GlStateManager.rotate(6.5F * f2, 0.0F, 0.0F, 1.0F);
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call
     * Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final GolemColorized golem) {
        return this.texture != null ? this.texture : fallbackTexture;
    }
}
