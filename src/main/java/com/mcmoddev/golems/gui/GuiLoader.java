package com.mcmoddev.golems.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class GuiLoader {

	private GuiLoader() {
		//
	}

    public static void loadBookGui(EntityPlayer playerIn, ItemStack itemstack) {
    	// only load client-side, of course
    	if(!playerIn.getEntityWorld().isRemote)
    		return;
    	// open the gui
    	Minecraft.getInstance().displayGuiScreen(new GuiGolemBook(playerIn, itemstack));
    }
}
