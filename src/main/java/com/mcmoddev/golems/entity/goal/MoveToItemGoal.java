package com.mcmoddev.golems.entity.goal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.EnumSet;
import java.util.List;

public class MoveToItemGoal extends Goal {

	protected final Mob entity;
	protected final double range;
	protected final double interval;
	protected final double speed;

	public MoveToItemGoal(final Mob entityIn, final double rangeIn, final int intervalIn, final double speedIn) {
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		entity = entityIn;
		range = rangeIn;
		interval = Math.max(intervalIn, 1);
		speed = speedIn;
	}

	@Override
	public boolean canUse() {
		return entity.tickCount % interval == 0;
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}

	@Override
	public void tick() {
		// make a list of arrow itemstacks in nearby area
		final List<ItemEntity> items = entity.level().getEntities(EntityType.ITEM, entity.getBoundingBox().inflate(range),
				e -> !e.isRemoved() && !e.getItem().isEmpty() && !e.hasPickUpDelay() && entity.wantsToPickUp(e.getItem()));

		if (!items.isEmpty()) {
			// path toward the nearest arrow itemstack
			items.sort((e1, e2) -> (int) (entity.distanceToSqr(e1) - entity.distanceToSqr(e2)));
			entity.getNavigation().moveTo(items.get(0), speed);
		}
	}
}
