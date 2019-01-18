package com.golems.gui;

import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GuiGolemBook extends GuiScreenBook {

	public GuiGolemBook(EntityPlayer player, ItemStack book, boolean isUnsigned) {
		super(player, book, isUnsigned);
	}

	
}
