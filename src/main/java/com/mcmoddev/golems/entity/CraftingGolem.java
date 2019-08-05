package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public final class CraftingGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Crafting";
	
	public CraftingGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
		this.enableSwim();
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
			ITextComponent itextcomponent = new TranslationTextComponent("");
			INamedContainerProvider provider = new SimpleNamedContainerProvider((i, inv, call) -> 
				new ContainerPortableWorkbench(i, inv, IWorldPosCallable.of(player.world, new BlockPos(player))), itextcomponent);
			player.openContainer(provider);
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
}
