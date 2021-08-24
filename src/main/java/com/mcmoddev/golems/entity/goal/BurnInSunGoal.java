package com.mcmoddev.golems.entity.goal;

import java.util.EnumSet;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class BurnInSunGoal extends Goal {

  protected final Mob entity;
  protected final float chance;

  public BurnInSunGoal(Mob entityIn, final float chanceIn) {
    setFlags(EnumSet.noneOf(Goal.Flag.class));
    this.entity = entityIn;
    this.chance = chanceIn;
  }

  @Override
  public boolean canUse() {
    return entity.level.isDay() && !entity.isOnFire() 
        && entity.level.canSeeSky(entity.blockPosition())
        && entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty();
  }

  @Override
  public boolean canContinueToUse() {
    return false;
  }
  
  @Override
  public void start() {
    if(entity.getRandom().nextFloat() < chance) {
      entity.setSecondsOnFire(3);
    }
  }
}
