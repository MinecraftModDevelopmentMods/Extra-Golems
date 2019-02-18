package com.mcmoddev.golems.gui;

import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemLookup;
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
    	// populate the DummyGolems list if it is empty
	    if (ExtraGolems.PROXY.DUMMY_GOLEMS.isEmpty()) {
		    ExtraGolems.PROXY.DUMMY_GOLEMS.addAll(GolemLookup.getDummyGolemList(playerIn.getEntityWorld()));
    	}
    	// open the gui
    	Minecraft.getMinecraft().displayGuiScreen(new GuiGolemBook(playerIn, itemstack));
    }   
}
