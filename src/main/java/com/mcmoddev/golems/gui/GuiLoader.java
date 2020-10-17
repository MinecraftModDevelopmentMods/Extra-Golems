package com.mcmoddev.golems.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public final class GuiLoader {

  private GuiLoader() {
    //
  }

  public static void loadBookGui(final PlayerEntity playerIn, final ItemStack itemstack) {
    // only load client-side, of course
    if (playerIn.isServerWorld()) {
      return;
    }
    // open the gui
    Minecraft.getInstance().displayGuiScreen(new GuiGolemBook(playerIn, itemstack));
  }
}
