package com.mcmoddev.golems.container.behavior;

import java.util.List;
import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.container.behavior.parameter.BehaviorParameter.Target;
import com.mcmoddev.golems.container.behavior.parameter.FireBehaviorParameter;
import com.mcmoddev.golems.container.behavior.parameter.MobEffectBehaviorParameter;
import com.mcmoddev.golems.container.behavior.parameter.SummonEntityBehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Immutable
public class OnHurtTargetBehavior extends GolemBehavior {
    
  protected final Optional<FireBehaviorParameter> fire;
  protected final Optional<MobEffectBehaviorParameter> effect;
  protected final Optional<SummonEntityBehaviorParameter> summon;

  public OnHurtTargetBehavior(CompoundTag tag) {
    super(tag);
    fire = tag.contains("fire") ? Optional.of(new FireBehaviorParameter(tag.getCompound("fire"))) : Optional.empty();
    effect = tag.contains("effect") ? Optional.of(new MobEffectBehaviorParameter(tag.getCompound("effect"))) : Optional.empty();
    summon = tag.contains("summon") ? Optional.of(new SummonEntityBehaviorParameter(tag.getCompound("summon"))) : Optional.empty();
  }
  
  @Override
  public void onHurtTarget(final GolemBase entity, final Entity target) {
    if(!entity.isBaby()) {
      fire.ifPresent(p -> p.apply(entity, target));
      if(target instanceof LivingEntity) {
        effect.ifPresent(p -> p.apply(entity, (LivingEntity)target));
      }
      summon.ifPresent(p -> p.apply(entity, target));
    }
  }
  
  @Override
  public void onAddDescriptions(List<Component> list) {
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
