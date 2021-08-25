package com.mcmoddev.golems.render.layer;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.client.GolemRenderSettings;
import com.mcmoddev.golems.container.client.LayerRenderSettings;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.render.GolemModel;
import com.mcmoddev.golems.render.GolemRenderType;
import com.mcmoddev.golems.render.GolemRenderer;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class ColoredTextureLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {
  
  private static final Vector3f ONE = new Vector3f(1.0F, 1.0F, 1.0F);

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
    GolemRenderSettings settings = ExtraGolems.GOLEM_RENDER_SETTINGS.get(entity.getMaterial()).orElse(GolemRenderSettings.EMPTY);
    if(!entity.isInvisible() && !getParentModel().disableLayers() && !settings.getLayers().isEmpty()) {
      getParentModel().copyPropertiesTo(layerModel);
      layerModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
      layerModel.setupAnim(entity, limbSwing, limbSwingAmount, partialTicks, netHeadYaw, headPitch);
      // render all of the layers in the LayerRenderSettings
      settings.getLayers().forEach(l -> renderTexture(layerModel, settings, l, matrixStackIn, bufferIn, packedLightIn, entity));
    }
  }
  
  protected static <G extends GolemBase> void renderTexture(GolemModel<G> model, GolemRenderSettings settings, LayerRenderSettings layer, 
      PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, G entity) {
    matrixStackIn.pushPose();
    final ResourcePair texture = layer.getTexture();
    // get packed light and a vertex builder bound to the correct texture
    final int packedLight = layer.getLight().orElse(settings.getBaseLight().orElse(false)) ? 15728880 : packedLightIn;
    final RenderType renderType;
    if(layer.isTranslucent()) {
      renderType = GolemRenderType.getGolemTranslucent(texture.resource(), layer.getTemplate(), !texture.flag());
    } else {
      renderType = GolemRenderType.getGolemCutout(texture.resource(), layer.getTemplate(), !texture.flag());
    }
    final VertexConsumer vertexBuilder = bufferIn.getBuffer(renderType);
    if(layer.isTranslucent()) {
      RenderSystem.enableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
    }
    final Vector3f colors;
    if(layer.getColor().isPresent() && layer.getColor().get() >= 0) {
      colors = GolemRenderSettings.unpackColor(layer.getColor().get());
    } else if (settings.getBaseColor().isPresent() && settings.getBaseColor().get() >= 0) {
      colors = GolemRenderSettings.unpackColor(settings.getBaseColor().get());
    } else {
      colors = ONE;
    }
    model.setColor(colors.x(), colors.y(), colors.z());
    model.renderToBuffer(matrixStackIn, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    if(layer.isTranslucent()) {
      RenderSystem.disableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
    matrixStackIn.popPose();
  }
}
