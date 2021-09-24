package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.IFuelConsumer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.LookAtGoal;

public class LookAtWhenActiveGoal<T extends MobEntity & IFuelConsumer> extends LookAtGoal {
  
  protected T entity;
  
  public LookAtWhenActiveGoal(T entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance) {
    super(entityIn, watchTargetClass, maxDistance);
    this.entity = entityIn;
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
