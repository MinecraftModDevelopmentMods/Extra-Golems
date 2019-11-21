package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.container.ContainerPortableWorkbench;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public final class CraftingGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Crafting";
	
	public CraftingGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
	}

	@Override
	protected boolean processInteract(final PlayerEntity player, final Hand hand) {
		if(!player.isSneaking() && player instanceof ServerPlayerEntity) {
			// display crafting grid by sending request to server
			NetworkHooks.openGui((ServerPlayerEntity)player, new ContainerPortableWorkbench.Provider());
			player.addStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
			player.swingArm(hand);
			return true;
		}
		return super.processInteract(player, hand);
	}
}
