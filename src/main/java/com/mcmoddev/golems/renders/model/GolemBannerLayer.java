package com.mcmoddev.golems.renders.model;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class GolemBannerLayer<T extends GolemBase> extends LayerRenderer<T, GolemModel<T>> {

  public GolemBannerLayer(IEntityRenderer<T, GolemModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    if (!entity.isInvisible() && entity.getBanner().getItem() instanceof BannerItem) {
      matrixStackIn.push();
      // position the banner
      matrixStackIn.translate(-0.09375D, -0.075D, 0.3525D);
      matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180.0F));
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0F));
      // animation
      final float bannerSwing = 0.1F + MathHelper.cos((ageInTicks + partialTicks) * 0.07F) * (limbSwingAmount + 0.1F) * 0.2F;
      matrixStackIn.translate(0, 0.5D, 0);
      matrixStackIn.rotate(Vector3f.ZP.rotation(bannerSwing));
      matrixStackIn.translate(0, -0.5D, 0);
      // scale and center on golem body
      matrixStackIn.scale(2.6F, 2.6F, 2.6F);
      getEntityModel().ironGolemBody.translateRotate(matrixStackIn);
      // Actually render the banner item
      Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entity, entity.getBanner(), ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, matrixStackIn, bufferIn, packedLightIn);
      matrixStackIn.pop();
    }
  }
}