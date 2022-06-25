package com.mcmoddev.golems.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class EGScreenLoader {

	private EGScreenLoader() {
		//
	}

	public static void loadBookGui(final Player playerIn, final ItemStack itemstack) {
		// only load client-side, of course
		if (!playerIn.getCommandSenderWorld().isClientSide()) {
			return;
		}
		// open the gui
		Minecraft.getInstance().setScreen(new GolemBookScreen(playerIn, itemstack));
	}
}
