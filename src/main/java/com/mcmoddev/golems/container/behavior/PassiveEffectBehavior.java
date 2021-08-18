package com.mcmoddev.golems.container.behavior;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.container.behavior.parameter.MobEffectBehaviorParameter;
import com.mcmoddev.golems.container.behavior.parameter.BehaviorParameter.Target;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.PassiveEffectsGoal;

import net.minecraft.nbt.CompoundTag;

@Immutable
public class PassiveEffectBehavior extends GolemBehavior {
  
  protected final boolean nightOnly;
  protected final Optional<MobEffectBehaviorParameter> effects;

  public PassiveEffectBehavior(CompoundTag tag) {
    super(tag, GolemBehaviors.PASSIVE_EFFECT);
    nightOnly = tag.getBoolean("night_only");
    effects = tag.contains("effects") ? Optional.of(new MobEffectBehaviorParameter(tag.getCompound("effects"))) : Optional.empty();
  }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    effects.ifPresent(e -> entity.goalSelector.addGoal(4, new PassiveEffectsGoal(entity, e.getEffects(), nightOnly, e.getTarget() == Target.SELF, (float)e.getChance())));
  }
}
