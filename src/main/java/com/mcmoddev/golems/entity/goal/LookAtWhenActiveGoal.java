package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.IFuelConsumer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

public class LookAtWhenActiveGoal<T extends Mob & IFuelConsumer> extends LookAtPlayerGoal {

	protected T entity;

	public LookAtWhenActiveGoal(T entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance) {
		super(entityIn, watchTargetClass, maxDistance);
		this.entity = entityIn;
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
