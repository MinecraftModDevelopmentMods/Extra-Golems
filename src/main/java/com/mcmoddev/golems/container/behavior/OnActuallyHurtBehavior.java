package com.mcmoddev.golems.container.behavior;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.container.behavior.parameter.FireBehaviorParameter;
import com.mcmoddev.golems.container.behavior.parameter.MobEffectBehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@Immutable
public class OnActuallyHurtBehavior extends GolemBehavior {
    
  protected final Optional<FireBehaviorParameter> fire;
  protected final Optional<MobEffectBehaviorParameter> effect;

  public OnActuallyHurtBehavior(CompoundTag tag) {
    super(tag, GolemBehaviors.ON_ACTUALLY_HURT);
    fire = tag.contains("fire") ? Optional.of(new FireBehaviorParameter(tag.getCompound("fire"))) : Optional.empty();
    effect = tag.contains("effects") ? Optional.of(new MobEffectBehaviorParameter(tag.getCompound("effects"))) : Optional.empty();
  }
  
  @Override
  public void onActuallyHurt(final GolemBase entity, final DamageSource source, final float amount) {
    if(!entity.isBaby()) {
      fire.ifPresent(p -> p.apply(entity, source.getEntity()));
      if(source.getEntity() instanceof LivingEntity) {
        effect.ifPresent(p -> p.apply(entity, (LivingEntity)source.getEntity()));
      }
    }
  }
}
