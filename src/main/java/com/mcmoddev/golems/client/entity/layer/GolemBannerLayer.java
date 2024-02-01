package com.mcmoddev.golems.client.entity.layer;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.client.entity.GolemModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GolemBannerLayer<T extends GolemBase> extends RenderLayer<T, GolemModel<T>> {

	public GolemBannerLayer(RenderLayerParent<T, GolemModel<T>> ientityrenderer) {
		super(ientityrenderer);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn, T entity,
					   float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		ItemStack itemStack = entity.getItemBySlot(EquipmentSlot.CHEST);
		if (itemStack.is(ItemTags.BANNERS)) {
			poseStack.pushPose();
			// position the banner
			poseStack.translate(0, 0.5825D, 0.3D); // 0.8725D
			poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
			poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
			// animation
			final float bannerSwing = 0.1F + Mth.cos((ageInTicks + partialTicks) * 0.07F) * (limbSwingAmount + 0.1F) * 0.2F;
			poseStack.translate(0, 1.5D, 0.0625D);
			poseStack.mulPose(Axis.ZP.rotation(bannerSwing));
			poseStack.translate(0, -1.5D, -0.0625D);
			// scale and center on entity body
			poseStack.scale(2.6F, 2.3F, 2.6F);
			getParentModel().root().translateAndRotate(poseStack);
			// Actually render the banner item
			Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer().renderItem(entity, itemStack, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, false, poseStack, bufferSource, packedLightIn);
			poseStack.popPose();
		}
	}
}
