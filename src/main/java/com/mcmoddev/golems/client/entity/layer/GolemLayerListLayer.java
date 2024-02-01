package com.mcmoddev.golems.client.entity.layer;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.data.model.Layer;
import com.mcmoddev.golems.data.model.LayerList;
import com.mcmoddev.golems.data.model.RenderTypes;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.client.entity.GolemModel;
import com.mcmoddev.golems.client.entity.GolemRenderType;
import com.mcmoddev.golems.client.entity.GolemRenderer;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Locale;
import java.util.Optional;

public class GolemLayerListLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {

	private final GolemModel<T> layerModel;

	/**
	 * Renders all layers in the golem {@link LayerList}
	 *
	 * @param renderParent the parent renderer
	 * @param modelSet     the model set used to bake a new model
	 **/
	public GolemLayerListLayer(RenderLayerParent<T, GolemModel<T>> renderParent, EntityModelSet modelSet) {
		super(renderParent);
		this.layerModel = new GolemModel<>(modelSet.bakeLayer(GolemRenderer.GOLEM_MODEL_RESOURCE));
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity,
					   float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		// validate no texture override exists
		if (GolemTextureOverrideLayer.getOverrideTexture(entity).isPresent()) {
			return;
		}
		// validate not invisible
		final Minecraft mc = Minecraft.getInstance();
		if (mc.player != null && entity.isInvisibleTo(mc.player)) {
			return;
		}
		// validate container
		final Optional<GolemContainer> oContainer = entity.getContainer();
		if(oContainer.isEmpty()) {
			return;
		}
		// load layer list
		final LayerList layers = oContainer.get().getModel();
		if(layers.getLayers().isEmpty()) {
			return;
		}
		// prepare to render
		getParentModel().copyPropertiesTo(layerModel);
		layerModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
		layerModel.setupAnim(entity, limbSwing, limbSwingAmount, partialTicks, netHeadYaw, headPitch);
		// render all layers
		int packedOverlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
		layers.get(entity.level().registryAccess()).forEach(layer -> renderTexture(entity, layerModel, layer, poseStack, bufferSource, packedLight, packedOverlay));
		// render special layers
		if(ExtraGolems.CONFIG.pride() || ChatFormatting.stripFormatting(entity.getName().getString()).toLowerCase(Locale.ENGLISH).startsWith("lgbt")) {
			renderTexture(entity, layerModel, Layer.RAINBOW, poseStack, bufferSource, packedLight, packedOverlay);
		}
	}

	/**
	 * Renders an individual texture using the given LayerRenderSettings
	 *
	 * @param entity the entity
	 * @param model the parent model
	 * @param layer the layer
	 * @param poseStack the pose stack
	 * @param bufferSource  the buffer source
	 * @param packedLightIn the packed light amount
	 * @param <G> the golem entity
	 */
	protected static <G extends GolemBase> void renderTexture(G entity, GolemModel<G> model, Layer layer, PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn, int packedOverlayIn) {
		poseStack.pushPose();
		// determine packed light
		final int packedLight = layer.isEmissive() ? LightTexture.FULL_BRIGHT : packedLightIn;
		// determine render type and create vertex consumer
		final RenderType renderType = getRenderType(layer.getRenderType(), layer.getTexture(), layer.getTemplate());
		final VertexConsumer vertexBuilder = bufferSource.getBuffer(renderType);
		// enable translucency if needed
		if (layer.getRenderType() == RenderTypes.TRANSLUCENT) {
			RenderSystem.enableBlend();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
		}
		// determine model color
		final Vector3f colors;
		if(layer.useBiomeColor()) {
			// unpack biome color
			colors = Vec3.fromRGB24(entity.getBiomeColor()).toVector3f();
		} else {
			// use layer color
			colors = layer.getColors().toVector3f();
		}
		// set model color
		model.setColor(colors.x(), colors.y(), colors.z());
		// render model
		model.renderToBuffer(poseStack, vertexBuilder, packedLight, packedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
		// disable translucency if needed
		if (layer.getRenderType() == RenderTypes.TRANSLUCENT) {
			RenderSystem.disableBlend();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
		poseStack.popPose();
	}

	protected static RenderType getRenderType(final RenderTypes type, final ResourcePair texture, final ResourceLocation template) {
		switch (type) {
			case TRANSLUCENT: return GolemRenderType.getGolemTranslucent(texture.resource(), template, !texture.flag());
			case SOLID: case CUTOUT: default: return GolemRenderType.getGolemCutout(texture.resource(), template, !texture.flag());
		}
	}
}
