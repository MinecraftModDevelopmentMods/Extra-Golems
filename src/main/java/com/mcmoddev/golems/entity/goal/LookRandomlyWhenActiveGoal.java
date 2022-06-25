package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.IFuelConsumer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

public class LookRandomlyWhenActiveGoal<T extends Mob & IFuelConsumer> extends RandomLookAroundGoal {

	protected T entity;

	public LookRandomlyWhenActiveGoal(T entitylivingIn) {
		super(entitylivingIn);
		entity = entitylivingIn;
	}

	@Override
	public boolean canUse() {
		return entity.hasFuel() && super.canUse();
	}

	@Override
	public boolean canContinueToUse() {
		return entity.hasFuel() && super.canContinueToUse();
	}
}
