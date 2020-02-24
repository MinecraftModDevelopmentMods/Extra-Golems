package com.mcmoddev.golems.gui;

import com.mcmoddev.golems.container.ContainerDispenserGolem;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class GuiDispenserGolem extends ContainerScreen<ContainerDispenserGolem> {

  public static final ResourceLocation BG_TEXTURE = new ResourceLocation("minecraft:textures/gui/container/dispenser.png");
  public static final int START_Y = 20;

  public GuiDispenserGolem(final ContainerDispenserGolem screenContainer, final PlayerInventory inv, final ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
  }

  @Override
  public void render(final int mouseX, final int mouseY, final float partialTicks) {
    renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    renderHoveredToolTip(mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.getMinecraft().getTextureManager().bindTexture(BG_TEXTURE);
    int startX = (this.width - this.xSize) / 2;
    int startY = (this.height - this.ySize) / 2;
    this.blit(startX, startY, 0, 0, this.xSize, this.ySize);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    this.font.drawString(this.title.getFormattedText(), 12.0F, 5.0F, 4210752);
    this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 2), 4210752);
  }

}
