package com.mcmoddev.golems.container.behavior;

import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * This behavior allows an entity to burn in sunlight
 * and seek shelter from the sun during the day
 **/
@Immutable
public class BurnInSunBehavior extends GolemBehavior {

	/**
	 * The goal priority
	 **/
	private final int priority;
	/**
	 * The percent chance [0,1] to apply each tick
	 **/
	private final double chance;

	public BurnInSunBehavior(CompoundTag tag) {
		super(tag);
		priority = tag.getInt("priority");
		chance = tag.getDouble("chance");
	}

	public int getPriority() {
		return priority;
	}

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		entity.goalSelector.addGoal(priority, new RestrictSunGoal(entity));
		entity.goalSelector.addGoal(priority, new BurnInSunGoal(entity, (float) chance));
		entity.goalSelector.addGoal(priority, new FleeSunGoal(entity, 1.1D));
	}

	@Override
	public void onAddDescriptions(List<Component> list) {
		list.add(Component.translatable("entitytip.burn_in_sun").withStyle(ChatFormatting.DARK_PURPLE));
	}
}
