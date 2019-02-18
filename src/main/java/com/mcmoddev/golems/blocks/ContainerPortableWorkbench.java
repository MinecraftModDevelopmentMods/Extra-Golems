package com.mcmoddev.golems.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerPortableWorkbench extends ContainerWorkbench {

	protected BlockPos blockPos;

	public ContainerPortableWorkbench(final InventoryPlayer playerInventory, final World worldIn,
					  final BlockPos posIn) {
		super(playerInventory, worldIn, posIn);
		this.blockPos = posIn;
	}

	@Override
	public boolean canInteractWith(final EntityPlayer playerIn) {
		return true;
	}

}
