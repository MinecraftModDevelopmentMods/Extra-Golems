package com.mcmoddev.golems.util.behavior;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.behavior.parameter.FireBehaviorParameter;
import com.mcmoddev.golems.util.behavior.parameter.MobEffectBehaviorParameter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Immutable
public class OnHurtTargetBehavior extends GolemBehavior {
    
  protected final Optional<FireBehaviorParameter> fire;
  protected final Optional<MobEffectBehaviorParameter> effect;

  public OnHurtTargetBehavior(CompoundTag tag) {
    super(tag, GolemBehaviors.ON_HURT_TARGET);
    fire = tag.contains("fire") ? Optional.of(new FireBehaviorParameter(tag.getCompound("fire"))) : Optional.empty();
    effect = tag.contains("effects") ? Optional.of(new MobEffectBehaviorParameter(tag.getCompound("effects"))) : Optional.empty();
  }
  
  @Override
  public void onHurtTarget(final GolemBase entity, final Entity target) {
    fire.ifPresent(p -> p.apply(entity, target));
    if(target instanceof LivingEntity) {
      effect.ifPresent(p -> p.apply(entity, (LivingEntity)target));
    }
  }
}
