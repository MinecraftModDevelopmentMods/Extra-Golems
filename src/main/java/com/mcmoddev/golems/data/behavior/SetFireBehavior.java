package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.TargetType;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.data.behavior.util.TriggerType;
import com.mcmoddev.golems.data.behavior.util.WorldPredicate;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.PredicateUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.Component;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * This behavior allows an entity to set entities on fire under specific conditions
 **/
@Immutable
public class SetFireBehavior extends Behavior {

	public static final Codec<SetFireBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(IntProvider.NON_NEGATIVE_CODEC.optionalFieldOf("seconds", ConstantInt.of(3)).forGetter(SetFireBehavior::getSeconds))
			.and(TargetType.CODEC.fieldOf("target").forGetter(SetFireBehavior::getTarget))
			.and(TriggerType.CODEC.fieldOf("trigger").forGetter(SetFireBehavior::getTrigger))
			.and(EGCodecUtils.listOrElementCodec(WorldPredicate.CODEC).optionalFieldOf("predicate", ImmutableList.of(WorldPredicate.ALWAYS)).forGetter(SetFireBehavior::getPredicates))
			.and(Codec.doubleRange(0.0D, 255.0D).optionalFieldOf("radius", 2.0D).forGetter(SetFireBehavior::getRadius))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 1.0D).forGetter(SetFireBehavior::getChance))
			.apply(instance, SetFireBehavior::new));

	/** The number of seconds to set fire **/
	private final IntProvider seconds;
	/** The position to set fire **/
	private final TargetType target;
	/** The trigger to set fire **/
	private final TriggerType trigger;
	/** The conditions to set fire **/
	private final List<WorldPredicate> predicates;
	/** The conditions to set fire as a single predicate **/
	private final Predicate<IExtraGolem> predicate;
	/** The radius to set fire, only used when {@link #target} is {@link TargetType#AREA} **/
	private final double radius;
	/** The percent chance [0,1] to apply **/
	private final double chance;

	public SetFireBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, IntProvider seconds, TargetType target, TriggerType trigger, List<WorldPredicate> predicates, double radius, double chance) {
		super(variant, tooltipPredicate);
		this.seconds = seconds;
		this.target = target;
		this.trigger = trigger;
		this.predicates = predicates;
		this.predicate = PredicateUtils.and(predicates);
		this.radius = radius;
		this.chance = chance;
	}

	//// GETTERS ////

	public IntProvider getSeconds() {
		return seconds;
	}

	public TargetType getTarget() {
		return target;
	}

	public TriggerType getTrigger() {
		return trigger;
	}

	public List<WorldPredicate> getPredicates() {
		return predicates;
	}

	public double getRadius() {
		return radius;
	}

	public double getChance() {
		return chance;
	}

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.SET_FIRE.get();
	}

	//// METHODS ////

	@Override
	public void onActuallyHurt(IExtraGolem entity, DamageSource source, float amount) {
		if(this.trigger == TriggerType.HURT) {
			setFire(entity.asMob());
		}
	}

	@Override
	public void onAttack(IExtraGolem entity, Entity target) {
		if(this.trigger == TriggerType.ATTACK) {
			setFire(entity.asMob());
		}
	}

	@Override
	public void onTick(IExtraGolem entity) {
		if(this.trigger == TriggerType.TICK) {
			setFire(entity.asMob());
		}
	}

	@Override
	public List<Component> createDescriptions() {
		final Component description = Component.empty(); // TODO description
		return ImmutableList.of(description);
	}

	protected boolean setFire(final GolemBase self) {
		if(!this.predicate.test(self)) {
			return false;
		}
		if(!(self.getRandom().nextDouble() < chance)) {
			return false;
		}
		switch (target) {
			case AREA:
				TargetingConditions condition = TargetingConditions.forNonCombat()
						.ignoreLineOfSight().ignoreInvisibilityTesting();
				List<LivingEntity> targets = self.level().getNearbyEntities(LivingEntity.class,
						condition, self, self.getBoundingBox().inflate(radius));
				// apply to each entity in list
				for (LivingEntity entity : targets) {
					entity.setSecondsOnFire(seconds.sample(self.getRandom()));
				}
				return !targets.isEmpty();
			case SELF:
				self.setSecondsOnFire(seconds.sample(self.getRandom()));
				return true;
			case ENEMY:
				LivingEntity target = self.getTarget();
				if(target != null) {
					target.setSecondsOnFire(seconds.sample(self.getRandom()));
				}
				return target != null;

		}
		return false;
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SetFireBehavior)) return false;
		if (!super.equals(o)) return false;
		SetFireBehavior that = (SetFireBehavior) o;
		return Double.compare(that.radius, radius) == 0 && Double.compare(that.chance, chance) == 0 && seconds.equals(that.seconds) && target == that.target && trigger == that.trigger && predicates.equals(that.predicates);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), seconds, target, trigger, predicates, radius, chance);
	}
}
