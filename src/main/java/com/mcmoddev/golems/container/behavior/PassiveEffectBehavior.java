package com.mcmoddev.golems.container.behavior;

import java.util.List;
import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.container.behavior.parameter.BehaviorParameter.Target;
import com.mcmoddev.golems.container.behavior.parameter.MobEffectBehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.PassiveEffectsGoal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

@Immutable
public class PassiveEffectBehavior extends GolemBehavior {
  
  protected final boolean nightOnly;
  protected final Optional<MobEffectBehaviorParameter> effect;

  public PassiveEffectBehavior(CompoundTag tag) {
    super(tag, GolemBehaviors.PASSIVE_EFFECT);
    nightOnly = tag.getBoolean("night_only");
    effect = tag.contains("effect") ? Optional.of(new MobEffectBehaviorParameter(tag.getCompound("effect"))) : Optional.empty();
  }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    effect.ifPresent(e -> entity.goalSelector.addGoal(4, new PassiveEffectsGoal(entity, e.getEffects(), nightOnly, e.getTarget() == Target.SELF, (float)e.getChance())));
  }
  
  @Override
  public void onAddDescriptions(List<Component> list) {
    effect.ifPresent(p -> {
      if(p.getTarget() == Target.SELF && !list.contains(EFFECTS_SELF_DESC)) {
        list.add(EFFECTS_SELF_DESC);
      } else if(p.getTarget() == Target.ENEMY && !list.contains(EFFECTS_ENEMY_DESC)) {
        list.add(EFFECTS_ENEMY_DESC);
      }
    });
  }
}
