package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GoToWaterGoal extends Goal {

	private final GolemBase golem;
	private final int detectWaterRadius;
	private double targetX;
	private double targetY;
	private double targetZ;
	private final double speed;
	private final Level world;

	public GoToWaterGoal(final GolemBase golemBase, final int radius, final double speed) {
		setFlags(EnumSet.of(Goal.Flag.MOVE));
		this.golem = golemBase;
		this.detectWaterRadius = radius;
		this.speed = speed;
		this.world = golemBase.level();
	}

	@Override
	public boolean canUse() {
		if (this.golem.isInWater()) {
			return false;
		}

		Vec3 target = getNearbyWater();
		if (target == null || !this.golem.shouldMoveToWater(target)) {
			return false;
		}

		this.targetX = target.x;
		this.targetY = target.y;
		this.targetZ = target.z;
		return true;
	}

	@Override
	public boolean canContinueToUse() {
		return !this.golem.getNavigation().isDone();
	}

	@Override
	public void start() {
		this.golem.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, this.speed);
	}

	private Vec3 getNearbyWater() {

		BlockPos pos1 = this.golem.getBlockBelow();

		for (int i = 0; i < 10; i++) {
			BlockPos pos2 = pos1.offset(
					this.golem.getRandom().nextInt(detectWaterRadius * 2) - detectWaterRadius,
					2 - this.golem.getRandom().nextInt(8),
					this.golem.getRandom().nextInt(detectWaterRadius * 2) - detectWaterRadius);

			if (this.world.getBlockState(pos2).getBlock() == Blocks.WATER) {
				return new Vec3(pos2.getX(), pos2.getY(), pos2.getZ());
			}
		}
		return null;
	}

}
