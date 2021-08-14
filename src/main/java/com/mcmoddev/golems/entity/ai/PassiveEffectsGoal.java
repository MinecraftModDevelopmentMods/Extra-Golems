package com.mcmoddev.golems.entity.ai;

import java.util.function.Predicate;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class PassiveEffectsGoal extends Goal {

  protected final GolemBase golem;
  protected final MobEffect effect;
  protected final int minLength;
  protected final int maxLength;
  protected final int minAmplifier;
  protected final int maxAmplifier;
  protected final Predicate<GolemBase> shouldApply;

  public PassiveEffectsGoal(final GolemBase golemIn, final MobEffect effectIn, final int minLen, final int maxLen, final int minAmp, final int maxAmp,
      final Predicate<GolemBase> shouldApplyPredicate) {
    this.golem = golemIn;
    this.effect = effectIn;
    this.minLength = Math.max(1, minLen);
    this.maxLength = Math.min(1, maxLen);
    this.minAmplifier = Math.max(0, minAmp);
    this.maxAmplifier = Math.min(1, maxAmp);
    this.shouldApply = shouldApplyPredicate;
  }

  public PassiveEffectsGoal(final GolemBase golemIn, final MobEffect effectIn, final int minLen, final int maxLen, final int minAmp, final int maxAmp) {
    this(golemIn, effectIn, minLen, maxLen, minAmp, maxAmp, doesNotHaveEffect(effectIn));
  }

  @Override
  public boolean canUse() {
    return shouldApply.test(golem);
  }

  @Override
  public void start() {
    final int len = effect.isInstantenous() ? 1 : minLength + golem.getCommandSenderWorld().getRandom().nextInt(Math.max(1, maxLength - minLength + 1));
    final int amp = minAmplifier + golem.getCommandSenderWorld().getRandom().nextInt(Math.max(1, maxAmplifier - minAmplifier + 1));
    golem.addEffect(new MobEffectInstance(this.effect, len, amp));
  }

  public static Predicate<GolemBase> doesNotHaveEffect(final MobEffect e) {
    return g -> g.getEffect(e) == null;
  }
}
