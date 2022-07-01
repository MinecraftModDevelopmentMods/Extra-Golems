package com.mcmoddev.golems.container.behavior;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.AoeBlocksGoal;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * This behavior allows an entity to remove water or
 * waterlogged blocks in an area
 **/
@Immutable
public class AoeDryBehavior extends GolemBehavior {

	/**
	 * The radius for which the behavior will apply
	 **/
	protected final int range;
	/**
	 * The average number of ticks between application of this behavior
	 **/
	protected final int interval;
	/**
	 * True to affect a spherical area, false to affect a disc area
	 **/
	protected final boolean sphere;

	public AoeDryBehavior(CompoundTag tag) {
		super(tag);
		range = tag.getInt("range");
		interval = tag.getInt("interval");
		sphere = tag.getBoolean("sphere");
	}

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		entity.goalSelector.addGoal(1, new AoeBlocksGoal(entity, range, interval, sphere, new AoeBlocksGoal.DryFunction()));
	}

	@Override
	public void onAddDescriptions(List<Component> list) {
		list.add(Component.translatable("entitytip.aoe_dry").withStyle(ChatFormatting.GOLD));
	}
}
