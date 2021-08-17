package com.mcmoddev.golems.entity.goal;

import java.util.EnumSet;

import com.mcmoddev.golems.entity.IFuelConsumer;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class UseFuelGoal<T extends Mob & IFuelConsumer> extends Goal {
  
  protected final T entity;
  protected final int interval;

  public UseFuelGoal(T entity, int interval) {
    this.setFlags(EnumSet.noneOf(Goal.Flag.class));
    this.entity = entity;
    this.interval = interval;
  }

  @Override
  public boolean canUse() {
    // only uses fuel every X ticks
    return entity.isEffectiveAi() && entity.getFuel() > 0 
        && entity.tickCount % interval == 0;
  }

  @Override
  public boolean canContinueToUse() {
    return false;
  }

  @Override
  public void start() {
    entity.addFuel(-1);
  }
}
