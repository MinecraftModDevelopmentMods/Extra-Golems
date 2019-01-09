package com.golems.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGolemBook extends GuiScreenBook {

	public GuiGolemBook(EntityPlayer player, ItemStack book, boolean isUnsigned) {
		super(player, book, isUnsigned);
	}

	
}
