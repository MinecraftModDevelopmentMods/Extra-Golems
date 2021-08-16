package com.mcmoddev.golems.util.behavior.parameter;

import javax.annotation.concurrent.Immutable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

@Immutable
public class MobEffectBehaviorParameter extends BehaviorParameter {

  private Target target = Target.SELF;
  private double chance = 0;
  private MobEffectInstance[] effects = new MobEffectInstance[] {};
  
  public MobEffectBehaviorParameter(final CompoundTag tag) {
    super();
    target = Target.getByName(tag.getString("target"));
    chance = tag.getDouble("chance");
    effects = readEffectArray(tag.getList("effects", Tag.TAG_COMPOUND));
  }
  
  public Target getTarget() { return target; }
  
  public double getChance() { return chance; }
  
  public MobEffectInstance[] getEffects() { return effects; }
  
  public void apply(LivingEntity self, LivingEntity other) {
    if(effects.length > 0 && self.getRandom().nextFloat() < chance) {
      LivingEntity effectTarget = (target == Target.SELF) ? self : other;
      if(effectTarget != null) {
        // apply a randomly chosen mob effects
        effectTarget.addEffect(effects[self.getRandom().nextInt(effects.length)]);
      }
    }
  }
}
