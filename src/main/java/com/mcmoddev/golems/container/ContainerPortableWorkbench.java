package com.mcmoddev.golems.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.IWorldPosCallable;

public class ContainerPortableWorkbench extends WorkbenchContainer {
	
	public ContainerPortableWorkbench(final int i, final PlayerInventory inv) {
		this(i, inv, null);
	}

	public ContainerPortableWorkbench(final int i, final PlayerInventory inv, final IWorldPosCallable call) {
		super(i, inv, call);
	}

	@Override
	public boolean canInteractWith(final PlayerEntity playerIn) {
		return true;
	}
}