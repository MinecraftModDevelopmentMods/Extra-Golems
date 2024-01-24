package com.mcmoddev.golems.client.menu.guide_book.module;

import com.mcmoddev.golems.client.menu.guide_book.GuideBookGroup;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;

public class DrawGroupPageModule extends DrawPageModule {

	protected final int imageWidth;
	protected final int imageHeight;

	protected GuideBookGroup group;

	public DrawGroupPageModule(Font font, int width, int height, int margin, int imageWidth, int imageHeight) {
		super(font, width, height, margin);
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	public DrawGroupPageModule withEntry(final GuideBookGroup group) {
		this.group = group;
		return this;
	}

	@Override
	public void render(Screen parent, GuiGraphics graphics, float partialTicks) {
		drawEntry(parent, graphics);
		drawPageNum(graphics);
	}

	protected void drawEntry(final Screen parent, final GuiGraphics graphics) {
		// determine the golem book group entry to draw
		// TODO make the entry alternate between values in the group
		GuideBookGroup.Entry entry = this.group.getEntry(0);
		if(this.group.getTitle() != null) {
			this.withTitle(this.group.getTitle()).withSubtitle(entry.getTitle());
		} else {
			this.withTitle(entry.getTitle()).withSubtitle(null);
		}

		// determine coordinates
		int posX = x + margin * 4;
		int posY = y + margin;

		// draw title text
		graphics.drawString(font, title, posX, posY, 0, false);
		// draw subtitle text
		posY += font.lineHeight;
		if(subtitle != null) {
			graphics.drawString(font, subtitle, posX, posY, 0, false);
		}

		// draw description text
		posX = x + margin;
		posY += margin * 2;
		graphics.drawWordWrap(font, entry.getDescription(), posX, posY, (width / 2) - (margin * 2), 0);

		// draw image
		if(entry.getImage() != null) {
			float scale = 0.9F;
			posX = x + (width / 4) - (int) ((imageWidth * scale) / 2.0F);
			posY = x + width - (int) (imageHeight * scale) - (margin * 2);
			int w = (int) (imageWidth * scale);
			int h = (int) (imageHeight * scale);
			graphics.blit(entry.getImage(), posX, posY, 0, 0, w, h, w, h);
		}
	}

}
