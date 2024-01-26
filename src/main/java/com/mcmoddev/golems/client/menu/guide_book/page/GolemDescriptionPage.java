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

	protected final GuideBookGroup group;
	protected int entryIndex;

	protected final CyclingItemButton item;

	public GolemDescriptionPage(GuideBookGroup group, Font font, int x, int y, int width, int height, int padding, CyclingItemButton itemButton) {
		super(font, x, y, width, height, padding, group.getEntry(0).getTitle(), group.getEntry(0).getDescription());
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
	public void render(IBookScreen parent, GuiGraphics graphics, int pageNumber, float ticksOpen) {
		super.render(parent, graphics, pageNumber, ticksOpen);
		this.item.setIndex((int) (ticksOpen / 30));
		renderDescription(this.group.getEntry(this.entryIndex), parent, graphics, pageNumber, ticksOpen);
	}

	protected void renderDescription(GuideBookEntry entry, IBookScreen parent, GuiGraphics graphics, int pageNumber, float ticksOpen) {
		// determine maximum width
		final int maxWidth = width - (padding * 2);
		// prepare to draw title
		int posX = x + padding + 4;
		int posY = y + padding;
		// draw title
		graphics.drawWordWrap(font, entry.getTitle(), posX, posY, maxWidth, 0);
		// draw body
		posY += padding * 2;
		graphics.drawWordWrap(font, entry.getDescription(), posX, posY, maxWidth, 0);
	}

	//// BUILDER ////

	public static class Builder extends TitleAndBodyPage.Builder {

		private final GuideBookGroup group;
		private float itemScale;

		public Builder(IBookScreen parent, GuideBookGroup group) {
			super(parent);
			this.group = group;
			this.itemScale = 1.6F;
		}

		public Builder scale(final float scale) {
			this.itemScale = scale;
			return this;
		}

		@Override
		public GolemDescriptionPage build() {
			final CyclingItemButton button = parent.addButton(new CyclingItemButton(Button.builder(Component.empty(), b -> {}), group.getItems(), this.itemScale));
			return new GolemDescriptionPage(group, font, x, y, width, height, padding, button);
		}
	}
}
