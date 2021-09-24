package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.potion.EffectInstance;

public class PassiveEffectsGoal extends Goal {

  protected final GolemBase golem;
  protected final EffectInstance[] effects;
  protected final boolean nightOnly;
  protected final boolean self;
  protected final float chance;

  public PassiveEffectsGoal(final GolemBase golemIn, final EffectInstance[] effectsIn,
      final boolean nightOnlyIn, final boolean selfIn, final float chanceIn) {
    this.golem = golemIn;
    this.effects = effectsIn;
    this.nightOnly = nightOnlyIn;
    this.self = selfIn;
    this.chance = chanceIn;
  }

  @Override
  public boolean shouldExecute() {
    return effects.length > 0 && golem.world.getRandom().nextFloat() < chance
        && ((self && golem.getActivePotionEffects().isEmpty())
            || (!self && golem.getAttackTarget() != null && golem.getAttackTarget().getActivePotionEffects().isEmpty()))
        && (!nightOnly || !golem.world.isDaytime() || !golem.world.getDimensionType().hasSkyLight());
  }

  @Override
  public void startExecuting() {
    LivingEntity effectTarget = (self) ? golem : golem.getAttackTarget();
    if(effectTarget != null) {
      // apply a randomly chosen mob effects (if not already present)
      EffectInstance effect = effects[golem.world.getRandom().nextInt(effects.length)];
      effectTarget.addPotionEffect(effect);
    }
  }
}
