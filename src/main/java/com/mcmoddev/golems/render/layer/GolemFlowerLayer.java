package com.mcmoddev.golems.render.layer;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.render.GolemModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;

public class GolemFlowerLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {

	public GolemFlowerLayer(RenderLayerParent<T, GolemModel<T>> ientityrenderer) {
		super(ientityrenderer);
	}

	@Override
	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entitylivingbaseIn,
					   float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		// copy
		if (entitylivingbaseIn.getOfferFlowerTick() != 0) {
			matrixStackIn.pushPose();
			ModelPart modelrenderer = this.getParentModel().getFlowerHoldingArm();
			modelrenderer.translateAndRotate(matrixStackIn);
			matrixStackIn.translate(-1.1875D, 1.0625D, -0.9375D);
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
			matrixStackIn.scale(0.5F, 0.5F, 0.5F);
			matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
			matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.POPPY.defaultBlockState(), matrixStackIn, bufferIn,
					packedLightIn, OverlayTexture.NO_OVERLAY, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
			matrixStackIn.popPose();
		}
	}
}
