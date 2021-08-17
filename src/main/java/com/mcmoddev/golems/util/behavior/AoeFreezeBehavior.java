package com.mcmoddev.golems.util.behavior;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.AoeBlocksGoal;

import net.minecraft.nbt.CompoundTag;

@Immutable
public class AoeFreezeBehavior extends GolemBehavior {
  
  protected final int range;
  protected final int interval;
  protected final boolean sphere;
  protected final boolean frosted;
  
  public AoeFreezeBehavior(CompoundTag tag) {
    super(tag, GolemBehaviors.AOE_FREEZE);
    range = tag.getInt("range");
    interval = tag.getInt("interval");
    sphere = tag.getBoolean("sphere");
    frosted = tag.getBoolean("frosted");
  }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    entity.goalSelector.addGoal(1, new AoeBlocksGoal(entity, range, interval, sphere, 
        new AoeBlocksGoal.FreezeFunction(entity.getRandom(), frosted)));
  }
}
