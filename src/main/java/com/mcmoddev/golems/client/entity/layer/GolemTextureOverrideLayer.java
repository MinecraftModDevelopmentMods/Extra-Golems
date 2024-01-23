package com.mcmoddev.golems.client.entity.layer;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.data.model.Layer;
import com.mcmoddev.golems.data.model.LayerList;
import com.mcmoddev.golems.data.model.RenderTypes;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.client.entity.GolemModel;
import com.mcmoddev.golems.client.entity.GolemRenderType;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.Locale;
import java.util.Optional;

public class GolemTextureOverrideLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {

	protected static final ResourceLocation BONE_SKELETON = new ResourceLocation(ExtraGolems.MODID, "textures/entity/golem/bone_skeleton.png");
	protected static final ResourceLocation GANON = new ResourceLocation(ExtraGolems.MODID, "textures/entity/golem/ganon.png");
	protected static final ResourceLocation COOKIE = new ResourceLocation(ExtraGolems.MODID, "textures/entity/golem/cookie.png");
	protected static final ResourceLocation YETI = new ResourceLocation(ExtraGolems.MODID, "textures/entity/golem/yeti.png");

	/**
	 * Renders all layers in the golem {@link LayerList}
	 *
	 * @param renderParent the parent renderer
	 **/
	public GolemTextureOverrideLayer(RenderLayerParent<T, GolemModel<T>> renderParent) {
		super(renderParent);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity,
					   float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		// validate not invisible
		final Minecraft mc = Minecraft.getInstance();
		if (mc.player != null && entity.isInvisibleTo(mc.player)) {
			return;
		}
		final Optional<ResourceLocation> override = getOverrideTexture(entity);
		if(override.isEmpty()) {
			return;
		}
		// prepare to render
		getParentModel().resetColor();
		renderColoredCutoutModel(getParentModel(), override.get(), poseStack, bufferSource, packedLight, entity, 1.0F, 1.0F, 1.0F);
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
		// set model color
		final Vector3f colors = layer.getColors().toVector3f();
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

	public static <G extends GolemBase> Optional<ResourceLocation> getOverrideTexture(final G entity) {
		// check time overrides
		if (ExtraGolems.CONFIG.halloween() && isNightTime(entity)) {
			return Optional.of(BONE_SKELETON);
		}
		// check name overrides
		final String name = ChatFormatting.stripFormatting(entity.getName().getString()).toLowerCase(Locale.ENGLISH);
		if("ganon".equals(name) || "ganondorf".equalsIgnoreCase(name)) {
			return Optional.of(GANON);
		}
		if("cookie".equals(name)) {
			return Optional.of(COOKIE);
		}
		if("yeti".equals(name)) {
			return Optional.of(YETI);
		}
		// no checks passed
		return Optional.empty();
	}

	private static boolean isNightTime(final GolemBase golem) {
		final long time = golem.level().getDayTime() % 24000L;
		return time > 13000L && time < 23000L;
	}
}
