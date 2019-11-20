package com.mcmoddev.golems.container;

import com.mcmoddev.golems.main.GolemItems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ContainerDispenserGolem extends Container {

	private final IInventory dispenserInventory;

	public ContainerDispenserGolem(final int id, final PlayerInventory playerInv) {
		this(id, playerInv, new Inventory(9));
	}

	public ContainerDispenserGolem(final int id, final PlayerInventory playerInv, final IInventory inv) {
		super(GolemItems.DISPENSER_GOLEM, id);
		assertInventorySize(inv, 9);
		this.dispenserInventory = inv;
		inv.openInventory(playerInv.player);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				addSlot(new Slot(inv, j + i * 3, 62 + j * 18, 17 + i * 18));
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
	public boolean canInteractWith(final PlayerEntity playerIn) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(final PlayerEntity player, final int slotIndex) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(slotIndex);
		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();

			if (slotIndex < 9) {
				if (!mergeItemStack(slotStack, 9, 45, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!mergeItemStack(slotStack, 0, 9, false)) {
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			if (slotStack.getCount() == stack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, slotStack);
		}

		return stack;
	}

	@Override
	public void onContainerClosed(final PlayerEntity player) {
		super.onContainerClosed(player);
		this.dispenserInventory.closeInventory(player);
	}

	public static class Provider implements INamedContainerProvider {
		
		private final IInventory inventory;

		public Provider(final IInventory inv) {
			super();
			inventory = inv;
		}
		
		@Override
		public Container createMenu(int i, final PlayerInventory playerInv, final PlayerEntity player) {
			if (player.isSpectator()) {
				return null;
			} else {
				return new ContainerDispenserGolem(i, playerInv, inventory);
			}
		}

		@Override
		public ITextComponent getDisplayName() {
			return new TranslationTextComponent("entity.golems.golem_dispenser");
		}
	}
}
