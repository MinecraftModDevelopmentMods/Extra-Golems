package com.mcmoddev.golems.entity.goal;

import java.util.EnumSet;

import com.mcmoddev.golems.entity.IRandomTeleporter;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;

public class RandomTeleportGoal<T extends MobEntity & IRandomTeleporter> extends Goal {
  
  protected final T entity;
  protected final double range;
  protected final double chanceOnIdle;
  protected final double chanceOnTarget;

  public RandomTeleportGoal(T entity, double range, double chanceOnIdle, double chanceOnTarget) {
    super();
    this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
    this.entity = entity;
    this.range = range;
    this.chanceOnIdle = chanceOnIdle;
    this.chanceOnTarget = chanceOnTarget;
  }

  @Override
  public boolean shouldExecute() {
    return !entity.isChild() && ((entity.getAttackTarget() == null && entity.world.getRandom().nextFloat() < chanceOnIdle)
        || (entity.getAttackTarget() != null && entity.getDistanceSq(entity.getAttackTarget()) > 10.0F && entity.world.getRandom().nextFloat() < chanceOnTarget));
  }
  
  @Override
  public void tick() {
    if (entity.getAttackTarget() != null) {
      entity.getLookController().setLookPositionWithEntity(entity.getAttackTarget(), 100.0F, 100.0F);
      if (entity.getDistanceSq(entity.getAttackTarget()) > 10.0F) {
        entity.teleportToEntity(entity, entity.getAttackTarget(), range);
      }
    } else {
      // or just teleport randomly
      entity.teleportRandomly(entity, range);
    }
  }
  
  @Override
  public boolean shouldContinueExecuting() { return false; }

}
