package com.mcmoddev.golems.entity.goal;

import java.util.EnumSet;

import com.mcmoddev.golems.entity.IFuelConsumer;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;

public class UseFuelGoal<T extends MobEntity & IFuelConsumer> extends Goal {
  
  protected final T entity;
  protected final int interval;

  public UseFuelGoal(T entity, int interval) {
    this.setMutexFlags(EnumSet.noneOf(Goal.Flag.class));
    this.entity = entity;
    this.interval = interval;
  }

  @Override
  public boolean shouldExecute() {
    // only uses fuel every X ticks
    return entity.isServerWorld() && entity.getFuel() > 0
        && entity.ticksExisted % interval == 0;
  }

  @Override
  public boolean shouldContinueExecuting() {
    return false;
  }

  @Override
  public void startExecuting() {
    entity.addFuel(-1);
  }
}
