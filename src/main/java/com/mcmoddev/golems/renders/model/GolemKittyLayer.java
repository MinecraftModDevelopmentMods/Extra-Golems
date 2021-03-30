package com.mcmoddev.golems.renders.model;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class GolemKittyLayer<T extends GolemBase> extends LayerRenderer<T, GolemModel<T>> {
  
  protected static final ResourceLocation kittyTexture = new ResourceLocation(ExtraGolems.MODID, "textures/entity/layer/kitty_layer.png");

  public GolemKittyLayer(IEntityRenderer<T, GolemModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T golem,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    if (!golem.isInvisible() && golem.hasCustomName() && "Kitty".equals(golem.getName().getUnformattedComponentText())) {
      // get packed light and a vertex builder bound to the correct texture
      int packedOverlay = LivingRenderer.getPackedOverlay(golem, 0.0F);
      IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(kittyTexture));
      // check for holiday tweaks
      if(ExtraGolemsConfig.aprilFirst()) {
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
      }
      // render ears
      matrixStackIn.push();
      this.getEntityModel().renderKittyEars(golem, matrixStackIn, vertexBuilder, packedLightIn, packedOverlay);
      
      // render tail
      this.getEntityModel().renderKittyTail(golem, matrixStackIn, vertexBuilder, packedLightIn, packedOverlay, limbSwing, limbSwingAmount);
      matrixStackIn.pop();
    }
  }
}