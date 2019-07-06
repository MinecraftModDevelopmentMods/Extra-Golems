package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class CraftingGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Crafting";
	
	public CraftingGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.CRAFTING_GOLEM);
	}

	@Override
	protected boolean processInteract(final PlayerEntity player, final Hand hand) {
		final ItemStack itemstack = player.getHeldItem(hand);
		if (!player.world.isRemote && itemstack.isEmpty()) {
			// display crafting grid for player
			player.displayGui(new CraftingGolem.InterfaceCraftingGrid(player.world,
					new BlockPos(player)));
			player.addStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
			player.swingArm(hand);
		}

		return super.processInteract(player, hand);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_WOOD_STEP;
	}

	public static class ContainerPortableWorkbench extends WorkbenchContainer {

		public ContainerPortableWorkbench(final PlayerInventory playerInventory, final World worldIn,
				final BlockPos posIn) {
			super(playerInventory, worldIn, posIn);
		}

		@Override
		public boolean canInteractWith(final PlayerEntity playerIn) {
			return true;
		}
	}

	public static class InterfaceCraftingGrid
		extends net.minecraft.block.CraftingTableBlock.InterfaceCraftingTable {

		private final World world2;
		private final BlockPos position2;

		public InterfaceCraftingGrid(final World worldIn, final BlockPos pos) {
			super(worldIn, pos);
			this.world2 = worldIn;
			this.position2 = pos;
		}

		@Override
		public Container createContainer(final InventoryPlayer playerInventory, final PlayerEntity playerIn) {
			return new ContainerPortableWorkbench(playerInventory, this.world2, this.position2);
		}
	}
}
