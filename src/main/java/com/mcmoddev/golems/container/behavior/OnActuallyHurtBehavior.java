package com.mcmoddev.golems.container.behavior;

import java.util.List;
import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.container.behavior.parameter.BehaviorParameter.Target;
import com.mcmoddev.golems.container.behavior.parameter.FireBehaviorParameter;
import com.mcmoddev.golems.container.behavior.parameter.MobEffectBehaviorParameter;
import com.mcmoddev.golems.container.behavior.parameter.SummonEntityBehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;

/**
 * This behavior allows an entity to react when actually hurt.
 * The entity may apply a fire parameter, mob effect parameter, 
 * or summon entity parameter
 **/
@Immutable
public class OnActuallyHurtBehavior extends GolemBehavior {
  
  /** An optional containing the fire parameter, if present **/
  protected final Optional<FireBehaviorParameter> fire;
  /** An optional containing the mob effect parameter, if present **/
  protected final Optional<MobEffectBehaviorParameter> effect;
  /** An optional containing the summon entity parameter, if present **/
  protected final Optional<SummonEntityBehaviorParameter> summon;

  public OnActuallyHurtBehavior(CompoundNBT tag) {
    super(tag);
    fire = tag.contains("fire") ? Optional.of(new FireBehaviorParameter(tag.getCompound("fire"))) : Optional.empty();
    effect = tag.contains("effect") ? Optional.of(new MobEffectBehaviorParameter(tag.getCompound("effect"))) : Optional.empty();
    summon = tag.contains("summon") ? Optional.of(new SummonEntityBehaviorParameter(tag.getCompound("summon"))) : Optional.empty();
  }
  
  @Override
  public void onActuallyHurt(final GolemBase entity, final DamageSource source, final float amount) {
    if(!entity.isChild()) {
      fire.ifPresent(p -> p.apply(entity, source.getImmediateSource()));
      if(source.getImmediateSource() instanceof LivingEntity) {
        effect.ifPresent(p -> p.apply(entity, (LivingEntity)source.getImmediateSource()));
      }
      summon.ifPresent(p -> p.apply(entity, source.getImmediateSource()));
    }
  }
  
  @Override
  public void onAddDescriptions(List<ITextComponent> list) {
    fire.ifPresent(p -> {
      if(p.getTarget() == Target.ENEMY && !list.contains(FIRE_DESC)) {
        list.add(FIRE_DESC);
      }
    });
    effect.ifPresent(p -> {
      if(p.getTarget() == Target.SELF && !list.contains(EFFECTS_SELF_DESC)) {
        list.add(EFFECTS_SELF_DESC);
      } else if(p.getTarget() == Target.ENEMY && !list.contains(EFFECTS_ENEMY_DESC)) {
        list.add(EFFECTS_ENEMY_DESC);
      }
    });
    summon.ifPresent(p -> {
      if(!list.contains(p.getDescription())) {
        list.add(p.getDescription());
      }
    });
  }
}
