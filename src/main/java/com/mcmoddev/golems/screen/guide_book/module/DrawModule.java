package com.mcmoddev.golems.screen.guide_book.module;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;

public abstract class DrawModule {

	protected int x;
	protected int y;

	public DrawModule withPos(int x, int y)  {
		this.x = x;
		this.y = y;
		return this;
	}

	public abstract void render(final Screen parent, final PoseStack poseStack, final float partialTicks);
}
