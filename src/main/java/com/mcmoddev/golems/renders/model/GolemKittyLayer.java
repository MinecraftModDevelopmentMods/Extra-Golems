package com.mcmoddev.golems.renders.model;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;

public class GolemKittyLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {
  
  protected static final ResourceLocation kittyTexture = new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/kitty_layer.png");

  public GolemKittyLayer(RenderLayerParent<T, GolemModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T golem,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    if (!golem.isInvisible() && golem.hasCustomName() && "Kitty".equals(golem.getName().getContents())) {
      // get packed light and a vertex builder bound to the correct texture
      int packedOverlay = LivingEntityRenderer.getOverlayCoords(golem, 0.0F);
      VertexConsumer vertexBuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(kittyTexture));
      // check for holiday tweaks
      if(ExtraGolemsConfig.aprilFirst()) {
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      }
      // render ears
      matrixStackIn.pushPose();
      this.getParentModel().renderKittyEars(golem, matrixStackIn, vertexBuilder, packedLightIn, packedOverlay);
      
      // render tail
      this.getParentModel().renderKittyTail(golem, matrixStackIn, vertexBuilder, packedLightIn, packedOverlay, limbSwing, limbSwingAmount);
      matrixStackIn.popPose();
    }
  }
}