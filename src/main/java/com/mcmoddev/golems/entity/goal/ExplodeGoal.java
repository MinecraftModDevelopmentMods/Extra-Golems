package com.mcmoddev.golems.entity.goal;

import java.util.EnumSet;

import com.mcmoddev.golems.entity.IRandomExploder;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class ExplodeGoal<T extends Mob & IRandomExploder> extends Goal {
  
  protected final T entity;
  /** Explosion radius **/
  protected final float range;

  public ExplodeGoal(T entity, float range) {
    this.setFlags(EnumSet.noneOf(Goal.Flag.class));
    this.entity = entity;
    this.range = range;
  }

  @Override
  public boolean canUse() {
    return entity.isFuseLit();
  }

  @Override
  public void start() {
    entity.resetFuse();
  }
  
  @Override
  public void tick() {
    entity.getNavigation().stop();
    if (entity.isInWaterRainOrBubble()) {
      stop();
    } else if(entity.getFuse() <= 0) {
      entity.explode(range);
    }
  }
  
  @Override
  public void stop() {
    entity.resetFuseLit();
  }
}
