package com.mcmoddev.golems.items;

import com.mcmoddev.golems.gui.GuiLoader;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemInfoBook extends Item {

	public ItemInfoBook() {
		super(new Item.Properties().maxStackSize(1).group(ItemGroup.MISC));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		if (playerIn.getEntityWorld().isRemote) {
			GuiLoader.loadBookGui(playerIn, itemstack);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}
}
