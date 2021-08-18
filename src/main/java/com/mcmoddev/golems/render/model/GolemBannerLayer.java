package com.mcmoddev.golems.render.model;

import com.mcmoddev.golems.EGConfig;
import com.mcmoddev.golems.entity.GolemBase;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.BannerItem;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;

public class GolemBannerLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {

  public GolemBannerLayer(RenderLayerParent<T, GolemModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entity,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    if (!entity.isInvisible() && entity.getBanner().getItem() instanceof BannerItem) {
      // check for holiday tweaks
      if(EGConfig.aprilFirst()) {
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      }
      matrixStackIn.pushPose();
      // position the banner
      matrixStackIn.translate(-0.09375D, -0.075D, 0.3525D);
      matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180.0F));
      matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90.0F));
      // animation
      final float bannerSwing = 0.1F + Mth.cos((ageInTicks + partialTicks) * 0.07F) * (limbSwingAmount + 0.1F) * 0.2F;
      matrixStackIn.translate(0, 0.5D, 0);
      matrixStackIn.mulPose(Vector3f.ZP.rotation(bannerSwing));
      matrixStackIn.translate(0, -0.5D, 0);
      // scale and center on entity body
      matrixStackIn.scale(2.6F, 2.6F, 2.6F);
      getParentModel().root().translateAndRotate(matrixStackIn);
      // Actually render the banner item
      Minecraft.getInstance().getItemInHandRenderer().renderItem(entity, entity.getBanner(), ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, matrixStackIn, bufferIn, packedLightIn);
      matrixStackIn.popPose();
    }
  }
}