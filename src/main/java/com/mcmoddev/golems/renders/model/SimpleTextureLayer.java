package com.mcmoddev.golems.renders.model;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.util.GolemRenderSettings;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class SimpleTextureLayer<T extends GolemBase> extends LayerRenderer<T, GolemModel<T>> {
  
  final GolemRenderSettings.ITextureProvider textureProvider;
  final GolemRenderSettings.IColorProvider colorProvider;
  final GolemRenderSettings.ILightingProvider lightingProvider;
  
  final float alphaColor;
  
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
      final Vector3f colors = GolemRenderSettings.unpackColor(colorProvider.getColor(golem));
      // get packed light and a vertex builder bound to the correct texture
      final int packedLight = lightingProvider.disableLighting(golem) ? 15728880 : packedLightIn;
//      final int packedOverlay = LivingRenderer.getPackedOverlay(golem, 0.0F);
//      final IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(texture));
      if(alphaColor < 1.0F) {
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
      }
      this.getEntityModel().setColor(colors.getX(), colors.getY(), colors.getZ());
      renderCutoutModel(this.getEntityModel(), texture, matrixStackIn, bufferIn, packedLight, golem, 1.0F, 1.0F, 1.0F);
//      this.getEntityModel().render(matrixStackIn, vertexBuilder, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, alphaColor);
      RenderSystem.disableBlend();
    }
  }
  
}
