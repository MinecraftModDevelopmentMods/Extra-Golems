package com.mcmoddev.golems.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class ItemGolemSpell extends Item {

	public ItemGolemSpell() {
		super(new Item.Properties().maxStackSize(64).group(ItemGroup.MISC));
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return super.getDisplayName(stack).applyTextStyle(TextFormatting.RED);
	}
}
