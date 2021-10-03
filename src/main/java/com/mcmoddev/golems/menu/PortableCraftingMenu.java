package com.mcmoddev.golems.menu;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PortableCraftingMenu extends WorkbenchContainer {

  public PortableCraftingMenu(final int i, final PlayerInventory inv) {
    this(i, inv, null);
  }

  public PortableCraftingMenu(final int i, final PlayerInventory inv, final IWorldPosCallable call) {
    super(i, inv, call);
  }

  @Override
  public boolean canInteractWith(final PlayerEntity playerIn) {
    return true;
  }

  public static class Provider implements INamedContainerProvider {

    public Provider() {
      super();
    }

    @Override
    public Container createMenu(int i, final PlayerInventory playerInv, final PlayerEntity player) {
      if (player.isSpectator()) {
        return null;
      } else {
        return new PortableCraftingMenu(i, playerInv, IWorldPosCallable.of(player.world, player.getPosition()));
      }
    }

    @Override
	public ITextComponent getDisplayName() { return new TranslationTextComponent(Blocks.CRAFTING_TABLE.getTranslationKey()); }
  }
}
