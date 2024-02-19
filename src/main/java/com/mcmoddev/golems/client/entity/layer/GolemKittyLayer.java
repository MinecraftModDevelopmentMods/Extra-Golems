package com.mcmoddev.golems.client.entity.layer;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.client.entity.GolemModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class GolemKittyLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ExtraGolems.MODID, "textures/entity/golem/layer/kitty_layer.png");

	public GolemKittyLayer(RenderLayerParent<T, GolemModel<T>> ientityrenderer) {
		super(ientityrenderer);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, T entity,
					   float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		// validate name and visibility
		final String name = ChatFormatting.stripFormatting(entity.getName().getString());
		final boolean isKittyVisible = entity.hasCustomName() && "kitty".equalsIgnoreCase(name);
		if(entity.isInvisible() || !isKittyVisible) {
			return;
		}
		// prepare animations
		this.getParentModel().setupKittyAnim(entity, limbSwing, limbSwingAmount, partialTicks, netHeadYaw, headPitch);
		// create vertex consumer with the texture
		VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		// render kitty layer
		this.getParentModel().renderKittyLayer(poseStack, vertexConsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F));
	}
}
