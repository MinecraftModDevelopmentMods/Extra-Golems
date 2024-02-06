package com.mcmoddev.golems.menu;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.entity.IExtraGolem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class GolemInventoryMenu extends AbstractContainerMenu {

	private final Container container;
	private final @Nullable IExtraGolem entity;
	private final ContainerLevelAccess access;

	public GolemInventoryMenu(final int id, final Inventory playerInv) {
		this(id, playerInv, new SimpleContainer(9), null, ContainerLevelAccess.create(playerInv.player.level(), playerInv.player.blockPosition()));
	}

	public GolemInventoryMenu(final int id, final Inventory playerInv, final Container inv, @Nullable final IExtraGolem entity, ContainerLevelAccess access) {
		super(EGRegistry.MenuReg.DISPENSER_GOLEM_MENU.get(), id);
		checkContainerSize(inv, 9);
		this.container = inv;
		this.entity = entity;
		this.access = access;
		inv.startOpen(playerInv.player);

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
	public ItemStack quickMoveStack(final Player player, final int slotIndex) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if (slot.hasItem()) {
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
	public boolean stillValid(final Player player) {
		return access.evaluate((level, blockPos) -> player.position().closerThan(Vec3.atCenterOf(blockPos), 8.0D), true);
	}

	@Override
	public void removed(Player player) {
		if(this.entity != null && this.entity.getPlayerInMenu() == player) {
			this.entity.setPlayerInMenu(null);
		}
		super.removed(player);
		this.container.stopOpen(player);
	}
}
