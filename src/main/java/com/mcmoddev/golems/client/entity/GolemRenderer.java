package com.mcmoddev.golems.client.entity;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.client.entity.layer.GolemBannerLayer;
import com.mcmoddev.golems.client.entity.layer.GolemCrackinessLayer;
import com.mcmoddev.golems.client.entity.layer.GolemFlowerLayer;
import com.mcmoddev.golems.client.entity.layer.GolemKittyLayer;
import com.mcmoddev.golems.client.entity.layer.GolemLayerListLayer;
import com.mcmoddev.golems.client.entity.layer.GolemTextureOverrideLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GolemRenderer<T extends GolemBase> extends MobRenderer<T, GolemModel<T>> {

	public static final ModelLayerLocation GOLEM_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(ExtraGolems.MODID, "golem"), "main");
	private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

	/**
	 * @param context the entity render manager
	 **/
	public GolemRenderer(final EntityRendererProvider.Context context) {
		super(context, new GolemModel<>(context.bakeLayer(GOLEM_MODEL_RESOURCE)), 0.5F);
		this.addLayer(new GolemLayerListLayer<>(this, context.getModelSet()));
		this.addLayer(new GolemTextureOverrideLayer<>(this));
		this.addLayer(new GolemCrackinessLayer<>(this));
		this.addLayer(new GolemFlowerLayer<>(this));
		this.addLayer(new GolemKittyLayer<>(this));
		this.addLayer(new GolemBannerLayer<>(this));
	}

	@Override
	protected void setupRotations(T entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
		// set up parent rotations
		super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick);
		// flip upside down
		if (ExtraGolems.CONFIG.aprilFirst()) {
			poseStack.translate(0.0F, entity.getBbHeight() + 0.1F, 0.0F);
			poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		}
		// update walk animation
		if (!(entity.walkAnimation.speed() < 0.01D)) {
			float maxAngle = 13.0F * entity.getScale();
			float walkAnimation = entity.walkAnimation.position(partialTick) + 6.0F;
			float walkAngle = (Math.abs(walkAnimation % maxAngle - (maxAngle / 2.0F)) - 3.25F) / 3.25F;
			poseStack.mulPose(Axis.ZP.rotationDegrees((maxAngle / 2.0F) * walkAngle));
		}
	}

	@Override
	protected void scale(T entity, PoseStack poseStack, float partialTick) {
		// scale baby
		if(entity.isBaby()) {
			final float scale = entity.getScale();
			poseStack.scale(scale, scale, scale);
		}
	}

	@Override
	public void render(final T entity, final float entityYaw, final float partialTick, final PoseStack poseStack,
					   final MultiBufferSource bufferSource, final int pPackedLight) {
		// validate container
		final Optional<GolemContainer> oContainer = entity.getContainer();
		if(oContainer.isEmpty()) {
			return;
		}
		// render the entity
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, pPackedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(T pEntity) {
		return GOLEM_LOCATION;
	}

	@Nullable
	@Override
	protected RenderType getRenderType(T pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing) {
		// This prevents the model from being rendered, but layer renderers still trigger.
		return null;
	}
}
