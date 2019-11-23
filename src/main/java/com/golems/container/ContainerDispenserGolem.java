package com.golems.container;

import javax.annotation.Nullable;

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
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ContainerDispenserGolem extends Container {
	
	public static final String GUI_ID = "dispenser_portable";

	public ContainerDispenserGolem(final IInventory playerInv, final IInventory dispenserInv) {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlotToContainer(new ArrowSlot(dispenserInv, j + i * 3, 62 + j * 18, 17 + i * 18));
			}
		}

		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlotToContainer(new Slot(playerInv, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}

		for (int l = 0; l < 9; ++l) {
			this.addSlotToContainer(new Slot(playerInv, l, 8 + l * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(final EntityPlayer playerIn) {
		return !playerIn.isSpectator();
	}

	/**
	 * Take a stack from the specified inventory slot.
	 */
	@Override
	@Nullable
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < 9) {
				if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 9, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(playerIn, itemstack1);
		}

		return itemstack;
	}

	public static class ArrowSlot extends Slot {

		public ArrowSlot(final IInventory inventoryIn, final int index, final int xPosition, final int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(final ItemStack stack) {
			return stack != null && stack.getItem() instanceof ItemArrow;
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
			return null;
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
//
//	public static class GuiHandler implements IGuiHandler {
//
//		@Override
//		public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
//			return 
//		}
//
//		@Override
//		public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
//			return new com.golems.gui.GuiDispenserGolem(player.openContainer);
//		}
//		
//	}
}
