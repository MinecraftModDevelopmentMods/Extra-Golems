package com.golems.gui;

import com.golems.container.ContainerDispenserGolem;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiDispenserGolem extends GuiContainer {

	private static final ResourceLocation BG_TEXTURE = new ResourceLocation("minecraft:textures/gui/container/dispenser.png");
	/** The player inventory bound to this GUI. */
	private InventoryPlayer playerInventory;
	/** The inventory contained within the corresponding Dispenser Golem. */
	public IInventory dispenserInventory;

	public GuiDispenserGolem(InventoryPlayer playerInv, IInventory dispenserInv) {
		super(new ContainerDispenserGolem(playerInv, dispenserInv));
		this.playerInventory = playerInv;
		this.dispenserInventory = dispenserInv;
	}
	
	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the
	 * items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
		String s = this.dispenserInventory.getDisplayName().getUnformattedText();
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8,
				this.ySize - 96 + 2, 4210752);
	}

	/**
	 * Draws the background layer of this container (behind the items).
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(BG_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}
}
