package com.mcmoddev.golems.render.layer;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.render.GolemModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemDisplayContext;

public class GolemBannerLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {

	public GolemBannerLayer(RenderLayerParent<T, GolemModel<T>> ientityrenderer) {
		super(ientityrenderer);
	}

	@Override
	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entity,
					   float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!entity.isInvisible() && entity.getBanner().getItem() instanceof BannerItem) {
			// check for holiday tweaks
			if (ExtraGolems.CONFIG.aprilFirst()) {
				matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180.0F));
			}
			matrixStackIn.pushPose();
			// position the banner
			matrixStackIn.translate(-0.09375D, 0.8725D, 0.3525D); // -0.075
			matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
			matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
			// animation
			final float bannerSwing = 0.1F + Mth.cos((ageInTicks + partialTicks) * 0.07F) * (limbSwingAmount + 0.1F) * 0.2F;
			matrixStackIn.translate(0, 1.5D, 0);
			matrixStackIn.mulPose(Axis.ZP.rotation(bannerSwing));
			matrixStackIn.translate(0, -1.5D, 0);
			// scale and center on entity body
			matrixStackIn.scale(2.6F, 2.3F, 2.6F);
			getParentModel().root().translateAndRotate(matrixStackIn);
			// Actually render the banner item
			Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer().renderItem(entity, entity.getBanner(), ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, false, matrixStackIn, bufferIn, packedLightIn);
			matrixStackIn.popPose();
		}
	}
}
