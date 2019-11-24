package com.golems.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
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

	public static class InterfaceCraftingGrid extends net.minecraft.block.BlockWorkbench.InterfaceCraftingTable {

		private final World world2;
		private final BlockPos position2;

		public InterfaceCraftingGrid(final World worldIn, final BlockPos pos) {
			super(worldIn, pos);
			this.world2 = worldIn;
			this.position2 = pos;
		}

		@Override
		public Container createContainer(final InventoryPlayer playerInventory, final EntityPlayer playerIn) {
			return new ContainerPortableWorkbench(playerInventory, this.world2, this.position2);
		}
	}

}
