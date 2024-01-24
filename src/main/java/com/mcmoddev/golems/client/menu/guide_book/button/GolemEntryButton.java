package com.mcmoddev.golems.client.menu.guide_book.button;

import com.mcmoddev.golems.client.menu.guide_book.GuideBookGroup;
import com.mcmoddev.golems.client.menu.guide_book.module.DrawBlockModule;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringUtil;

import java.util.function.Supplier;

public class GolemEntryButton extends ImageButton {

	protected final Screen parent;
	protected final DrawBlockModule drawBlockModule;
	protected final Font font;
	protected final int margin;
	protected final Supplier<Long> ticksOpenSupplier;
	
	protected GuideBookGroup group;
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

	public void setGroup(final GuideBookGroup entry, final int page) {
		this.group = entry;
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
		final GuideBookGroup.Entry entry = this.group.getEntry(index);
		drawBlockModule
				.withBlock(this.group.getEntry(index).getBlock(index))
				.withScale(1.0F)
				.withPos(this.getX() - margin - 2, this.getY() - 9)
				.render(parent, graphics, partialTicks);

		// prepare to draw the entity's name
		final int textMargin = 20;
		final int titleWidth = this.width - textMargin;
		// create a truncated title component
		Component title = this.group.getTitle() != null ? this.group.getTitle() : this.group.getEntry(0).getTitle();
		final String sTitle = StringUtil.truncateStringIfNecessary(ChatFormatting.stripFormatting(title.getString()), (int) (titleWidth / 5.5D), true);
		title = Component.literal(sTitle).withStyle(title.getStyle());
		// draw the title
		graphics.drawString(font, title, textMargin, this.getY() + (this.height - font.lineHeight) / 2, 0, false);
	}
}
