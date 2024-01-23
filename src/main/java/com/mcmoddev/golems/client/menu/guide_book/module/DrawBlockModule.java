package com.mcmoddev.golems.client.menu.guide_book.module;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class DrawBlockModule extends DrawModule {
	protected final int margin;

	protected Block block;
	protected ItemStack blockStack;
	protected float scale;

	public DrawBlockModule(int margin) {
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
	public void render(final Screen parent, final GuiGraphics graphics, final float partialTicks) {
		float blockX = (float) (x + margin + 4);
		float blockY = (float) (y + margin);
		// Render the Block with given scale
		graphics.pose().pushPose();
		// Scale the pose stack that will be used by the item renderer
		graphics.pose().scale(scale, scale, scale);

		// render the item stack and ensure the x and y coordinates are un-scaled
		graphics.renderItem(blockStack, (int) (blockX / scale), (int) (blockY / scale));
		graphics.pose().popPose();
	}
}
