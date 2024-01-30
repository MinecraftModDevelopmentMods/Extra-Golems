package com.mcmoddev.golems.client.menu.guide_book.page;

import com.mcmoddev.golems.client.menu.guide_book.IBookScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class BookPage {

	protected final Font font;
	protected final int page;
	protected final int x;
	protected final int y;
	protected final int width;
	protected final int height;
	protected final int padding;

	protected final Component pageText;

	/**
	 * @param font the font
	 * @param page the page number
	 * @param x the x position
	 * @param y the y position
	 * @param width the page width
	 * @param height the page height
	 * @param padding the page margin
	 */
	public BookPage(Font font, int page, int x, int y, int width, int height, int padding) {
		this.font = font;
		this.page = page;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.padding = padding;
		this.pageText = Component.literal("" + page);
	}

	//// GETTERS ////

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getPadding() {
		return padding;
	}

	public int getPage() {
		return page;
	}

	//// RENDER METHODS ////

	public void onShow(final IBookScreen parent) {}

	public void onHide(final IBookScreen parent) {}

	/**
	 * Renders the content(s) of this page
	 * @param parent the screen
	 * @param graphics the gui graphics
	 * @param ticksOpen the number of ticks since the screen was opened, including the partial tick
	 */
	public void render(final IBookScreen parent, final GuiGraphics graphics, final float ticksOpen) {
		drawPageNumber(graphics, this.page + 1);
	}

	protected void drawPageNumber(GuiGraphics graphics, int number) {
		final int posX;
		if((number % 2) == 1) {
			// right-hand page has text near left side
			posX = this.x + padding * 2;
		} else {
			// left-hand page has text near right side
			posX = this.x + this.width - padding * 2;
		}
		final int posY = this.y + this.height + 18;
		graphics.drawString(font, pageText, posX, posY, 0, false);
	}

	//// BUILDER ////

	public static class Builder {
		protected final IBookScreen parent;
		protected final int page;
		protected Font font;
		protected int x;
		protected int y;
		protected int width;
		protected int height;
		protected int padding;

		//// CONSTRUCTOR ////

		public Builder(IBookScreen parent, int page) {
			this.parent = parent;
			this.page = page;
			this.font = parent.getFont();
			this.width = 128;
			this.height = 256;
			this.padding = 11;
			this.x = parent.getStartX();
			this.y = parent.getStartY();
		}

		//// CHAIN METHODS ////

		public Builder pos(final int x, final int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public Builder dimensions(final int width, final int height) {
			this.width = width;
			this.height = height;
			return this;
		}

		public Builder padding(final int padding) {
			this.padding = padding;
			return this;
		}

		//// BUILD ////

		public BookPage build() {
			return new BookPage(font, page, x, y, width, height, padding);
		}
	}
}
