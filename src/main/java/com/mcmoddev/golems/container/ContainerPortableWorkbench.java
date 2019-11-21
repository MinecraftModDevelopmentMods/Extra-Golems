package com.mcmoddev.golems.container;

import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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
	
	public static class Provider implements INamedContainerProvider {
		
		public Provider() {
			super();
		}
		
		@Override
		public Container createMenu(int i, final PlayerInventory playerInv, final PlayerEntity player) {
			if (player.isSpectator()) {
				return null;
			} else {
				return new ContainerPortableWorkbench(i, playerInv, IWorldPosCallable.of(player.world, new BlockPos(player)));
			}
		}

		@Override
		public ITextComponent getDisplayName() {
			return new TranslationTextComponent("entity.golems." + GolemNames.CRAFTING_GOLEM);
		}
	}
}