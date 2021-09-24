package com.mcmoddev.golems.screen;

import com.mcmoddev.golems.menu.PortableDispenserMenu;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;


public class DispenserGolemScreen extends ContainerScreen<PortableDispenserMenu> {

  public static final ResourceLocation BG_TEXTURE = new ResourceLocation("minecraft:textures/gui/container/dispenser.png");

  public DispenserGolemScreen(PortableDispenserMenu cont, PlayerInventory pInv, ITextComponent title) {
    super(cont, pInv, title);
  }

  @Override
  protected void init() {
    super.init();
	this.titleX = (this.xSize - this.font.getStringPropertyWidth(this.title)) / 2;
  }

  @Override
  public void render(MatrixStack matrix, int x, int y, float f) {
	this.renderBackground(matrix);
	super.render(matrix, x, y, f);
	this.renderHoveredTooltip(matrix, x, y);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float f, int i1, int i2) {
	RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	this.minecraft.getTextureManager().bindTexture(BG_TEXTURE);
	int i = (this.width - this.xSize) / 2;
	int j = (this.height - this.ySize) / 2;
	this.blit(matrix, i, j, 0, 0, this.xSize, this.ySize);
  }
}
