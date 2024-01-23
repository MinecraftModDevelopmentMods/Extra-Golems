package com.mcmoddev.golems.client.menu.guide_book.module;

import com.mcmoddev.golems.client.menu.guide_book.GolemBookEntry;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;

public class DrawEntryPageModule extends DrawPageModule {

	protected final int imageWidth;
	protected final int imageHeight;

	protected GolemBookEntry entry;

	public DrawEntryPageModule(Font font, int width, int height, int margin, int imageWidth, int imageHeight) {
		super(font, width, height, margin);
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	public DrawEntryPageModule withEntry(final GolemBookEntry entry) {
		this.entry = entry;
		this.withTitle(entry.getGolemName());
		return this;
	}

	@Override
	public void render(Screen parent, GuiGraphics graphics, float partialTicks) {
		drawEntry(parent, graphics);
		drawPageNum(graphics);
	}

	protected void drawEntry(final Screen parent, final GuiGraphics graphics) {
		// 'entity name' text box
		int nameX = x + margin * 4;
		int nameY = y + margin;
		graphics.drawWordWrap(font, title, nameX, nameY, (width / 2) - margin * 5, 0);

		// 'entity stats' text box
		int statsX = x + margin;
		int statsY = nameY + margin * 2;
		MutableComponent stats = entry.getDescriptionPage();
		graphics.drawWordWrap(font, stats, statsX, statsY, (width / 2) - (margin * 2), 0);

		// 'screenshot' (supplemental image)
		if (entry.hasImage()) {
			float scale = 0.9F;
			int imgX = x + (width / 4) - (int) ((imageWidth * scale) / 2.0F);
			int imgY = x + width - (int) (imageHeight * scale) - (margin * 2);
			int w = (int) (imageWidth * scale);
			int h = (int) (imageHeight * scale);
			graphics.blit(entry.getImageResource(), imgX, imgY, 0, 0, w, h, w, h);
		}
	}

}
