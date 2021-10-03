package com.mcmoddev.golems.render.layer;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.golem_models.GolemRenderSettings;
import com.mcmoddev.golems.golem_models.LayerRenderSettings;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.render.GolemModel;
import com.mcmoddev.golems.render.GolemRenderType;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.vector.Vector3f;

public class ColoredTextureLayer<T extends GolemBase> extends LayerRenderer<T, GolemModel<T>> {
  
  private static final Vector3f ONE = new Vector3f(1.0F, 1.0F, 1.0F);

  private final GolemModel<T> layerModel;
  
  /**
   * Renders all of the textures in the golem render settings
   * @param renderParent the parent renderer
   **/
  public ColoredTextureLayer(IEntityRenderer<T, GolemModel<T>> renderParent) {
    super(renderParent);
    this.layerModel = new GolemModel<>();
  }

  @Override
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity,
					 float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    GolemRenderSettings settings = ExtraGolems.GOLEM_RENDER_SETTINGS.get(entity.getMaterial()).orElse(GolemRenderSettings.EMPTY);
    if(!entity.isInvisible() && !getEntityModel().disableLayers() && !settings.getLayers().isEmpty()) {
      getEntityModel().copyModelAttributesTo(layerModel);
      layerModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
      layerModel.setRotationAngles(entity, limbSwing, limbSwingAmount, partialTicks, netHeadYaw, headPitch);
      // render all of the layers in the LayerRenderSettings
      settings.getLayers().forEach(l -> renderTexture(layerModel, settings, l, matrixStackIn, bufferIn, packedLightIn, entity));
    }
  }
  
  protected static <G extends GolemBase> void renderTexture(GolemModel<G> model, GolemRenderSettings settings, LayerRenderSettings layer, 
      MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, G entity) {
    matrixStackIn.push();
    final ResourcePair texture = layer.getTexture();
    // get packed light and a vertex builder bound to the correct texture
    final int packedLight = layer.getLight().orElse(settings.getBaseLight().orElse(false)) ? 15728880 : packedLightIn;
    final RenderType renderType;
    if(layer.isTranslucent()) {
      renderType = GolemRenderType.getGolemTranslucent(texture.resource(), layer.getTemplate(), !texture.flag());
    } else {
      renderType = GolemRenderType.getGolemCutout(texture.resource(), layer.getTemplate(), !texture.flag());
    }
    final IVertexBuilder vertexBuilder = bufferIn.getBuffer(renderType);
    if(layer.isTranslucent()) {
      RenderSystem.enableBlend();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.5F);
    }
    final Vector3f colors;
    if(layer.getColor().isPresent() && layer.getColor().get() >= 0) {
      colors = GolemRenderSettings.unpackColor(layer.getColor().get());
    } else if (settings.getBaseColor().isPresent() && settings.getBaseColor().get() >= 0) {
      colors = GolemRenderSettings.unpackColor(settings.getBaseColor().get());
    } else {
      colors = ONE;
    }
    model.setColor(colors.getX(), colors.getY(), colors.getZ());
    model.render(matrixStackIn, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    if(layer.isTranslucent()) {
      RenderSystem.disableBlend();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
    matrixStackIn.pop();
  }
}
