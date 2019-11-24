package com.golems.container;

import com.golems.util.GolemNames;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;

public class ContainerDispenserGolem extends Container {

	public static final String GUI_ID = "dispenser_portable";

	public ContainerDispenserGolem(final IInventory playerInventory, final IInventory dispenserInventoryIn) {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlotToContainer(new ArrowSlot(dispenserInventoryIn, j + i * 3, 62 + j * 18, 17 + i * 18));
			}
		}

		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}

		for (int l = 0; l < 9; ++l) {
			this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 142));
		}
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	@Override
	public boolean canInteractWith(final EntityPlayer playerIn) {
		return !playerIn.isSpectator();
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this
	 * moves the stack between the player inventory and the other inventory(s).
	 */
	@Override
	public ItemStack transferStackInSlot(final EntityPlayer playerIn, final int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < 9) {
				if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 9, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, itemstack1);
		}

		return itemstack;
	}

	public static class ArrowSlot extends Slot {

		public ArrowSlot(final IInventory inventoryIn, final int index, final int xPosition, final int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(final ItemStack stack) {
			return !stack.isEmpty() && stack.getItem() instanceof ItemArrow;
		}
	}

	public static class Provider implements IInteractionObject {

		private final IInventory inventory;

		public Provider(final IInventory inv) {
			super();
			inventory = inv;
		}

		@Override
		public String getName() {
			return getDisplayName().getFormattedText();
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public ITextComponent getDisplayName() {
			return new TextComponentTranslation("entity.golems." + GolemNames.DISPENSER_GOLEM + ".name");
		}

		@Override
		public Container createContainer(final InventoryPlayer playerInventory, final EntityPlayer playerIn) {
			if (playerIn.isSpectator()) {
				return null;
			} else {
				return new ContainerDispenserGolem(playerInventory, inventory);
			}
		}

		@Override
		public String getGuiID() {
			return GUI_ID;
		}
	}
}
