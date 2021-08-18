package com.mcmoddev.golems.render.layer;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.client.GolemRenderSettings;
import com.mcmoddev.golems.container.client.LayerRenderSettings;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.render.GolemModel;
import com.mcmoddev.golems.render.GolemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ColoredTextureLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {
  
  private final GolemModel<T> layerModel;
  
  /**
   * Renders all of the textures in the golem render settings
   * @param ientityrenderer the parent renderer
   **/
  public ColoredTextureLayer(RenderLayerParent<T, GolemModel<T>> renderParent, EntityModelSet modelSet) {
    super(renderParent);
    this.layerModel = new GolemModel<>(modelSet.bakeLayer(GolemRenderer.GOLEM_MODEL_RESOURCE));
  }

  @Override
  public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entity,
      float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    GolemRenderSettings settings = ExtraGolems.PROXY.GOLEM_RENDER_SETTINGS.get(entity.getMaterial()).orElse(GolemRenderSettings.EMPTY);
    if(!entity.isInvisible() && !settings.getLayers().isEmpty()) {
      getParentModel().copyPropertiesTo(layerModel);
      layerModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
      layerModel.setupAnim(entity, limbSwing, limbSwingAmount, partialTicks, netHeadYaw, headPitch);

      settings.getLayers().forEach(l -> renderTexture(layerModel, settings, l, matrixStackIn, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch));
    }
  }
  
  protected static void renderTexture(GolemModel<? extends GolemBase> model, GolemRenderSettings settings, LayerRenderSettings layer, 
      PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, float limbSwing, 
      float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    matrixStackIn.pushPose();
    final ResourceLocation texture = layer.getTexture();
    // get packed light and a vertex builder bound to the correct texture
    final int packedLight = layer.getLight().orElse(settings.getBaseLight().orElse(false)) ? 15728880 : packedLightIn;
    final VertexConsumer vertexBuilder = bufferIn.getBuffer(layer.isTranslucent() ? RenderType.entityTranslucent(texture) : RenderType.entityCutout(texture));
    if(layer.isTranslucent()) {
      // TODO ??
//      RenderSystem.defaultAlphaFunc(); // alphaFunc(516, 0.1F);
//      RenderSystem.enableBlend();
      
    }
    final Vector3f colors;
    if(layer.getColor().isPresent()) {
      colors = GolemRenderSettings.unpackColor(layer.getColor().get());
      model.setColor(colors.x(), colors.y(), colors.z());
    } else if (settings.getBaseColor().isPresent()) {
      colors = GolemRenderSettings.unpackColor(settings.getBaseColor().get());
      model.setColor(colors.x(), colors.y(), colors.z());
    } else {
      model.resetColor();
    }
    model.renderToBuffer(matrixStackIn, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
//    if(layer.isTranslucent()) {
//      RenderSystem.disableBlend();
//    }
    matrixStackIn.popPose();
  }
}
