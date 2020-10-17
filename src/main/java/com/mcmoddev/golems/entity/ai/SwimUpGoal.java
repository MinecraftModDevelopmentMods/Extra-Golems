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
    return (!golem.world.isDaytime() && golem.isInWater() && golem.getPosY() < (this.targetY - 2.5D));
  }

  @Override
  public boolean shouldContinueExecuting() {
    return (shouldExecute() && !this.obstructed);
  }

  @Override
  public void tick() {
    if (golem.getPosY() < (this.targetY - 1) && (golem.getNavigator().noPath() || isCloseToPathTarget())) {

      Vec3d vec = RandomPositionGenerator.findRandomTargetBlockTowards(golem, 4, 8,
          new Vec3d(golem.getPosX(), (this.targetY - 1), golem.getPosZ()));

      if (vec == null) {
        this.obstructed = true;
        return;
      }
      golem.getNavigator().tryMoveToXYZ(vec.x, vec.y, vec.z, this.speed);
    }
  }

  @Override
  public void startExecuting() {
    golem.setSwimmingUp(true);
    this.obstructed = false;
  }

  @Override
  public void resetTask() {
    golem.setSwimmingUp(false);
  }

  private boolean isCloseToPathTarget() {
    Path path = golem.getNavigator().getPath();
    if (path != null) {
      BlockPos pos = path.getTarget();
      if (pos != null) {
        double dis = golem.getDistanceSq(pos.getX(), pos.getY(), pos.getZ());
        if (dis < 4.0D) {
          return true;
        }
      }
    }
    return false;
  }
}
