package com.mcmoddev.golems.entity;

import java.util.List;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityCraftingGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Crafting";
	
	public EntityCraftingGolem(final World world) {
		super(EntityCraftingGolem.class, world);
		this.setLootTableLoc(GolemNames.CRAFTING_GOLEM);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.CRAFTING_GOLEM);
	}

	@Override
	protected boolean processInteract(final EntityPlayer player, final EnumHand hand) {
		final ItemStack itemstack = player.getHeldItem(hand);
		if (!player.world.isRemote && itemstack.isEmpty()) {
			// display crafting grid for player
			player.displayGui(new EntityCraftingGolem.InterfaceCraftingGrid(player.world,
					player.bedLocation));
			player.addStat(StatList.INTERACT_WITH_CRAFTING_TABLE);
			player.swingArm(hand);
		}

		return super.processInteract(player, hand);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_WOOD_STEP;
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if(this.getConfigBool(ALLOW_SPECIAL)) {
			list.add(TextFormatting.BLUE + trans("entitytip.click_open_crafting"));
		}
		return list;
	}
	
	public static class ContainerPortableWorkbench extends ContainerWorkbench {

		public ContainerPortableWorkbench(final InventoryPlayer playerInventory, final World worldIn,
						  final BlockPos posIn) {
			super(playerInventory, worldIn, posIn);
		}

		@Override
		public boolean canInteractWith(final EntityPlayer playerIn) {
			return true;
		}
	}

	public static class InterfaceCraftingGrid
			extends net.minecraft.block.BlockWorkbench.InterfaceCraftingTable {

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
