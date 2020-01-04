package com.mcmoddev.golems.entity.ai;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SwimUpGoal extends Goal {

	private final GolemBase golem;
	private final double speed;
	private final int targetY;
	private boolean obstructed;

	public SwimUpGoal(GolemBase golemIn, double speedIn, int seaLevel) {
		this.golem = golemIn;
		this.speed = speedIn;
		this.targetY = seaLevel;
	}

	public boolean shouldExecute() {
		return (this.golem.isInWater() && this.golem.getPositionVec().y < (this.targetY - 2));
	}

	@Override
	public boolean shouldContinueExecuting() {
		return (shouldExecute() && !this.obstructed);
	}

	@Override
	public void tick() {
		final Vec3d gPos = golem.getPositionVec();
		if (gPos.y < (this.targetY - 1) && (this.golem.getNavigator().noPath() || isCloseToPathTarget())) {

			Vec3d vec = RandomPositionGenerator.findRandomTargetBlockTowards(this.golem, 4, 8,
					new Vec3d(gPos.x, (this.targetY - 1), gPos.z));
			if (vec == null) {
				this.obstructed = true;
				return;
			}
			this.golem.getNavigator().tryMoveToXYZ(vec.x, vec.y, vec.z, this.speed);
		}
	}

	@Override
	public void startExecuting() {
		this.golem.setSwimmingUp(true);
		this.obstructed = false;
	}

	@Override
	public void resetTask() {
		this.golem.setSwimmingUp(false);
	}
	
	public boolean isCloseToPathTarget() {
		Path path = this.golem.getNavigator().getPath();
		if (path != null) {
			BlockPos pos = path.func_224770_k();
			if (pos != null) {
				double dis = this.golem.getDistanceSq(pos.getX(), pos.getY(), pos.getZ());
				if (dis < 4.0D) {
					return true;
				}
			}
		}
		return false;
	}
}