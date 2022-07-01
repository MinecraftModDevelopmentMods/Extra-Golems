package com.mcmoddev.golems.container.behavior;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.InertGoal;
import com.mcmoddev.golems.entity.goal.LookAtWhenActiveGoal;
import com.mcmoddev.golems.entity.goal.LookRandomlyWhenActiveGoal;
import com.mcmoddev.golems.entity.goal.UseFuelGoal;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;

/**
 * This behavior allows an entity to use fuel, accept fuel items,
 * only move and attack while fueled, and save/load fuel
 **/
@Immutable
public class UseFuelBehavior extends GolemBehavior {

	/**
	 * The maximum amount of fuel the entity can accept
	 **/
	protected final int maxFuel;
	/**
	 * The number of ticks it takes to deplete one unit of fuel
	 **/
	protected final int interval;

	public UseFuelBehavior(CompoundTag tag) {
		super(tag);
		maxFuel = tag.getInt("max_fuel");
		interval = tag.getInt("burn_interval");
	}

	/**
	 * @return The maximum amount of fuel the entity can accept
	 **/
	public int getMaxFuel() {
		return maxFuel;
	}

	/**
	 * @return The number of ticks it takes to deplete one unit of fuel
	 **/
	public int getInterval() {
		return interval;
	}

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		removeGoal(entity, LookAtPlayerGoal.class);
		removeGoal(entity, RandomLookAroundGoal.class);
		entity.goalSelector.addGoal(0, new InertGoal<>(entity));
		entity.goalSelector.addGoal(1, new UseFuelGoal<>(entity, interval));
		entity.goalSelector.addGoal(7, new LookAtWhenActiveGoal<>(entity, Player.class, 6.0F));
		entity.goalSelector.addGoal(8, new LookRandomlyWhenActiveGoal<>(entity));
	}

	@Override
	public void onMobInteract(final GolemBase entity, final Player player, final InteractionHand hand) {
		if (!player.isCrouching() && !player.getItemInHand(hand).isEmpty()) {
			entity.consumeFuel(player, hand);
		}
	}

	@Override
	public void onWriteData(final GolemBase entity, final CompoundTag tag) {
		entity.saveFuel(tag);
	}

	@Override
	public void onReadData(final GolemBase entity, final CompoundTag tag) {
		entity.loadFuel(tag);
	}

	@Override
	public void onAddDescriptions(List<Component> list) {
		list.add(Component.translatable("entitytip.use_fuel").withStyle(ChatFormatting.GRAY));
	}

	protected static boolean removeGoal(final GolemBase entity, final Class<? extends Goal> goalToRemove) {
		final List<Goal> goalsToRemove = new ArrayList<>();
		entity.goalSelector.availableGoals.forEach(g -> {
			if (g.getGoal().getClass() == goalToRemove) {
				goalsToRemove.add(g.getGoal());
			}
		});
		// remove the matching goals
		goalsToRemove.forEach(g -> entity.goalSelector.removeGoal(g));
		return !goalsToRemove.isEmpty();
	}
}
