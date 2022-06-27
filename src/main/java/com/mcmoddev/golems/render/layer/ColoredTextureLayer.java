package com.mcmoddev.golems.render.layer;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.render.GolemRenderSettings;
import com.mcmoddev.golems.container.render.LayerRenderSettings;
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
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

import java.util.ArrayList;
import java.util.List;

public class ColoredTextureLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {

	private static final Vector3f ONE = new Vector3f(1.0F, 1.0F, 1.0F);

	private final GolemModel<T> layerModel;

	/**
	 * Renders all of the textures in the golem render settings
	 *
	 * @param renderParent the parent renderer
	 * @param modelSet     the model set used to bake a new model
	 **/
	public ColoredTextureLayer(RenderLayerParent<T, GolemModel<T>> renderParent, EntityModelSet modelSet) {
		super(renderParent);
		this.layerModel = new GolemModel<>(modelSet.bakeLayer(GolemRenderer.GOLEM_MODEL_RESOURCE));
	}

	@Override
	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entity,
					   float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		GolemRenderSettings settings = ExtraGolems.GOLEM_RENDER_SETTINGS.get(entity.getMaterial()).orElse(GolemRenderSettings.EMPTY);
		// prepare to render each layer
		if (!entity.isInvisible() && !getParentModel().disableLayers() && !settings.getLayers().isEmpty()) {
			getParentModel().copyPropertiesTo(layerModel);
			layerModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
			layerModel.setupAnim(entity, limbSwing, limbSwingAmount, partialTicks, netHeadYaw, headPitch);
			// render all of the layers in the LayerRenderSettings
			int packedOverlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
			settings.getLayers().forEach(l -> renderTexture(entity, layerModel, settings, l, matrixStackIn, bufferIn, packedLightIn, packedOverlay));
		}
	}

	/**
	 * Renders an individual texture using the given LayerRenderSettings
	 * @param entity the entity
	 * @param model the parent model
	 * @param settings the parent render settings
	 * @param layer the layer render settings
	 * @param matrixStackIn the pose stack
	 * @param bufferIn the buffer source
	 * @param packedLightIn the packed light amount
	 * @param <G> the golem entity
	 */
	protected static <G extends GolemBase> void renderTexture(G entity, GolemModel<G> model, GolemRenderSettings settings, LayerRenderSettings layer,
															  PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, int packedOverlayIn) {
		matrixStackIn.pushPose();
		final ResourcePair texture = layer.getTexture();
		// get packed light and a vertex builder bound to the correct texture
		final int packedLight = layer.getLight().orElse(settings.getBaseLight().orElse(false)) ? 15728880 : packedLightIn;
		final RenderType renderType;
		if (layer.isTranslucent()) {
			renderType = GolemRenderType.getGolemTranslucent(texture.resource(), layer.getTemplate(), !texture.flag());
		} else {
			renderType = GolemRenderType.getGolemCutout(texture.resource(), layer.getTemplate(), !texture.flag());
		}
		final VertexConsumer vertexBuilder = bufferIn.getBuffer(renderType);
		if (layer.isTranslucent()) {
			RenderSystem.enableBlend();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
		}
		final Vector3f colors;
		if (layer.getColor().isPresent() && layer.getColor().get() >= 0) {
			colors = GolemRenderSettings.unpackColor(layer.getColor().get());
		} else if (settings.getBaseColor().isPresent() && settings.getBaseColor().get() >= 0) {
			colors = GolemRenderSettings.unpackColor(settings.getBaseColor().get());
		} else {
			colors = ONE;
		}
		model.setColor(colors.x(), colors.y(), colors.z());
		model.renderToBuffer(matrixStackIn, vertexBuilder, packedLight, packedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
		if (layer.isTranslucent()) {
			RenderSystem.disableBlend();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
		matrixStackIn.popPose();
	}
}
