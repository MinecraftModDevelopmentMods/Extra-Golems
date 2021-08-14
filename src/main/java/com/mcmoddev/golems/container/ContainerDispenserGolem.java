package com.mcmoddev.golems.container;

import com.mcmoddev.golems.GolemItems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class ContainerDispenserGolem extends AbstractContainerMenu {

  private final Container dispenserInventory;

  public ContainerDispenserGolem(final int id, final Inventory playerInv) {
    this(id, playerInv, new SimpleContainer(9));
  }

  public ContainerDispenserGolem(final int id, final Inventory playerInv, final Container inv) {
    super(GolemItems.DISPENSER_GOLEM, id);
    checkContainerSize(inv, 9);
    this.dispenserInventory = inv;
    inv.startOpen(playerInv.player);

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        addSlot(new ArrowSlot(inv, j + i * 3, 62 + j * 18, 17 + i * 18));
      }
    }

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 9; j++) {
        addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
      }
    }
    for (int i = 0; i < 9; i++) {
      addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
    }
  }

  @Override
  public boolean stillValid(final Player playerIn) {
    return true;
  }

  @Override
  public ItemStack quickMoveStack(final Player player, final int slotIndex) {
    ItemStack stack = ItemStack.EMPTY;
    Slot slot = this.slots.get(slotIndex);
    if (slot != null && slot.hasItem()) {
      ItemStack slotStack = slot.getItem();
      stack = slotStack.copy();

      if (slotIndex < 9) {
        if (!moveItemStackTo(slotStack, 9, 45, true)) {
          return ItemStack.EMPTY;
        }
      } else if (!moveItemStackTo(slotStack, 0, 9, false)) {
        return ItemStack.EMPTY;
      }

      if (slotStack.isEmpty()) {
        slot.set(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }
      if (slotStack.getCount() == stack.getCount()) {
        return ItemStack.EMPTY;
      }
      slot.onTake(player, slotStack);
    }

    return stack;
  }

  @Override
  public void removed(final Player player) {
    super.removed(player);
    this.dispenserInventory.stopOpen(player);
  }

  public static class Provider implements MenuProvider {

    private final Container inventory;

    public Provider(final Container inv) {
      super();
      inventory = inv;
    }

    @Override
    public AbstractContainerMenu createMenu(int i, final Inventory playerInv, final Player player) {
      if (player.isSpectator()) {
        return null;
      } else {
        return new ContainerDispenserGolem(i, playerInv, inventory);
      }
    }

    @Override
    public Component getDisplayName() {
      return new TranslatableComponent("entity.golems." + GolemNames.DISPENSER_GOLEM);
    }
  }

  public static class ArrowSlot extends Slot {

    public ArrowSlot(Container inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(final ItemStack stack) {
      return stack.isEmpty() || stack.getItem() instanceof ArrowItem;
    }
  }
}
