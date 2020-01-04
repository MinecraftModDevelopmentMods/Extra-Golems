package com.mcmoddev.golems.items;

import com.mcmoddev.golems.gui.GuiLoader;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemInfoBook extends Item {

	public ItemInfoBook() {
		super(new Item.Properties().maxStackSize(1).group(ItemGroup.MISC));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		if (playerIn.getEntityWorld().isRemote) {
			GuiLoader.loadBookGui(playerIn, itemstack);
		}
		return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
	}
}
