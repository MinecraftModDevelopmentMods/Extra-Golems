package com.mcmoddev.golems.client.menu.guide_book.book;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

public interface IBookScreen {

	<T extends AbstractWidget> T addButton(final T button);

	long getTicksOpen();

	Screen getSelf();

	Font getFont();

	int getStartX();

	int getStartY();

	void setPageIndex(int page);

	int getPageIndex();
}
