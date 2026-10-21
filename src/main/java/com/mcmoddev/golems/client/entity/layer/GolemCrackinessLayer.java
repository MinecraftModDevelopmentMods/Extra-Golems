package com.mcmoddev.golems.client.entity.layer;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.client.entity.GolemModel;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class GolemCrackinessLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {

	private static final ResourceLocation LOW = ResourceLocation.parse("textures/entity/iron_golem/iron_golem_crackiness_low.png");
	private static final ResourceLocation MEDIUM = ResourceLocation.parse("textures/entity/iron_golem/iron_golem_crackiness_medium.png");
	private static final ResourceLocation HIGH = ResourceLocation.parse("textures/entity/iron_golem/iron_golem_crackiness_high.png");

	public GolemCrackinessLayer(RenderLayerParent<T, GolemModel<T>> ientityrenderer) {
		super(ientityrenderer);
	}

	@Override
	public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, T entity,
					   float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!entity.isInvisible()) {
			ResourceLocation crackinessTexture = getCrackinessTexture(entity);
			if (crackinessTexture != null) {
				VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(crackinessTexture));
				stack.pushPose();
				RenderSystem.enableBlend();
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
				getParentModel().renderToBuffer(stack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 0x80FFFFFF);
				RenderSystem.disableBlend();
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				stack.popPose();
			}
		}
	}

	private ResourceLocation getCrackinessTexture(T entity) {
		float healthPercent = entity.getHealth() / entity.getMaxHealth();
		if (healthPercent < 0.25F) {
			return HIGH;
		} else if (healthPercent < 0.5F) {
			return MEDIUM;
		} else if (healthPercent < 0.75F) {
			return LOW;
		}
		return null;
	}
}
