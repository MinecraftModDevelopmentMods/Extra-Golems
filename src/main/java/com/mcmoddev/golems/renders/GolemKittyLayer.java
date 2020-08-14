package com.mcmoddev.golems.renders;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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
      
      // render ears
      matrixStackIn.push();
      float yaw = MathHelper.lerp(partialTicks, golem.prevRotationYaw, golem.rotationYaw) - MathHelper.lerp(partialTicks, golem.prevRenderYawOffset, golem.renderYawOffset);
      float pitch = MathHelper.lerp(partialTicks, golem.prevRotationPitch, golem.rotationPitch);
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(yaw));
      matrixStackIn.rotate(Vector3f.XP.rotationDegrees(pitch));
      matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-pitch));
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-yaw));
      this.getEntityModel().renderKittyEars(matrixStackIn, vertexBuilder, packedLightIn, packedOverlay);
      matrixStackIn.pop();
      
      // render tail
      this.getEntityModel().renderKittyTail(matrixStackIn, vertexBuilder, packedLightIn, packedOverlay, limbSwing, limbSwingAmount);
    }
  }
}