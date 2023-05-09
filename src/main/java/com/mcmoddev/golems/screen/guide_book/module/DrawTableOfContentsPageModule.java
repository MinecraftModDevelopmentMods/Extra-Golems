package com.mcmoddev.golems.screen.guide_book.module;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public class DrawTableOfContentsPageModule extends DrawPageModule {

	protected final ResourceLocation texture;
	protected final int u;
	protected final int v;
	protected final int tableWidth;
	protected final int tableHeight;

	public DrawTableOfContentsPageModule(Font font, int width, int height, int margin, ResourceLocation texture,
										 int u, int v, int tableWidth, int tableHeight) {
		super(font, width, height, margin);
		this.texture = texture;
		this.u = u;
		this.v = v;
		this.tableWidth = tableWidth;
		this.tableHeight = tableHeight;
	}

	@Override
	public void render(Screen parent, PoseStack poseStack, float partialTicks) {
		drawBackground(parent, poseStack);
		drawBasicPage(poseStack, title, body);
		drawPageNum(poseStack);
	}

	protected void drawPageNum(final PoseStack poseStack) {
		super.drawPageNum(poseStack);
	}

	protected void drawBackground(final Screen parent, final PoseStack poseStack) {
		// draw background
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		parent.blit(poseStack, x + margin + 1, y + margin * 2 - 1, u, v, tableWidth, tableHeight);
	}
}
