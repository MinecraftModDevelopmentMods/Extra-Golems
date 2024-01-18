package com.mcmoddev.golems.container.behavior;

import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * This behavior allows an entity to grow crops in an area
 **/
@Immutable
public class AoeGrowBehavior extends GolemBehavior {

	/**
	 * The radius for which the behavior will apply
	 **/
	protected final int range;
	/**
	 * The average number of ticks between application of this behavior
	 **/
	protected final int interval;
	/**
	 * The percent chance [0,1] to affect each block
	 **/
	protected final double chance;

	public AoeGrowBehavior(CompoundTag tag) {
		super(tag);
		range = tag.getInt("range");
		interval = tag.getInt("interval");
		chance = tag.getDouble("chance");
	}

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		entity.goalSelector.addGoal(1, new AoeBlocksGoal(entity, range, interval, false,
				new AoeBlocksGoal.GrowMapper((float) chance)));
	}

	@Override
	public void onAddDescriptions(List<Component> list) {
		list.add(Component.translatable("entitytip.aoe_grow").withStyle(ChatFormatting.GOLD));
	}
}
