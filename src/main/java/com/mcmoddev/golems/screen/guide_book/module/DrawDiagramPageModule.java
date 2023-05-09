package com.mcmoddev.golems.screen.guide_book.module;

import com.mcmoddev.golems.EGRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class DrawDiagramPageModule extends DrawPageModule {

	protected final DrawBlockModule drawBlockModule;

	public DrawDiagramPageModule(DrawBlockModule drawBlockModule, Font font, int width, int height, int margin) {
		super(font, width, height, margin);
		this.drawBlockModule = drawBlockModule;
	}

	@Override
	public void render(Screen parent, PoseStack poseStack, float partialTicks) {
		drawGolemDiagram(parent, poseStack);
		drawPageNum(poseStack);
	}

	protected void drawGolemDiagram(final Screen parent, final PoseStack matrix) {
		Block golemBody = Blocks.IRON_BLOCK;
		Block golemHead = EGRegistry.GOLEM_HEAD.get();
		float scale = 2.0F;
		final int blockW = (int) (8.0F * scale);
		int startX = x + (width / 8);
		int startY = y + blockW;
		// head
		this.drawBlockModule.withScale(scale);
		this.drawBlockModule.withBlock(golemHead).withPos(startX, startY).render(parent, matrix, 0);
		// middle-bottom
		startY += blockW * 4;
		this.drawBlockModule.withBlock(golemBody);
		this.drawBlockModule.withPos(startX, startY).render(parent, matrix, 0);
		// arm-right
		startX += blockW * 2;
		startY -= (blockW * 5) / 2;
		this.drawBlockModule.withPos(startX, startY).render(parent, matrix, 0);
		// middle-top
		startX -= blockW * 2;
		startY += (blockW / 2);
		this.drawBlockModule.withPos(startX, startY).render(parent, matrix, 0);
		// arm-left
		startX -= blockW * 2;
		startY += (blockW / 2);
		this.drawBlockModule.withPos(startX, startY).render(parent, matrix, 0);
	}

}
