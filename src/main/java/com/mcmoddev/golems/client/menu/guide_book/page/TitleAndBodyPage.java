package com.mcmoddev.golems.client.menu.guide_book.page;

import com.mcmoddev.golems.client.menu.guide_book.IBookScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class TitleAndBodyPage extends BookPage {

	protected @Nullable Component title;
	protected @Nullable Component body;

	public TitleAndBodyPage(Font font, int page, int x, int y, int width, int height, int padding, @Nullable Component title, @Nullable Component body) {
		super(font, page, x, y, width, height, padding);
		this.title = title;
		this.body = body;
	}

	//// GETTERS AND SETTERS ////

	public void setTitle(@Nullable Component title) {
		this.title = title;
	}

	public void setBody(@Nullable Component body) {
		this.body = body;
	}

	//// RENDER METHODS ////

	@Override
	public void render(final IBookScreen parent, final GuiGraphics graphics, final float ticksOpen) {
		super.render(parent, graphics, ticksOpen);
		renderTitle(parent, graphics, ticksOpen);
		renderBody(parent, graphics, ticksOpen);
	}

	protected void renderTitle(IBookScreen parent, GuiGraphics graphics, float ticksOpen) {
		// validate title
		if(null == title) {
			return;
		}
		// determine maximum width
		//final int maxWidth = width - (padding * 2);
		final int textWidth = font.width(title);
		// determine position
		int posX = x + Math.max(0, width - textWidth) / 2;
		int posY = y + padding;
		// draw title
		graphics.drawString(font, title, posX, posY, 0, false);
	}

	protected void renderBody(IBookScreen parent, GuiGraphics graphics, float ticksOpen) {
		// validate body
		if(null == body) {
			return;
		}
		// determine maximum width
		final int maxWidth = width - (padding * 2);
		// determine position
		int posX = x + padding + 4;
		int posY = y + padding;
		posY += padding * 2;
		graphics.drawWordWrap(font, body, posX, posY, maxWidth, 0);
	}

	//// BUILDER ////

	public static class Builder extends BookPage.Builder  {
		protected @Nullable Component title;
		protected @Nullable Component body;

		//// CONSTRUCTOR ////

		public Builder(IBookScreen parent, int page) {
			super(parent, page);
		}

		//// CHAIN METHODS ////

		public Builder title(final @Nullable Component title) {
			this.title = title;
			return this;
		}

		public Builder body(final @Nullable Component body) {
			this.body = body;
			return this;
		}

		//// BUILD ////

		@Override
		public TitleAndBodyPage build() {
			return new TitleAndBodyPage(font, page, x, y, width, height, padding, title, body);
		}
	}
}
