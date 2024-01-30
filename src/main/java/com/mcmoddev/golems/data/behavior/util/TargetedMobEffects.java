package com.mcmoddev.golems.data.behavior.util;

import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

@Immutable
public class TargetedMobEffects {

	public static final Codec<TargetedMobEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			TargetType.CODEC.fieldOf("target").forGetter(TargetedMobEffects::getTargetType),
			Codec.doubleRange(0.0D, 128.0D).optionalFieldOf("radius", 2.0D).forGetter(TargetedMobEffects::getRadius),
			IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("rolls", ConstantInt.of(0)).forGetter(TargetedMobEffects::getRolls),
			EGCodecUtils.listOrElementCodec(EGCodecUtils.MOB_EFFECT_INSTANCE_CODEC).fieldOf("effect").forGetter(TargetedMobEffects::getEffects)
	).apply(instance, TargetedMobEffects::new));

	/** The effect target **/
	private final TargetType targetType;
	/** The radius of the effect, only used when {@link #targetType} is {@link TargetType#AREA} **/
	private final double radius;
	/** The number of effects to apply, or zero to apply all effects **/
	private final IntProvider rolls;
	/** The effects to apply **/
	private final List<MobEffectInstance> effects;

	public TargetedMobEffects(TargetType targetType, double radius, IntProvider rolls, List<MobEffectInstance> effects) {
		this.targetType = targetType;
		this.radius = radius;
		this.rolls = rolls;
		this.effects = effects;
	}

	//// GETTERS ////

	public TargetType getTargetType() {
		return targetType;
	}

	public double getRadius() {
		return radius;
	}

	public IntProvider getRolls() {
		return rolls;
	}

	public List<MobEffectInstance> getEffects() {
		return effects;
	}

	/**
	 * Applies the effects based on the target
	 *
	 * @param entity  the entity
	 */
	public void apply(IExtraGolem entity) {
		if(effects.isEmpty()) {
			return;
		}
		final Mob mob = entity.asMob();
		final int rolls = this.rolls.sample(mob.getRandom());
		switch (targetType) {
			case AREA:
				TargetingConditions condition = TargetingConditions.forNonCombat()
						.ignoreLineOfSight().ignoreInvisibilityTesting();
				List<LivingEntity> targets = mob.level().getNearbyEntities(LivingEntity.class,
						condition, mob, mob.getBoundingBox().inflate(radius));
				// apply to each entity in list
				for (LivingEntity target : targets) {
					copyEffects(target, rolls, effects);
				}
				break;
			case SELF:
				copyEffects(mob, rolls, effects);
				break;
			case ENEMY:
				if(mob.getTarget() != null) {
					copyEffects(mob.getTarget(), rolls, effects);
				}
				break;
		}
	}

	/**
	 * Applies a {@link MobEffectInstance} to the given entity
	 *
	 * @param target the entity
	 * @param effects the effect instance list
	 */
	private void copyEffects(final LivingEntity target, final int rolls, final List<MobEffectInstance> effects) {
		// apply all effects when rolls are not positive or the list has a single entry
		if(rolls <= 0 || effects.size() == 1) {
			for(MobEffectInstance effect : effects) {
				target.addEffect(new MobEffectInstance(effect));
			}
			return;
		}
		// apply randomly selected effects
		for(int i = 0; i < rolls; i++) {
			target.addEffect(Util.getRandom(effects, target.getRandom()));
		}
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TargetedMobEffects)) return false;
		TargetedMobEffects that = (TargetedMobEffects) o;
		return Double.compare(that.radius, radius) == 0 && targetType == that.targetType
				&& rolls.getType() == that.rolls.getType() && rolls.getMinValue() == that.rolls.getMinValue() && rolls.getMaxValue() == this.rolls.getMaxValue()
				&& effects.equals(that.effects);
	}

	@Override
	public int hashCode() {
		return Objects.hash(targetType, radius, rolls.getType(), rolls.getMinValue(), rolls.getMaxValue(), effects);
	}
}
