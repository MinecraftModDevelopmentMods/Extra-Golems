package com.mcmoddev.golems.client.menu.guide_book.module;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class DrawPageModule extends DrawModule {

	protected final Font font;
	protected final int width;
	protected final int height;
	protected final int margin;
	
	protected int page;
	// unused
	protected int maxPages;
	protected Component title;
	protected @Nullable Component subtitle;
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

	public DrawPageModule withSubtitle(final @Nullable Component subtitle) {
		this.subtitle = subtitle;
		return this;
	}
	
	public DrawPageModule withBody(final Component body) {
		this.body = body;
		return this;
	}

	@Override
	public void render(Screen parent, GuiGraphics graphics, float partialTicks) {
		drawBasicPage(graphics, title, body);
		drawPageNum(graphics);
	}

	protected void drawPageNum(GuiGraphics graphics) {
		final boolean isRight = (page % 2) == 1;
		final int posX = isRight ? x + margin * 2 : x + width / 2 - margin * 2;
		final int posY = y + height - 18;
		final String sPage = String.valueOf(page + 1);
		final int sWidth = isRight ? this.font.width(sPage) : 0;
		graphics.drawString(font, sPage, posX - sWidth, posY, 0, false);
	}

	protected void drawBasicPage(GuiGraphics graphics, Component title, Component body) {
		final int maxWidth = (width / 2) - (margin * 2);

		// TODO clean up to properly display title and subtitle
		int posX = x + margin + 4;
		int posY = y + margin;
		int sWidth = this.font.width(title.getString());
		if (sWidth > maxWidth) {
			// draw title wrapped
			graphics.drawWordWrap(font, title, posX, posY, maxWidth, 0);
		} else {
			// draw title centered
			graphics.drawString(font, title, posX + ((maxWidth - sWidth) / 2), posY, 0, false);
		}
		// draw subtitle
		posY += margin;
		if(this.subtitle != null) {
			graphics.drawString(font, subtitle, posX, posY, 0, false);
		}
		// draw body
		posY += margin;
		graphics.drawWordWrap(font, body, posX, posY, maxWidth, 0);
	}
}
