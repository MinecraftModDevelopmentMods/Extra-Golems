package com.mcmoddev.golems.client.menu.guide_book.page;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.client.menu.guide_book.GuideBookScreen;
import com.mcmoddev.golems.client.menu.guide_book.IBookScreen;
import com.mcmoddev.golems.client.menu.guide_book.ITableOfContentsEntry;
import com.mcmoddev.golems.client.menu.button.ScrollButton;
import com.mcmoddev.golems.client.menu.button.TableOfContentsButton;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TableOfContentsPage extends TitleAndBodyPage implements ScrollButton.IScrollListener, ScrollButton.IScrollProvider {

	protected final ResourceLocation texture;
	protected final int tableU;
	protected final int tableV;
	private final List<ITableOfContentsEntry> entries;

	protected final List<TableOfContentsButton> buttons;
	protected ScrollButton scrollButton;
	protected int scrollOffset;
	protected int tableWidth;
	protected int tableHeight;

	public TableOfContentsPage(Font font, int page, int x, int y, int width, int height, int padding, Component title,
							   ResourceLocation texture, int tableU, int tableV, final List<ITableOfContentsEntry> entries) {
		super(font, page, x, y, width, height, padding, title, null);
		this.texture = texture;
		this.tableU = tableU;
		this.tableV = tableV;
		this.entries = entries;
		this.buttons = new ArrayList<>();
		this.tableWidth = width;
		this.tableHeight = height;
	}

	protected void setButtons(final ScrollButton scrollButton, List<TableOfContentsButton> buttons) {
		this.scrollButton = scrollButton;
		this.buttons.clear();
		this.buttons.addAll(buttons);
		if(!buttons.isEmpty()) {
			TableOfContentsButton b = buttons.get(0);
			this.tableWidth = b.getWidth() + scrollButton.getWidth() + 6;
			this.tableHeight = b.getHeight() * buttons.size() + 2;
		}
	}

	public ITableOfContentsEntry getEntry(final int index) {
		return this.entries.get(index);
	}

	//// PAGE ////

	@Override
	public void onShow(IBookScreen parent) {
		// update scroll button
		this.scrollButton.visible = true;
		this.scrollButton.active = this.entries.size() > this.buttons.size();
		updateButtons();
	}

	@Override
	public void onHide(IBookScreen parent) {
		this.scrollButton.visible = this.scrollButton.active = false;
		for(Button b : this.buttons) {
			b.visible = b.active = false;
		}
	}

	@Override
	public void render(IBookScreen parent, GuiGraphics graphics, float ticksOpen) {
		graphics.blit(texture, x + padding, y + padding * 2, tableU, tableV, tableWidth, tableHeight);
		super.render(parent, graphics, ticksOpen);
	}

	//// SCROLL LISTENER ////

	protected void updateButtons() {
		// iterate table of contents buttons
		for(int i = 0, n = buttons.size(), index = 0; i < n; i++) {
			TableOfContentsButton button = buttons.get(i);
			index = i + scrollOffset;
			// hide button when reaching end of entries
			if(index >= entries.size()) {
				button.active = button.visible = false;
				continue;
			}
			// update button with current entry
			button.setEntry(entries.get(index), index);
			button.active = button.visible = true;
		}
	}

	@Override
	public void onScroll(final ScrollButton scrollButton, final float percent) {
		// calculate scroll offset
		this.scrollOffset = Mth.floor(percent * (entries.size() - buttons.size()));
		updateButtons();
	}

	@Override
	public ScrollButton getScrollButton() {
		return this.scrollButton;
	}

	//// BUILDER ////

	public static class Builder extends TitleAndBodyPage.Builder {

		private final List<ITableOfContentsEntry> entries;
		protected ResourceLocation texture;
		protected int tableU;
		protected int tableV;
		protected int buttonCount;
		protected int buttonU;
		protected int buttonV;
		private int buttonHeight;
		private int buttonWidth;
		protected int scrollU;
		protected int scrollV;
		protected Consumer<Integer> onPress;

		public Builder(IBookScreen parent, int page, List<? extends ITableOfContentsEntry> entries, Consumer<Integer> onPress) {
			super(parent, page);
			this.entries = ImmutableList.copyOf(entries);
			this.buttonCount = 5;
			this.onPress = onPress;
			this.texture = GuideBookScreen.CONTENTS;
			this.tableU = 0;
			this.tableV = 0;
			this.buttonU = 108;
			this.buttonV = 0;
			this.buttonWidth = 88;
			this.buttonHeight = 22;
			this.scrollU = 198;
			this.scrollV = 0;
		}

		public Builder texture(final ResourceLocation texture) {
			this.texture = texture;
			return this;
		}

		public Builder tableUV(final int u, final int v) {
			this.tableU = u;
			this.tableV = v;
			return this;
		}

		public Builder scrollUV(final int u, final int v) {
			this.scrollU = u;
			this.scrollV = v;
			return this;
		}

		public Builder buttonUV(final int u, final int v) {
			this.buttonU = u;
			this.buttonV = v;
			return this;
		}

		public Builder buttonDimensions(final int width, final int height) {
			this.buttonWidth = width;
			this.buttonHeight = height;
			return this;
		}

		public Builder buttonCount(final int count) {
			this.buttonCount = count;
			return this;
		}

		@Override
		public TableOfContentsPage build() {
			// build page
			final TableOfContentsPage tableOfContentsPage = new TableOfContentsPage(font, page, x, y, width, height, padding, title, texture, tableU, tableV, entries);
			// add scroll button
			final ScrollButton scrollButton = parent.addButton(new ScrollButton(Button.builder(Component.empty(), b -> {})
					.pos(x + padding + buttonWidth + 5, y + padding * 2 + 1)
					.size(12, buttonHeight * buttonCount),
					texture, scrollU, scrollV, 12, 15, 15, true,
					1.0F / Math.max(1, entries.size()), tableOfContentsPage));
			// add table of contents buttons
			final List<TableOfContentsButton> buttons = new ArrayList<>();
			for(int i = 0; i < buttonCount; i++) {
				Button.OnPress onPress = b -> this.onPress.accept(((TableOfContentsButton)b).getIndex());
				TableOfContentsButton button = new TableOfContentsButton(parent, font, x + padding + 1, y + padding * 2 + 1 + i * buttonHeight, buttonWidth, buttonHeight, padding, texture, buttonU, buttonV, buttonHeight, onPress);
				buttons.add(parent.addButton(button));
			}
			// assign buttons
			tableOfContentsPage.setButtons(scrollButton, buttons);
			return tableOfContentsPage;
		}
	}
}
