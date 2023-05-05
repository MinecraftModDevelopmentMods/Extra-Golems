package com.mcmoddev.golems.render.layer;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.render.GolemModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class GolemKittyLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {

	protected static final ResourceLocation kittyTexture = new ResourceLocation(ExtraGolems.MODID, "textures/entity/golem/layer/kitty_layer.png");

	public GolemKittyLayer(RenderLayerParent<T, GolemModel<T>> ientityrenderer) {
		super(ientityrenderer);
	}

	@Override
	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T golem,
					   float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!golem.isInvisible() && getParentModel().getKitty().visible) {
			// check for holiday tweaks
			if (ExtraGolems.CONFIG.aprilFirst()) {
				matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
			}
			// get packed light and a vertex builder bound to the correct texture
			VertexConsumer vertexBuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(kittyTexture));
			// render ears
			this.getParentModel().getKitty().render(matrixStackIn, vertexBuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(golem, 0.0F));
		}
	}
}
