package com.mcmoddev.golems.entity.ai;

import java.util.EnumSet;
import java.util.Random;

import com.mcmoddev.golems.entity.CoralGolem;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.block.Blocks;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GoToWaterGoal extends Goal {

	private final GolemBase golem;
	private double targetX;
	private double targetY;
	private double targetZ;
	private final double speed;
	private final World world;

	public GoToWaterGoal(GolemBase golemBase, double speed) {
		this.golem = golemBase;
		this.speed = speed;
		this.world = golemBase.world;
		setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean shouldExecute() {
		if (this.golem.isInWater()) {
			return false;
		}

		Vec3d target = getNearbyWater();
		if (target == null || !this.golem.shouldMoveToWater(target)) {
			return false;
		}
		
		this.targetX = target.x;
		this.targetY = target.y;
		this.targetZ = target.z;
		return true;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !this.golem.getNavigator().noPath();
	}

	@Override
	public void startExecuting() {
		this.golem.getNavigator().tryMoveToXYZ(this.targetX, this.targetY,
				this.targetZ, this.speed);
	}

	private Vec3d getNearbyWater() {
		Random rand = this.golem.getRNG();
		BlockPos pos1 = new BlockPos(this.golem.posX, (this.golem.getBoundingBox()).minY,
				this.golem.posZ);

		for (int i = 0; i < 10; i++) {
			BlockPos pos2 = pos1.add(rand.nextInt(20) - 10, 2 - rand.nextInt(8), rand.nextInt(20) - 10);

			if (this.world.getBlockState(pos2).getBlock() == Blocks.WATER) {
				return new Vec3d(pos2.getX(), pos2.getY(), pos2.getZ());
			}
		}
		return null;
	}

}