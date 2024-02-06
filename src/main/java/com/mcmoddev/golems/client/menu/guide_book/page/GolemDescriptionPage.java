package com.mcmoddev.golems.client.menu.guide_book.page;

import com.mcmoddev.golems.client.menu.guide_book.GuideBookEntry;
import com.mcmoddev.golems.client.menu.guide_book.GuideBookGroup;
import com.mcmoddev.golems.client.menu.guide_book.IBookScreen;
import com.mcmoddev.golems.client.menu.button.CyclingItemButton;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class GolemDescriptionPage extends TitleAndBodyPage {

	/*
	 * Size of the supplemental image for each entry, if one is present.
	 * Any image with a 2:1 ratio will render with no issues.
	 */
	public static final int IMAGE_WIDTH = 128;
	public static final int IMAGE_HEIGHT = 64;

	protected final GuideBookGroup group;
	protected int entryIndex;

	protected final CyclingItemButton item;

	public GolemDescriptionPage(GuideBookGroup group, Font font, int page, int x, int y, int width, int height, int padding, CyclingItemButton itemButton) {
		super(font, page, x, y, width, height, padding, group.getEntry(0).getTitle(), group.getEntry(0).getDescription());
		this.group = group;
		this.item = itemButton;
		this.setEntryIndex(0);
	}

	//// GETTERS AND SETTERS ////

	public GuideBookGroup getGroup() {
		return group;
	}

	public int getEntryIndex() {
		return entryIndex;
	}

	public void setEntryIndex(int entryIndex) {
		this.entryIndex = Mth.clamp(entryIndex, 0, this.group.getList().size() - 1);
		final GuideBookEntry entry = this.group.getEntry(this.entryIndex);
		this.setTitle(entry.getTitle());
		this.setBody(entry.getDescription());
		this.item.setItems(entry.getItems());
	}

	//// BOOK PAGE ////

	@Override
	public void onShow(IBookScreen parent) {
		this.item.visible = this.item.active = true;
	}

	@Override
	public void onHide(IBookScreen parent) {
		this.item.visible = this.item.active = false;
	}

	@Override
	public void render(IBookScreen parent, GuiGraphics graphics, float ticksOpen) {
		// update item button
		this.item.setIndex((int) (ticksOpen / 30));
		// render image
		renderImage(parent, graphics, ticksOpen);
		// render title and body
		super.render(parent, graphics, ticksOpen);
	}

	@Override
	protected void renderTitle(IBookScreen parent, GuiGraphics graphics, float ticksOpen) {
		// validate title
		if(null == title) {
			return;
		}
		// determine maximum width
		final int itemWidth = (int) (16 * item.getScale());
		final int maxWidth = width - itemWidth - (padding * 2);
		// determine position
		int posX = x + padding + itemWidth + 2;
		int posY = y + padding + (padding * 2 - font.wordWrapHeight(title, maxWidth)) / 2;
		// draw title
		graphics.drawWordWrap(font, title, posX, posY, maxWidth, 0);
	}

	@Override
	protected void renderBody(IBookScreen parent, GuiGraphics graphics, float ticksOpen) {
		// validate body
		if(null == body) {
			return;
		}
		// determine maximum width
		final int maxWidth = width - (padding * 2);
		// determine position
		int posX = x + padding + 4;
		int posY = y + (int)(padding * 3.5F);
		// draw body
		graphics.drawWordWrap(font, body, posX, posY, maxWidth, 0);
	}

	protected void renderImage(IBookScreen parent, GuiGraphics graphics, float ticksOpen) {
		final GuideBookEntry entry = group.getEntry(entryIndex);
		// validate image
		if(null == entry.getImage()) {
			return;
		}
		// draw image
		final int posX = this.x + (this.width - IMAGE_WIDTH) / 2;
		final int posY = this.y + this.height - IMAGE_HEIGHT - (int) (padding * 1.5F);
		graphics.blit(entry.getImage(), posX, posY, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);
	}

	//// BUILDER ////

	public static class Builder extends TitleAndBodyPage.Builder {

		private final GuideBookGroup group;
		private float itemScale;

		public Builder(IBookScreen parent, int page, GuideBookGroup group) {
			super(parent, page);
			this.group = group;
			this.itemScale = 1.6F;
		}

		public Builder scale(final float scale) {
			this.itemScale = scale;
			return this;
		}

		@Override
		public GolemDescriptionPage build() {
			final CyclingItemButton button = parent.addButton(new CyclingItemButton(Button
					.builder(Component.empty(), b -> {})
					.pos(x + padding, y + padding), group.getItems(), this.itemScale));
			return new GolemDescriptionPage(group, font, page, x, y, width, height, padding, button);
		}
	}
}
