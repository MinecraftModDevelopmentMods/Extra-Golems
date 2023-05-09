package com.mcmoddev.golems.item;

import com.mcmoddev.golems.EGClientEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GuideBookItem extends Item {

	public GuideBookItem(final Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		if (playerIn.getCommandSenderWorld().isClientSide()) {
			EGClientEvents.loadBookGui(playerIn, itemstack);
		}
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
	}
}
