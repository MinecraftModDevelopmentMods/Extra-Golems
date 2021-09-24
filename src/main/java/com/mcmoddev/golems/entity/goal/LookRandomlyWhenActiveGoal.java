package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.IFuelConsumer;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;

public class LookRandomlyWhenActiveGoal<T extends MobEntity & IFuelConsumer> extends LookRandomlyGoal {
  
  protected T entity;
  
  public LookRandomlyWhenActiveGoal(T entitylivingIn) {
    super(entitylivingIn);
    entity = entitylivingIn;
  }

  @Override
  public boolean shouldExecute() {
    return entity.hasFuel() && super.shouldExecute();
  }
  
  @Override
  public boolean shouldContinueExecuting() {
    return entity.hasFuel() && super.shouldContinueExecuting();
  }
}
