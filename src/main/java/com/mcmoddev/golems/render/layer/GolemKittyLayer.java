package com.mcmoddev.golems.render.layer;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.EGConfig;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.render.GolemModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
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
    if (!golem.isInvisible() && getEntityModel().getKitty().showModel) {
      // check for holiday tweaks
      if(EGConfig.aprilFirst()) {
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
      }
      // get packed light and a vertex builder bound to the correct texture
      IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(kittyTexture));
      // render ears
      this.getEntityModel().getKitty().render(matrixStackIn, vertexBuilder, packedLightIn, MobRenderer.getPackedOverlay(golem, 0.0F));
    }
  }
}
