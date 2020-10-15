package com.mcmoddev.golems.renders.model;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.util.GolemRenderSettings;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class GolemEyesLayer<T extends GolemBase> extends LayerRenderer<T, GolemModel<T>> {
  
  public GolemEyesLayer(IEntityRenderer<T, GolemModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T golem,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    final GolemRenderSettings settings = golem.getGolemContainer().getRenderSettings();
    if (!golem.isInvisible()) {
      // eyes texture
      final ResourceLocation vines = settings.getEyesTexture().getTexture(golem);
      // get packed light and a vertex builder bound to the correct texture
      final int packedLight = settings.doEyesGlow() ? 15728880 : packedLightIn;
      final int packedOverlay = LivingRenderer.getPackedOverlay(golem, 0.0F);
      final IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(vines));
      
      // render vines
      this.getEntityModel().render(matrixStackIn, vertexBuilder, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }
  }
}
