package com.mcmoddev.golems.container.behavior.parameter;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.common.util.Constants;

@Immutable
public class MobEffectBehaviorParameter extends BehaviorParameter {

  private final Target target;
  private final double chance;
  private final EffectInstance[] effects;
  
  public MobEffectBehaviorParameter(final CompoundNBT tag) {
    super();
    this.target = Target.getByName(tag.getString("target"));
    this.chance = tag.getDouble("chance");
    this.effects = readEffectArray(tag.getList("effects", Constants.NBT.TAG_COMPOUND));
  }
  
  public Target getTarget() { return target; }
  
  public double getChance() { return chance; }
  
  public EffectInstance[] getEffects() { return effects; }
  
  public void apply(GolemBase self, LivingEntity other) {
    if(effects.length > 0 && self.world.getRandom().nextFloat() < chance) {
      LivingEntity effectTarget = (target == Target.SELF) ? self : other;
      if(effectTarget != null) {
        // apply a randomly chosen mob effects
        effectTarget.addPotionEffect(effects[self.world.getRandom().nextInt(effects.length)]);
      }
    }
  }
}
