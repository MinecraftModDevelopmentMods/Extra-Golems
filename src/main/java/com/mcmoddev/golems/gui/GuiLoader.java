package com.mcmoddev.golems.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class GuiLoader {

  private GuiLoader() {
    //
  }

  public static void loadBookGui(final Player playerIn, final ItemStack itemstack) {
    // only load client-side, of course
    if (!playerIn.getCommandSenderWorld().isClientSide()) {
      return;
    }
    // open the gui
    Minecraft.getInstance().setScreen(new GuiGolemBook(playerIn, itemstack));
  }
}
