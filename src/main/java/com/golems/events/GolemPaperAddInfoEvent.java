package com.golems.events;

import java.util.List;

import com.golems.items.ItemGolemPaper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** 
 * Fired after {@link ItemGolemPaper} has added information to itself.
 * Only activates when player is sneaking.
 **/
@SideOnly(Side.CLIENT)
public class GolemPaperAddInfoEvent extends Event
{
	public final ItemStack itemStack;
	public final EntityPlayer player;
	public final List<String> infoList;
	public final boolean isAdvanced;
	
	public GolemPaperAddInfoEvent(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean advanced)
	{
		this.itemStack = par1ItemStack;
		this.player = par2EntityPlayer;
		this.infoList = par3List;
		this.isAdvanced = advanced;
	}
	
	/** Removes all entries matching the passed String **/
	public void replaceWith(String toRemove, String replace)
	{
		for(String line : this.infoList)
		{
			line.replaceAll(toRemove, replace);
		}
	}
}
