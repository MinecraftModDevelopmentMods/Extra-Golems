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

public class TableOfContentsPage extends TitleAndBodyPage implements ScrollButton.IScrollListener {

	protected final ResourceLocation texture;
	protected final int tableU;
	protected final int tableV;
	private final List<ITableOfContentsEntry> entries;

	protected final List<TableOfContentsButton> buttons;
	protected ScrollButton scrollButton;
	protected int scrollOffset;

	public TableOfContentsPage(Font font, int x, int y, int width, int height, int padding, Component title,
							   ResourceLocation texture, int tableU, int tableV, final List<ITableOfContentsEntry> entries) {
		super(font, x, y, width, height, padding, title, null);
		this.texture = texture;
		this.tableU = tableU;
		this.tableV = tableV;
		this.entries = entries;
		this.buttons = new ArrayList<>();
	}

	protected void setButtons(final ScrollButton scrollButton, List<TableOfContentsButton> buttons) {
		this.scrollButton = scrollButton;
		this.buttons.clear();
		this.buttons.addAll(buttons);
	}

	public ITableOfContentsEntry getEntry(final int index) {
		return this.entries.get(index);
	}

	//// PAGE ////

	@Override
	public void onShow(IBookScreen parent) {
		this.scrollButton.visible = true;
		this.scrollButton.active = this.entries.size() > this.buttons.size();
	}

	@Override
	public void onHide(IBookScreen parent) {
		this.scrollButton.visible = this.scrollButton.active = false;
	}

	@Override
	public void render(IBookScreen parent, GuiGraphics graphics, int pageNumber, float ticksOpen) {
		graphics.blit(texture, x + padding + 1, y + padding * 2 - 1, tableU, tableV, width, height);
		super.render(parent, graphics, pageNumber, ticksOpen);
	}

	//// SCROLL LISTENER ////

	public void onScroll(final ScrollButton scrollButton, final float percent) {
		// calculate scroll offset
		this.scrollOffset = Mth.floor(percent * (entries.size() - buttons.size()));
		// iterate table of contents buttons
		for(int i = 0, n = buttons.size(), index = 0; i < n; i++) {
			TableOfContentsButton button = buttons.get(i);
			index = i + scrollOffset;
			// hide button when reaching end of entries
			if(index >= buttons.size()) {
				button.active = button.visible = false;
				continue;
			}
			// update button with current entry
			button.setEntry(entries.get(index), index);
			button.active = button.visible = true;
		}
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
		protected int scrollU;
		protected int scrollV;
		protected Consumer<Integer> onPress;

		public Builder(IBookScreen parent, List<? extends ITableOfContentsEntry> entries, Consumer<Integer> onPress) {
			super(parent);
			this.entries = ImmutableList.copyOf(entries);
			this.buttonCount = 5;
			this.onPress = onPress;
			this.padding = 2;
			this.texture = GuideBookScreen.CONTENTS;
			this.tableU = 0;
			this.tableV = 0;
			this.buttonU = 111;
			this.buttonV = 0;
			this.scrollU = 0;
			this.scrollV = 115;
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

		public Builder buttonCount(final int count) {
			this.buttonCount = count;
			return this;
		}

		@Override
		public TableOfContentsPage build() {
			// build page
			final TableOfContentsPage page = new TableOfContentsPage(font, x, y, width, height * buttonCount, padding, title, texture, tableU, tableV, entries);
			// add scroll button
			final ScrollButton scrollButton = new ScrollButton(Button.builder(Component.empty(), b -> {})
					.pos(x + width + padding, y)
					.size(12, height * buttonCount),
					texture, scrollU, scrollV, 12, 15, 15, true,
					1.0F / entries.size(), page);
			// add table of contents buttons
			final List<TableOfContentsButton> buttons = new ArrayList<>();
			for(int i = 0; i < buttonCount; i++) {
				Button.OnPress onPress = b -> this.onPress.accept(((TableOfContentsButton)b).getIndex());
				buttons.add(parent.addButton(new TableOfContentsButton(parent, font, x + padding, y + padding + i * height, width - padding * 2, height - padding * 2, padding, texture, buttonU, buttonV, height, onPress)));
			}
			// assign buttons
			page.setButtons(scrollButton, buttons);
			return page;
		}
	}
}
