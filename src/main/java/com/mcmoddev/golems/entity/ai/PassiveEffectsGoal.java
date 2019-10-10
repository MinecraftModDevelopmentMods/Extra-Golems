package com.mcmoddev.golems.entity.ai;

import java.util.function.Predicate;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

public class PassiveEffectsGoal extends Goal {
	
	protected final GolemBase golem;
	protected final Effect effect;
	protected final int minLength;
	protected final int maxLength;
	protected final int minAmplifier;
	protected final int maxAmplifier;
	protected final Predicate<GolemBase> shouldApply;
	
	public PassiveEffectsGoal(final GolemBase golemIn, final Effect effectIn, final int minLen, final int maxLen,
			final int minAmp, final int maxAmp,	final Predicate<GolemBase> shouldApplyPredicate) {
		this.golem = golemIn;
		this.effect = effectIn;
		this.minLength = Math.max(1, minLen);
		this.maxLength = Math.min(1, maxLen);
		this.minAmplifier = Math.max(0, minAmp);
		this.maxAmplifier = Math.min(1, maxAmp);
		this.shouldApply = shouldApplyPredicate;
	}
	
	public PassiveEffectsGoal(final GolemBase golemIn, final Effect effectIn, final int minLen, final int maxLen,
			final int minAmp, final int maxAmp) {
		this(golemIn, effectIn, minLen, maxLen, minAmp, maxAmp, doesNotHaveEffect(effectIn));
	}
	
	@Override
	public boolean shouldExecute() {
		return shouldApply.test(golem);
	}
		
	@Override
	public void startExecuting() {
		final int len = effect.isInstant() ? 1 : 
			minLength + golem.getEntityWorld().getRandom().nextInt(Math.max(1, maxLength - minLength + 1));
		final int amp = minAmplifier + golem.getEntityWorld().getRandom()
				.nextInt(Math.max(1, maxAmplifier - minAmplifier + 1));
		golem.addPotionEffect(new EffectInstance(this.effect, len, amp));
	}
	
	public static Predicate<GolemBase> doesNotHaveEffect(final Effect e) {
		return g -> g.getActivePotionEffect(e) == null;
	}
}
