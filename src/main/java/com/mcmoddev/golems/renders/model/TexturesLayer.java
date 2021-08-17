package com.mcmoddev.golems.renders.model;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.GolemRenderSettings;
import com.mcmoddev.golems.util.LayerRenderSettings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class TexturesLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {
  
  private final RenderLayerParent<T, GolemModel<T>> renderParent;
  private final GolemModel<T> layerModel;
  
  /**
   * Renders all of the textures in the golem render settings
   * @param ientityrenderer the parent renderer
   **/
  public TexturesLayer(RenderLayerParent<T, GolemModel<T>> renderParent, EntityModelSet modelSet) {
    super(renderParent);
    this.renderParent = renderParent;
    this.layerModel = new GolemModel<>(modelSet.bakeLayer(new ModelLayerLocation(new ResourceLocation(ExtraGolems.MODID, "entity"), "main")));
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
    final Vector3f colors = GolemRenderSettings.unpackColor(layer.getColor().orElse(settings.getBaseColor()));
    // get packed light and a vertex builder bound to the correct texture
    final int packedLight = layer.getLight().orElse(settings.getBaseLight().orElse(false)) ? 15728880 : packedLightIn;
    final VertexConsumer vertexBuilder = bufferIn.getBuffer(layer.isTranslucent() ? RenderType.entityTranslucent(texture) : RenderType.entityCutout(texture));
    if(layer.isTranslucent()) {
      // TODO ??
//      RenderSystem.defaultAlphaFunc(); // alphaFunc(516, 0.1F);
//      RenderSystem.enableBlend();
//      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.25F);
    }
    model.renderToBuffer(matrixStackIn, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, colors.x(), colors.y(), colors.z(), 1.0F);
//    if(layer.isTranslucent()) {
//      RenderSystem.disableBlend();
//    }
    matrixStackIn.popPose();
  }
}
