package com.mcmoddev.golems.data.behavior.util;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import javax.annotation.concurrent.Immutable;
import java.util.List;

@Immutable
public class TargetedMobEffects {

	public static final Codec<TargetedMobEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			TargetType.CODEC.fieldOf("target").forGetter(TargetedMobEffects::getTargetType),
			Codec.doubleRange(0.0D, 128.0D).optionalFieldOf("radius", 2.0D).forGetter(TargetedMobEffects::getRadius),
			EGCodecUtils.listOrElementCodec(EGCodecUtils.MOB_EFFECT_INSTANCE_CODEC).fieldOf("effect").forGetter(TargetedMobEffects::getEffects)
	).apply(instance, TargetedMobEffects::new));

	/** The effect target **/
	private final TargetType targetType;
	/** The radius of the effect, only used when {@link #targetType} is {@link TargetType#AREA} **/
	private final double radius;
	/** The effects to apply **/
	private final List<MobEffectInstance> effects;

	public TargetedMobEffects(TargetType targetType, double radius, List<MobEffectInstance> effects) {
		this.targetType = targetType;
		this.radius = radius;
		this.effects = effects;
	}

	//// GETTERS ////

	public TargetType getTargetType() {
		return targetType;
	}

	public double getRadius() {
		return radius;
	}

	public List<MobEffectInstance> getEffects() {
		return effects;
	}

	/**
	 * Applies the effects based on the target
	 *
	 * @param self  the entity
	 */
	public void apply(GolemBase self) {
		if(effects.isEmpty()) {
			return;
		}
		switch (targetType) {
			case AREA:
				TargetingConditions condition = TargetingConditions.forNonCombat()
						.ignoreLineOfSight().ignoreInvisibilityTesting();
				List<LivingEntity> targets = self.level().getNearbyEntities(LivingEntity.class,
						condition, self, self.getBoundingBox().inflate(radius));
				// apply to each entity in list
				for (LivingEntity target : targets) {
					copyEffects(target, effects);
				}
				break;
			case SELF:
				copyEffects(self, effects);
				break;
			case ENEMY:
				if(self.getTarget() != null) {
					copyEffects(self.getTarget(), effects);
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
	private void copyEffects(final LivingEntity target, final List<MobEffectInstance> effects) {
		for(MobEffectInstance effect : effects) {
			target.addEffect(new MobEffectInstance(effect));
		}
	}

}
