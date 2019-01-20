package com.golems.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.golems.entity.EntityBedrockGolem;
import com.golems.entity.GolemBase;
import com.golems.entity.GolemMultiTextured;
import com.golems.main.ExtraGolems;
import com.golems.util.GolemLookup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;

public class GuiLoader {

    public static void loadBookGui(EntityPlayer playerIn, ItemStack itemstack) {
    	// only load client-side, of course
    	if(!playerIn.getEntityWorld().isRemote)
    		return;
    	// populate the DummyGolems list if it is empty
    	if(ExtraGolems.proxy.DUMMY_GOLEMS.isEmpty()) {
    		ExtraGolems.proxy.DUMMY_GOLEMS.addAll(GolemLookup.getDummyGolemList(playerIn.getEntityWorld()));
    	}
    	// use DummyGolems list to build pages (need to rebuild in real-time for localization)
    	GuiGolemBook.initGolemBookEntries(playerIn.getEntityWorld());
    	final List<String> pages = GuiGolemBook.getPages(GuiGolemBook.GOLEMS);
    	GuiGolemBook.addNBT(itemstack, pages);
    	// open gui
        //Minecraft.getMinecraft().displayGuiScreen(new GuiScreenBook(playerIn, itemstack, false));
    	Minecraft.getMinecraft().displayGuiScreen(new GuiGolemBook(playerIn, itemstack));
    }   
}
