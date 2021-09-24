package com.mcmoddev.golems.entity.goal;

import java.util.EnumSet;

import com.mcmoddev.golems.entity.IRandomExploder;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.SoundEvents;

public class ExplodeGoal<T extends MobEntity & IRandomExploder> extends Goal {
  
  protected final T entity;
  /** Explosion radius **/
  protected final float range;

  public ExplodeGoal(T entity, float range) {
    this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    this.entity = entity;
    this.range = range;
  }

  @Override
  public boolean shouldExecute() {
    return entity.isFuseLit();
  }

  @Override
  public void startExecuting() {
    entity.resetFuse();
  }
  
  @Override
  public void tick() {
    entity.setFuse(entity.getFuse() - 1);
    entity.getNavigator().clearPath();
    if (entity.isInWaterRainOrBubbleColumn()) {
      // reset fuse and play sound when wet
      resetTask();
      entity.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.9F, entity.world.getRandom().nextFloat());
    } else if(entity.getFuse() <= 0) {
      entity.explode(range);
    }
  }
  
  @Override
  public void resetTask() {
    entity.resetFuseLit();
  }
}
