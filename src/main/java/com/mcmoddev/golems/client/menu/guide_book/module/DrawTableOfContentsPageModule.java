package com.mcmoddev.golems.client.menu.guide_book.module;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
	public void render(Screen parent, GuiGraphics graphics, float partialTicks) {
		drawBackground(parent, graphics);
		drawBasicPage(graphics, title, body);
		drawPageNum(graphics);
	}

	protected void drawPageNum(final GuiGraphics graphics) {
		super.drawPageNum(graphics);
	}

	protected void drawBackground(final Screen parent, final GuiGraphics graphics) {
		// draw background
		graphics.blit(texture, x + margin + 1, y + margin * 2 - 1, u, v, tableWidth, tableHeight);
	}
}
