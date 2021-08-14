package com.mcmoddev.golems.container;

import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class ContainerPortableWorkbench extends CraftingMenu {

  public ContainerPortableWorkbench(final int i, final Inventory inv) {
    this(i, inv, null);
  }

  public ContainerPortableWorkbench(final int i, final Inventory inv, final ContainerLevelAccess call) {
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
        return new ContainerPortableWorkbench(i, playerInv, ContainerLevelAccess.create(player.level, new BlockPos(player.position())));
      }
    }

    @Override
    public Component getDisplayName() {
      return new TranslatableComponent("entity.golems." + GolemNames.CRAFTING_GOLEM);
    }
  }
}