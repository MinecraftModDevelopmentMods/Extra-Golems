package com.mcmoddev.golems.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.block.Blocks;

public class PortableCraftingMenu extends CraftingMenu {

  public PortableCraftingMenu(final int i, final Inventory inv) {
    this(i, inv, null);
  }

  public PortableCraftingMenu(final int i, final Inventory inv, final ContainerLevelAccess call) {
    super(i, inv, call);
  }

  @Override
  public boolean stillValid(final Player playerIn) {
    return true;
  }

  public static class Provider implements MenuProvider {

    public Provider() {
      super();
    }

    @Override
    public AbstractContainerMenu createMenu(int i, final Inventory playerInv, final Player player) {
      if (player.isSpectator()) {
        return null;
      } else {
        return new PortableCraftingMenu(i, playerInv, ContainerLevelAccess.create(player.level, new BlockPos(player.position())));
      }
    }

    @Override
    public Component getDisplayName() {
      return Blocks.CRAFTING_TABLE.getName();
    }
  }
}