package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

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

  public boolean canUse() {
    return (!golem.level.isDay() && golem.isInWater() && golem.getY() < (this.targetY - 2.5D));
  }

  @Override
  public boolean canContinueToUse() {
    return (canUse() && !this.obstructed);
  }

  @Override
  public void tick() {
    if (golem.getY() < (this.targetY - 1) && (golem.getNavigation().isDone() || isCloseToPathTarget())) {

      Vec3 vec = DefaultRandomPos.getPosTowards(this.golem, 4, 8, new Vec3(this.golem.getX(), (double)(this.targetY - 1), this.golem.getZ()), (double)((float)Math.PI / 2F));

      if (vec == null) {
        this.obstructed = true;
        return;
      }
      golem.getNavigation().moveTo(vec.x, vec.y, vec.z, this.speed);
    }
  }

  @Override
  public void start() {
    golem.setSwimmingUp(true);
    this.obstructed = false;
  }

  @Override
  public void stop() {
    golem.setSwimmingUp(false);
  }

  private boolean isCloseToPathTarget() {
    Path path = golem.getNavigation().getPath();
    if (path != null) {
      BlockPos pos = path.getTarget();
      if (pos != null) {
        double dis = golem.distanceToSqr(pos.getX(), pos.getY(), pos.getZ());
        if (dis < 4.0D) {
          return true;
        }
      }
    }
    return false;
  }
}
