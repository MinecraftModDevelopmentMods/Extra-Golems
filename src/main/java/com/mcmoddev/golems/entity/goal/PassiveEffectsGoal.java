package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.container.behavior.parameter.BehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.List;

public class PassiveEffectsGoal extends Goal {

	protected final GolemBase golem;
	protected final MobEffectInstance[] effects;
	protected final boolean nightOnly;
	protected final BehaviorParameter.Target target;
	protected final float chance;
	protected final double range;

	public PassiveEffectsGoal(final GolemBase golemIn, final MobEffectInstance[] effectsIn,
							  final boolean nightOnlyIn, final BehaviorParameter.Target targetIn,
							  final float chanceIn, final double rangeIn) {
		this.golem = golemIn;
		this.effects = effectsIn;
		this.nightOnly = nightOnlyIn;
		this.target = targetIn;
		this.chance = chanceIn;
		if (rangeIn > 0) {
			this.range = rangeIn;
		} else {
			this.range = golemIn.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() * 0.5D;
		}
	}

	@Override
	public boolean canUse() {
		if (effects.length <= 0) {
			return false;
		}
		if (nightOnly && golem.level().isDay() && golem.level().dimensionType().hasSkyLight()) {
			return false;
		}
		if (target == BehaviorParameter.Target.ENEMY && null == golem.getTarget()) {
			return false;
		}
		return golem.getRandom().nextFloat() < chance;
	}

	@Override
	public boolean canContinueToUse() {
		return false;
	}

	@Override
	public void start() {
		// choose random effect to apply
		MobEffectInstance effect = new MobEffectInstance(effects[golem.getRandom().nextInt(effects.length)]);
		// apply effect to target(s)
		switch (target) {
			case AREA:
				double inflate = range > 0 ? range : 2.0D;
				TargetingConditions condition = TargetingConditions.forNonCombat()
						.ignoreLineOfSight().ignoreInvisibilityTesting();
				List<LivingEntity> targets = this.golem.level().getNearbyEntities(LivingEntity.class,
						condition, this.golem, this.golem.getBoundingBox().inflate(inflate));
				// apply to each entity in the list
				for (LivingEntity target : targets) {
					applyEffect(target, effect);
				}
				break;
			case SELF:
				applyEffect(this.golem, effect);
				break;
			case ENEMY:
				applyEffect(this.golem.getTarget(), effect);
				break;
		}
	}

	/**
	 * Applies a MobEffectInstance to the given entity
	 *
	 * @param target the entity
	 * @param effect the effect instance
	 */
	private void applyEffect(final LivingEntity target, final MobEffectInstance effect) {
		// only apply when target has no effects
		if (effect != null && target != null && target.getActiveEffects().isEmpty()) {
			target.addEffect(effect);
		}
	}
}
