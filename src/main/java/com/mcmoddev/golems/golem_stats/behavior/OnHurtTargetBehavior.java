package com.mcmoddev.golems.golem_stats.behavior;

import java.util.List;
import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.golem_stats.behavior.parameter.BehaviorParameter.Target;
import com.mcmoddev.golems.golem_stats.behavior.parameter.FireBehaviorParameter;
import com.mcmoddev.golems.golem_stats.behavior.parameter.MobEffectBehaviorParameter;
import com.mcmoddev.golems.golem_stats.behavior.parameter.SummonEntityBehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

/**
 * This behavior allows an entity to react when it hurts another entity.
 * The entity may apply a fire parameter, mob effect parameter, 
 * or summon entity parameter
 **/
@Immutable
public class OnHurtTargetBehavior extends GolemBehavior {
    
  /** An optional containing the fire parameter, if present **/
  protected final Optional<FireBehaviorParameter> fire;
  /** An optional containing the mob effect parameter, if present **/
  protected final Optional<MobEffectBehaviorParameter> effect;
  /** An optional containing the summon entity parameter, if present **/
  protected final Optional<SummonEntityBehaviorParameter> summon;

  public OnHurtTargetBehavior(CompoundNBT tag) {
    super(tag);
    fire = tag.contains("fire") ? Optional.of(new FireBehaviorParameter(tag.getCompound("fire"))) : Optional.empty();
    effect = tag.contains("effect") ? Optional.of(new MobEffectBehaviorParameter(tag.getCompound("effect"))) : Optional.empty();
    summon = tag.contains("summon") ? Optional.of(new SummonEntityBehaviorParameter(tag.getCompound("summon"))) : Optional.empty();
  }
  
  @Override
  public void onHurtTarget(final GolemBase entity, final Entity target) {
    if(!entity.isChild()) {
      fire.ifPresent(p -> p.apply(entity, target));
      if(target instanceof LivingEntity) {
        effect.ifPresent(p -> p.apply(entity, (LivingEntity)target));
      }
      summon.ifPresent(p -> p.apply(entity, target));
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
