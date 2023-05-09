package com.mcmoddev.golems.screen.guide_book.module;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DrawPageModule extends DrawModule {

	protected final Font font;
	protected final int width;
	protected final int height;
	protected final int margin;
	
	protected int page;
	// unused
	protected int maxPages;
	protected Component title;
	protected Component body;

	public DrawPageModule(Font font, int width, int height, int margin) {
		this.font = font;
		this.width = width;
		this.height = height;
		this.margin = margin;
		this.page = 0;
		this.maxPages = 1;
		this.title = Component.empty();
		this.body = Component.empty();
	}

	public DrawPageModule withPage(final int page, final int maxPages) {
		this.page = page;
		this.maxPages = maxPages;
		return this;
	}
	
	public DrawPageModule withPage(final int page) {
		this.page = page;
		return this;
	}
	
	public DrawPageModule withTitle(final Component title) {
		this.title = title;
		return this;
	}
	
	public DrawPageModule withBody(final Component body) {
		this.body = body;
		return this;
	}

	@Override
	public void render(Screen parent, PoseStack poseStack, float partialTicks) {
		drawBasicPage(poseStack, title, body);
		drawPageNum(poseStack);
	}

	protected void drawPageNum(final PoseStack poseStack) {
		final boolean isRight = (page % 2) == 1;
		final int posX = isRight ? x + margin * 2 : x + width / 2 - margin * 2;
		final int posY = y + height - 18;
		final String sPage = String.valueOf(page + 1);
		final int sWidth = isRight ? this.font.width(sPage) : 0;
		this.font.draw(poseStack, sPage, posX - sWidth, posY, 0);
	}

	protected void drawBasicPage(final PoseStack poseStack, Component title, Component body) {
		final int maxWidth = (width / 2) - (margin * 2);

		int titleX = x + margin + 4;
		int titleY = y + margin;
		int sWidth = this.font.width(title.getString());
		if (sWidth > maxWidth) {
			// draw title wrapped
			this.font.drawWordWrap(title, titleX, titleY, maxWidth, 0);
		} else {
			// draw title centered
			this.font.draw(poseStack, title.getString(), titleX + ((maxWidth - sWidth) / 2.0F), titleY, 0);
		}

		int bodyX = titleX;
		int bodyY = titleY + margin * 2;
		this.font.drawWordWrap(body, bodyX, bodyY, maxWidth, 0);
	}
}
