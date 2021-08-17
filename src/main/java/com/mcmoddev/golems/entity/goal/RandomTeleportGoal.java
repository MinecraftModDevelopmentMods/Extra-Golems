package com.mcmoddev.golems.entity.goal;

import java.util.EnumSet;

import com.mcmoddev.golems.entity.IRandomTeleporter;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class RandomTeleportGoal<T extends Mob & IRandomTeleporter> extends Goal {
  
  protected final T entity;
  protected final double range;
  protected final double chanceOnIdle;
  protected final double chanceOnTarget;

  public RandomTeleportGoal(T entity, double range, double chanceOnIdle, double chanceOnTarget) {
    super();
    this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
    this.entity = entity;
    this.range = range;
    this.chanceOnIdle = chanceOnIdle;
    this.chanceOnTarget = chanceOnTarget;
  }

  @Override
  public boolean canUse() {
    return (entity.getTarget() == null && entity.getRandom().nextFloat() < chanceOnIdle)
        || (entity.getTarget() != null && entity.distanceToSqr(entity.getTarget()) > 10.0F && entity.getRandom().nextFloat() < chanceOnTarget);
  }
  
  @Override
  public void tick() {
    if (entity.getTarget() != null) {
      entity.lookAt(entity.getTarget(), 100.0F, 100.0F);
      if (entity.distanceToSqr(entity.getTarget()) > 10.0F) {
        entity.teleportToEntity(entity, entity.getTarget(), range);
      }
    } else {
      // or just teleport randomly
      entity.teleportRandomly(entity, range);
    }
  }
  
  @Override
  public boolean canContinueToUse() {
    return false;
  }

}
