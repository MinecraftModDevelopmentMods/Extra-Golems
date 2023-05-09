package com.mcmoddev.golems.screen.guide_book.module;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class DrawBlockModule extends DrawModule {

	protected final ItemRenderer itemRenderer;
	protected final int margin;

	protected Block block;
	protected ItemStack blockStack;
	protected float scale;

	public DrawBlockModule(ItemRenderer itemRenderer, int margin) {
		this.itemRenderer = itemRenderer;
		this.margin = margin;
	}

	/**
	 * @param block the block to draw, or {@link Blocks#AIR} to render a barrier
	 **/
	public DrawBlockModule withBlock(final Block block) {
		if(block == Blocks.AIR) {
			this.block = Blocks.BARRIER;
		} else {
			this.block = block;
		}
		this.blockStack = new ItemStack(this.block);
		return this;
	}

	public DrawBlockModule withScale(final float scale) {
		this.scale = scale;
		return this;
	}

	@Override
	public void render(final Screen parent, final PoseStack poseStack, final float partialTicks) {
		float blockX = (float) (x + margin + 4);
		float blockY = (float) (y + margin);
		// Render the Block with given scale
		RenderSystem.getModelViewStack().pushPose();
		// Scale the pose stack that will be used by the item renderer
		RenderSystem.getModelViewStack().scale(scale, scale, scale);

		// prepare to render the item stack
		Lighting.setupForFlatItems();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		// render the item stack and ensure the x and y coordinates are un-scaled
		this.itemRenderer.renderGuiItem(blockStack, (int) (blockX / scale), (int) (blockY / scale));

		RenderSystem.getModelViewStack().popPose();
		// re-apply the previous model view matrix, required to fully pop the view settings after scaling
		RenderSystem.applyModelViewMatrix();
	}
}
