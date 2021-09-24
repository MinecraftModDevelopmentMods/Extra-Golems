package com.mcmoddev.golems.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public final class EGScreenLoader {

  private EGScreenLoader() {
    //
  }

  public static void loadBookGui(final PlayerEntity playerIn, final ItemStack itemstack) {
    // only load client-side, of course
    if (!playerIn.world.isRemote()) {
      return;
    }
    // open the gui
    Minecraft.getInstance().displayGuiScreen(new GolemBookScreen(playerIn, itemstack));
  }
}
