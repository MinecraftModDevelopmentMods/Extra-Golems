package com.mcmoddev.golems.client.entity.layer;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.client.entity.GolemModel;
import com.mcmoddev.golems.data.model.LayerList;
import com.mcmoddev.golems.entity.GolemBase;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.Optional;

public class GolemTextureOverrideLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {

	private static final ResourceLocation BONE_SKELETON = new ResourceLocation(ExtraGolems.MODID, "textures/entity/golem/bone_skeleton.png");
	private static final ResourceLocation GANON = new ResourceLocation(ExtraGolems.MODID, "textures/entity/golem/ganon.png");
	private static final ResourceLocation COOKIE = new ResourceLocation(ExtraGolems.MODID, "textures/entity/golem/cookie.png");
	private static final ResourceLocation YETI = new ResourceLocation(ExtraGolems.MODID, "textures/entity/golem/yeti.png");
	private static final ResourceLocation HARAMBE = new ResourceLocation(ExtraGolems.MODID, "textures/entity/golem/harambe.png");

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

	public static <G extends GolemBase> Optional<ResourceLocation> getOverrideTexture(final G entity) {
		// check time overrides
		if (ExtraGolems.CONFIG.halloween() && isNightTime(entity)) {
			return Optional.of(BONE_SKELETON);
		}
		// check name overrides
		final String name = ChatFormatting.stripFormatting(entity.getName().getString()).toLowerCase(Locale.ENGLISH);
		if("ganon".equalsIgnoreCase(name) || "ganondorf".equalsIgnoreCase(name)) {
			return Optional.of(GANON);
		}
		if("cookie".equalsIgnoreCase(name)) {
			return Optional.of(COOKIE);
		}
		if("yeti".equalsIgnoreCase(name)) {
			return Optional.of(YETI);
		}
		if("harambe".equalsIgnoreCase(name)) {
			return Optional.of(HARAMBE);
		}
		// no checks passed
		return Optional.empty();
	}

	private static boolean isNightTime(final GolemBase golem) {
		final long time = golem.level().getDayTime() % 24000L;
		return time > 13000L && time < 23000L;
	}
}
