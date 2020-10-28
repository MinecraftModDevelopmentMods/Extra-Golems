package com.mcmoddev.golems.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public final class GuiLoader {

  private GuiLoader() {
    //
  }

  public static void loadBookGui(final PlayerEntity playerIn, final ItemStack itemstack) {
    // only load client-side, of course
    if (!playerIn.world.isRemote()) {
      return;
    }
    // open the gui
    net.minecraft.client.Minecraft.getInstance().displayGuiScreen(new GuiGolemBook(playerIn, itemstack));
  }
}
