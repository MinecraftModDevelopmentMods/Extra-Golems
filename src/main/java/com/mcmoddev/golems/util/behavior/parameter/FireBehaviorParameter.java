package com.mcmoddev.golems.util.behavior.parameter;

import javax.annotation.concurrent.Immutable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Immutable
public class FireBehaviorParameter extends BehaviorParameter {

  private Target target = Target.SELF;
  private double chance = 0;
  private int time = 0;
  
  public FireBehaviorParameter(final CompoundTag tag) {
    super();
    target = Target.getByName(tag.getString("target"));
    chance = tag.getDouble("chance");
    time = tag.getInt("time");
  }
  
  public Target getTarget() { return target; }
  
  public double getChance() { return chance; }
  
  public int getTime() { return time; }
  
  public void apply(final LivingEntity self, final Entity other) {
    if(self.getRandom().nextFloat() < chance) {
      Entity fireTarget = (target == Target.SELF) ? self : other;
      if(fireTarget != null) {
        fireTarget.setSecondsOnFire(time);
      }
    }
  }
}
