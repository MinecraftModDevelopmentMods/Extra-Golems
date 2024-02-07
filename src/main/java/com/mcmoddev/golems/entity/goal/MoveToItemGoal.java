package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.IVariantProvider;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.EnumSet;
import java.util.List;

public class MoveToItemGoal<T extends Mob & IVariantProvider> extends Goal implements IVariantPredicate {

	protected final T entity;
	protected final double range;
	protected final double interval;
	protected final double speed;
	private final MinMaxBounds.Ints variant;

	public MoveToItemGoal(final T entityIn, final double rangeIn, final int intervalIn, final double speedIn, final MinMaxBounds.Ints variant) {
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		this.entity = entityIn;
		this.range = rangeIn;
		this.interval = Math.max(intervalIn, 1);
		this.speed = speedIn;
		this.variant = variant;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public MinMaxBounds.Ints getVariantBounds() {
		return this.variant;
	}

	@Override
	public boolean canUse() {
		return entity.tickCount % interval == 0 && isVariantInBounds(entity);
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}

	@Override
	public void tick() {
		// make a list of itemstacks in nearby area
		final List<ItemEntity> items = entity.level().getEntities(EntityType.ITEM, entity.getBoundingBox().inflate(range),
				e -> !e.isRemoved() && !e.getItem().isEmpty() && !e.hasPickUpDelay() && entity.wantsToPickUp(e.getItem()));

		if (!items.isEmpty()) {
			// path toward the nearest itemstack
			items.sort((e1, e2) -> (int) (entity.distanceToSqr(e1) - entity.distanceToSqr(e2)));
			entity.getNavigation().moveTo(items.get(0), speed);
		}
	}
}
