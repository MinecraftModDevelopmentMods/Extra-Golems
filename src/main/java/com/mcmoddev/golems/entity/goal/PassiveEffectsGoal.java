package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class PassiveEffectsGoal extends Goal {

	protected final GolemBase golem;
	protected final MobEffectInstance[] effects;
	protected final boolean nightOnly;
	protected final boolean self;
	protected final float chance;

	public PassiveEffectsGoal(final GolemBase golemIn, final MobEffectInstance[] effectsIn,
							  final boolean nightOnlyIn, final boolean selfIn, final float chanceIn) {
		this.golem = golemIn;
		this.effects = effectsIn;
		this.nightOnly = nightOnlyIn;
		this.self = selfIn;
		this.chance = chanceIn;
	}

	@Override
	public boolean canUse() {
		return effects.length > 0 && golem.getRandom().nextFloat() < chance
				&& ((self && golem.getActiveEffects().isEmpty())
				|| (!self && golem.getTarget() != null && golem.getTarget().getActiveEffects().isEmpty()))
				&& (!nightOnly || !golem.level.isDay() || !golem.level.dimensionType().hasSkyLight());
	}

	@Override
	public void start() {
		LivingEntity effectTarget = (self) ? golem : golem.getTarget();
		if (effectTarget != null) {
			// apply a randomly chosen mob effects (if not already present)
			MobEffectInstance effect = effects[golem.getRandom().nextInt(effects.length)];
			effectTarget.addEffect(effect);
		}
	}
}
