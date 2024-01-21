package com.mcmoddev.golems.entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GoToWaterGoal extends Goal {

	private final PathfinderMob entity;
	private final int detectWaterRadius;
	private double targetX;
	private double targetY;
	private double targetZ;
	private final double speed;

	public GoToWaterGoal(final PathfinderMob entity, final int radius, final double speed) {
		setFlags(EnumSet.of(Goal.Flag.MOVE));
		this.entity = entity;
		this.detectWaterRadius = radius;
		this.speed = speed;
	}

	//// GOAL ////

	protected boolean shouldMoveToWater(final Vec3 target) {
		return true;
	}

	@Override
	public boolean canUse() {
		if (this.entity.isInWater()) {
			return false;
		}

		Vec3 target = getNearbyWater();
		if (target == null || !shouldMoveToWater(target)) {
			return false;
		}

		this.targetX = target.x;
		this.targetY = target.y;
		this.targetZ = target.z;
		return true;
	}

	@Override
	public boolean canContinueToUse() {
		return !this.entity.getNavigation().isDone();
	}

	@Override
	public void start() {
		this.entity.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	private Vec3 getNearbyWater() {
		final BlockPos.MutableBlockPos pos = this.entity.blockPosition().mutable();
		for (int i = 0; i < 10; i++) {
			// select random position
			pos.setWithOffset(entity.blockPosition(), this.entity.getRandom().nextInt(detectWaterRadius * 2) - detectWaterRadius,
					2 - this.entity.getRandom().nextInt(8),
					this.entity.getRandom().nextInt(detectWaterRadius * 2) - detectWaterRadius);
			// check for water at this position
			if (this.entity.level().getBlockState(pos).getBlock() == Blocks.WATER) {
				return pos.getCenter();
			}
		}
		// no position found
		return null;
	}

}
