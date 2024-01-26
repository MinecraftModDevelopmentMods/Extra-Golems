package com.mcmoddev.golems.client.menu.guide_book.page;

import com.mcmoddev.golems.client.menu.guide_book.IBookScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class TitleAndBodyPage extends BookPage {

	protected @Nullable Component title;
	protected @Nullable Component body;

	public TitleAndBodyPage(Font font, int x, int y, int width, int height, int padding, @Nullable Component title, @Nullable Component body) {
		super(font, x, y, width, height, padding);
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
	public void render(final IBookScreen parent, final GuiGraphics graphics, final int pageNumber, final float ticksOpen) {
		super.render(parent, graphics, pageNumber, ticksOpen);
		renderTitle(parent, graphics, pageNumber, ticksOpen);
		renderBody(parent, graphics, pageNumber, ticksOpen);
	}

	protected void renderTitle(IBookScreen parent, GuiGraphics graphics, int pageNumber, float ticksOpen) {
		// validate title
		if(null == title) {
			return;
		}
		// determine maximum width
		final int maxWidth = width - (padding * 2);
		// determine position
		int posX = x + padding + 4;
		int posY = y + padding;
		// draw title
		graphics.drawWordWrap(font, title, posX, posY, maxWidth, 0);
	}

	protected void renderBody(IBookScreen parent, GuiGraphics graphics, int pageNumber, float ticksOpen) {
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

		public Builder(IBookScreen parent) {
			super(parent);
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
			return new TitleAndBodyPage(font, x, y, width, height, padding, title, body);
		}
	}
}
