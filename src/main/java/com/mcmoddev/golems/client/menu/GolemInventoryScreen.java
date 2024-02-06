package com.mcmoddev.golems.client.menu;

import com.mcmoddev.golems.menu.GolemInventoryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GolemInventoryScreen extends AbstractContainerScreen<GolemInventoryMenu> {

	public static final ResourceLocation BG_TEXTURE = new ResourceLocation("minecraft:textures/gui/container/dispenser.png");

	public GolemInventoryScreen(GolemInventoryMenu cont, Inventory pInv, Component title) {
		super(cont, pInv, title);
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
	}

	@Override
	public void render(GuiGraphics matrix, int x, int y, float f) {
		this.renderBackground(matrix);
		super.render(matrix, x, y, f);
		this.renderTooltip(matrix, x, y);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float f, int i1, int i2) {
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		graphics.blit(BG_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
	}
}
