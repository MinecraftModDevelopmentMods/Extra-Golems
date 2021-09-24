package com.mcmoddev.golems.entity.goal;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;

import java.util.EnumSet;


public class BurnInSunGoal extends Goal {

  protected final MobEntity entity;
  protected final float chance;

  public BurnInSunGoal(MobEntity entityIn, final float chanceIn) {
    setMutexFlags(EnumSet.noneOf(Goal.Flag.class));
    this.entity = entityIn;
    this.chance = chanceIn;
  }

  @Override
  public boolean shouldExecute() {
    return entity.world.isDaytime() && !entity.isBurning()
        && entity.world.canSeeSky(entity.getPosition())
        && entity.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty();
  }

  @Override
  public boolean shouldContinueExecuting() {
    return false;
  }
  
  @Override
  public void startExecuting() {
    if(entity.world.getRandom().nextFloat() < chance) {
      entity.setFire(3);
    }
  }
}
