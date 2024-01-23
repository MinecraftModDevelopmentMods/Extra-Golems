package com.mcmoddev.golems.client.menu.guide_book.module;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public abstract class DrawModule {

	protected int x;
	protected int y;

	public DrawModule withPos(int x, int y)  {
		this.x = x;
		this.y = y;
		return this;
	}

	public abstract void render(final Screen parent, final GuiGraphics graphics, final float partialTicks);
}
