package com.mcmoddev.golems.renders.model;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.util.GolemRenderSettings;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class SimpleTextureLayer<T extends GolemBase> extends LayerRenderer<T, GolemModel<T>> {
  
  final GolemRenderSettings.ITextureProvider textureProvider;
  final GolemRenderSettings.IColorProvider colorProvider;
  final GolemRenderSettings.ILightingProvider lightingProvider;
  
  final float alphaColor;
  
  /**
   * Create a simple texture with the following settings:
   * @param ientityrenderer the parent renderer
   * @param texture a texture provider
   * @param color a color provider
   * @param lighting a lighting provider
   * @param alpha the alpha level of the layer
   **/
  public SimpleTextureLayer(IEntityRenderer<T, GolemModel<T>> ientityrenderer, 
      GolemRenderSettings.ITextureProvider texture,
      GolemRenderSettings.IColorProvider color,
      GolemRenderSettings.ILightingProvider lighting,
      float alpha) {
    super(ientityrenderer);
    textureProvider = texture;
    colorProvider = color;
    lightingProvider = lighting;
    alphaColor = alpha;
  }

  @Override
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T golem,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    final ResourceLocation texture = textureProvider.getTexture(golem);
    if(!golem.isInvisible() && texture != null) {
      matrixStackIn.push();
      final Vector3f colors = GolemRenderSettings.unpackColor(colorProvider.getColor(golem));
      // get packed light and a vertex builder bound to the correct texture
      final int packedLight = lightingProvider.disableLighting(golem) ? 15728880 : packedLightIn;
      //final int packedOverlay = LivingRenderer.getPackedOverlay(golem, 0.0F);
      final IVertexBuilder vertexBuilder = bufferIn.getBuffer(getRenderType(texture));
      if(alphaColor < 1.0F) {
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alphaColor);
      }
      this.getEntityModel().setColor(colors.getX(), colors.getY(), colors.getZ());
      this.getEntityModel().render(matrixStackIn, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, alphaColor);
      if(alphaColor < 1.0F) {
        RenderSystem.disableBlend();
      }
      matrixStackIn.pop();
    }
  }
  
  protected RenderType getRenderType(final ResourceLocation texture) {
    return alphaColor < 1.0F ? RenderType.getEntityTranslucent(texture) : RenderType.getEntityCutoutNoCull(texture);
  }
  
}
