package com.mcmoddev.golems.golem_stats.behavior.parameter;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

@Immutable
public class FireBehaviorParameter extends BehaviorParameter {

  private final Target target;
  private final double chance;
  private final int time;
  
  public FireBehaviorParameter(final CompoundNBT tag) {
    super();
    this.target = Target.getByName(tag.getString("target"));
    this.chance = tag.getDouble("chance");
    this.time = tag.getInt("time");
  }
  
  public Target getTarget() { return target; }
  
  public double getChance() { return chance; }
  
  public int getTime() { return time; }
  
  public void apply(final GolemBase self, final Entity other) {
    if(self.world.getRandom().nextFloat() < chance) {
      Entity fireTarget = (target == Target.SELF) ? self : other;
      if(fireTarget != null) {
        fireTarget.setFire(time);
      }
    }
  }
}
