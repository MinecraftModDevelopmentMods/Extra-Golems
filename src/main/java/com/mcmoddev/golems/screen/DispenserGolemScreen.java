package com.mcmoddev.golems.screen;

import com.mcmoddev.golems.menu.PortableDispenserMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DispenserGolemScreen extends AbstractContainerScreen<PortableDispenserMenu> {

	public static final ResourceLocation BG_TEXTURE = new ResourceLocation("minecraft:textures/gui/container/dispenser.png");

	public DispenserGolemScreen(PortableDispenserMenu cont, Inventory pInv, Component title) {
		super(cont, pInv, title);
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
	}

	@Override
	public void render(PoseStack matrix, int x, int y, float f) {
		this.renderBackground(matrix);
		super.render(matrix, x, y, f);
		this.renderTooltip(matrix, x, y);
	}

	@Override
	protected void renderBg(PoseStack matrix, float f, int i1, int i2) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, BG_TEXTURE);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
	}
}
