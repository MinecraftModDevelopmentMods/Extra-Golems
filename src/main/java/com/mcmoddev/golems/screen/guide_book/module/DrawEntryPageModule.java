package com.mcmoddev.golems.screen.guide_book.module;

import com.mcmoddev.golems.screen.guide_book.GolemBookEntry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
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
	public void render(Screen parent, PoseStack poseStack, float partialTicks) {
		drawEntry(parent, poseStack);
		drawPageNum(poseStack);
	}

	protected void drawEntry(final Screen parent, final PoseStack poseStack) {
		// 'entity name' text box
		int nameX = x + margin * 4;
		int nameY = y + margin;
		this.font.drawWordWrap(title, nameX, nameY, (width / 2) - margin * 5, 0);

		// 'entity stats' text box
		int statsX = x + margin;
		int statsY = nameY + margin * 2;
		MutableComponent stats = entry.getDescriptionPage();
		this.font.drawWordWrap(stats, statsX, statsY, (width / 2) - (margin * 2), 0);

		// 'screenshot' (supplemental image)
		if (entry.hasImage()) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			float scale = 0.9F;
			int imgX = x + (width / 4) - (int) ((imageWidth * scale) / 2.0F);
			int imgY = x + width - (int) (imageHeight * scale) - (margin * 2);
			RenderSystem.setShaderTexture(0, entry.getImageResource());
			int w = (int) (imageWidth * scale);
			int h = (int) (imageHeight * scale);
			Screen.blit(poseStack, imgX, imgY, 0, 0, w, h, w, h);
		}
	}

}
