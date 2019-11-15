package com.golems.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ItemGolemSpell extends Item {
	
	public ItemGolemSpell() {
		super();
		this.setCreativeTab(CreativeTabs.MISC);
	}
	
	@Override
	public String getItemStackDisplayName(final ItemStack stack) {
		return TextFormatting.RED + super.getItemStackDisplayName(stack) + TextFormatting.RESET;
	}
}
