package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.IRandomExploder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class ExplodeGoal<T extends Mob & IRandomExploder> extends Goal {

	protected final T entity;
	/**
	 * Explosion radius
	 **/
	protected final float range;

	public ExplodeGoal(T entity, float range) {
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		this.entity = entity;
		this.range = range;
	}

	@Override
	public boolean canUse() {
		return entity.isFuseLit();
	}

	@Override
	public void start() {
		entity.resetFuse();
	}

	@Override
	public void tick() {
		entity.setFuse(entity.getFuse() - 1);
		entity.getNavigation().stop();
		if (entity.isInWaterRainOrBubble()) {
			// reset fuse and play sound when wet
			stop();
			entity.playSound(SoundEvents.FIRE_EXTINGUISH, 0.9F, entity.getRandom().nextFloat());
		} else if (entity.getFuse() <= 0) {
			entity.explode(range);
		}
	}

	@Override
	public void stop() {
		entity.resetFuseLit();
	}
}
