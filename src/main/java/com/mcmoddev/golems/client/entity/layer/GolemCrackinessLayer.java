package com.mcmoddev.golems.client.entity.layer;

import com.google.common.collect.ImmutableMap;
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
import net.minecraft.world.entity.animal.IronGolem;

import java.util.Map;

public class GolemCrackinessLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {

	private static final Map<IronGolem.Crackiness, ResourceLocation> resourceLocations = ImmutableMap.of(
			IronGolem.Crackiness.LOW, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_low.png"),
			IronGolem.Crackiness.MEDIUM, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_medium.png"),
			IronGolem.Crackiness.HIGH, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_high.png"));

	public GolemCrackinessLayer(RenderLayerParent<T, GolemModel<T>> ientityrenderer) {
		super(ientityrenderer);
	}

	@Override
	public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, T entity,
					   float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!entity.isInvisible()) {
			IronGolem.Crackiness irongolem$crackiness = entity.getCrackiness();
			if (irongolem$crackiness != IronGolem.Crackiness.NONE) {
				ResourceLocation resourcelocation = resourceLocations.get(irongolem$crackiness);
				VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(resourcelocation));
				stack.pushPose();
				RenderSystem.enableBlend();
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
				getParentModel().renderToBuffer(stack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 0.5F);
				RenderSystem.disableBlend();
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				stack.popPose();
			}
		}
	}
}
