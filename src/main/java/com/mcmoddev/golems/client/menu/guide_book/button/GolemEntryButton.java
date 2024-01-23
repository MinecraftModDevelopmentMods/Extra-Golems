package com.mcmoddev.golems.client.menu.guide_book.button;

import com.mcmoddev.golems.client.menu.guide_book.GolemBookEntry;
import com.mcmoddev.golems.client.menu.guide_book.module.DrawBlockModule;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.function.Supplier;

public class GolemEntryButton extends ImageButton {

	protected final Screen parent;
	protected final DrawBlockModule drawBlockModule;
	protected final Font font;
	protected final int margin;
	protected final Supplier<Long> ticksOpenSupplier;
	
	protected GolemBookEntry entry;
	protected int page;
	
	public GolemEntryButton(final Screen parent, final Font font, final DrawBlockModule drawBlockModule,
							final int x, final int y, final int width, final int height, int margin,
							final ResourceLocation texture, final int u, final int v, final int dv,
							final Supplier<Long> ticksOpenSupplier, final OnPress onPress) {
		super(x, y, width, height, u, v, dv, texture, onPress);
		this.parent = parent;
		this.font = font;
		this.drawBlockModule = drawBlockModule;
		this.ticksOpenSupplier = ticksOpenSupplier;
		this.margin = margin;
		this.page = 0;
	}

	public void setEntry(final GolemBookEntry entry, final int page) {
		this.entry = entry;
		this.page = page;
	}

	public int getPage() {
		return page;
	}

	@Override
	public void renderWidget(final GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.renderWidget(graphics, mouseX, mouseY, partialTicks);
		// draw the block and name of the entity
		int index = (int) (ticksOpenSupplier.get() / 30);
		drawBlockModule
				.withBlock(this.entry.getBlock(index))
				.withScale(1.0F)
				.withPos(this.getX() - margin - 2, this.getY() - 9)
				.render(parent, graphics, partialTicks);

		// prepare to draw the entity's name
		graphics.pose().pushPose();

		final MutableComponent name = entry.getGolemName();
		final int wrap = this.width - 20;
		float scale = 1.0F;
		int nameH = font.wordWrapHeight(name.getString(), wrap);
		if (nameH > this.height) {
			scale = 0.78F;
			nameH = (int) (scale * font.wordWrapHeight(name.getString(), (int) (wrap / scale)));
		}
		int nameX = this.getX() + 20;
		int nameY = this.getY() + ((this.height - nameH) / 2) + 1;
		// re-scale and draw the entity name
		graphics.pose().scale(scale, scale, scale);
		for (final FormattedCharSequence word : font.split(name, (int) (wrap / scale))) {
			graphics.drawString(font, word, nameX / scale, nameY / scale, 0, false);
			nameY += font.lineHeight;
		}
		graphics.pose().popPose();
	}
}
