package com.mcmoddev.golems.entity.ai;

import java.util.EnumSet;
import java.util.Random;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class GoToWaterGoal extends Goal {

  private final GolemBase golem;
  private final int detectWaterRadius;
  private double targetX;
  private double targetY;
  private double targetZ;
  private final double speed;
  private final World world;

  public GoToWaterGoal(final GolemBase golemBase, final int radius, final double speed) {
    this.golem = golemBase;
    this.detectWaterRadius = radius;
    this.speed = speed;
    this.world = golemBase.world;
    setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
  }

  @Override
  public boolean shouldExecute() {
    if (this.golem.isInWater()) {
      return false;
    }

    Vector3d target = getNearbyWater();
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
    this.golem.getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, this.speed);
  }

  private Vector3d getNearbyWater() {
    Random rand = this.golem.getRNG();

    BlockPos pos1 = this.golem.getBlockBelow();

    for (int i = 0; i < 10; i++) {
      BlockPos pos2 = pos1.add(rand.nextInt(detectWaterRadius * 2) - detectWaterRadius, 2 - rand.nextInt(8),
          rand.nextInt(detectWaterRadius * 2) - detectWaterRadius);

      if (this.world.getBlockState(pos2).getBlock() == Blocks.WATER) {
        return new Vector3d(pos2.getX(), pos2.getY(), pos2.getZ());
      }
    }
    return null;
  }

}