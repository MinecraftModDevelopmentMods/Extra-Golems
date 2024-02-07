package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.TargetedMobEffects;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.data.behavior.util.TriggerType;
import com.mcmoddev.golems.data.behavior.util.GolemPredicate;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.PredicateUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This behavior allows an entity to passively apply a mob effect
 **/
@Immutable
public class EffectBehavior extends Behavior {

	public static final Codec<EffectBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(TargetedMobEffects.CODEC.fieldOf("apply").forGetter(EffectBehavior::getTargetedMobEffects))
			.and(TriggerType.CODEC.optionalFieldOf("trigger", TriggerType.TICK).forGetter(EffectBehavior::getTrigger))
			.and(EGCodecUtils.listOrElementCodec(GolemPredicate.CODEC).optionalFieldOf("predicate", ImmutableList.of(GolemPredicate.ALWAYS)).forGetter(EffectBehavior::getPredicates))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 1.0D).forGetter(EffectBehavior::getChance))
			.apply(instance, EffectBehavior::new));

	/** The mob effects to apply **/
	private final TargetedMobEffects targetedMobEffects;
	/** The trigger to apply the effects **/
	private final TriggerType trigger;
	/** The predicate to check before applying **/
	private final List<GolemPredicate> predicates;
	/** The conditions to summon the entity as a single predicate **/
	private final Predicate<IExtraGolem> predicate;
	/** The chance to apply **/
	private final double chance;

	public EffectBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, TargetedMobEffects targetedMobEffects, TriggerType trigger, List<GolemPredicate> predicates, double chance) {
		super(variant, tooltipPredicate);
		this.targetedMobEffects = targetedMobEffects;
		this.trigger = trigger;
		this.predicates = predicates;
		this.predicate = PredicateUtils.and(predicates);
		this.chance = chance;
	}

	//// GETTERS ////

	public TargetedMobEffects getTargetedMobEffects() {
		return targetedMobEffects;
	}

	public TriggerType getTrigger() {
		return trigger;
	}

	public List<GolemPredicate> getPredicates() {
		return predicates;
	}

	public double getChance() {
		return chance;
	}

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.EFFECT.get();
	}

	//// METHODS ////

	@Override
	public void onActuallyHurt(IExtraGolem entity, DamageSource source, float amount) {
		if(this.trigger == TriggerType.HURT && this.predicate.test(entity) && entity.asMob().getRandom().nextDouble() < chance) {
			targetedMobEffects.apply(entity.asMob());
		}
	}

	@Override
	public void onAttack(IExtraGolem entity, Entity target) {
		if(this.trigger == TriggerType.ATTACK && this.predicate.test(entity) && entity.asMob().getRandom().nextDouble() < chance) {
			targetedMobEffects.apply(entity.asMob());
		}
	}

	@Override
	public void onTick(IExtraGolem entity) {
		if(this.trigger == TriggerType.TICK && this.predicate.test(entity) && entity.asMob().getRandom().nextDouble() < chance) {
			targetedMobEffects.apply(entity.asMob());
		}
	}

	@Override
	public List<Component> createDescriptions(RegistryAccess registryAccess) {
		// create predicate text, if any
		final Optional<Component> predicateText = createTriggerAndPredicateDescription(trigger, predicates);

		// determine whether to display one effect or multiple
		final boolean multipleEffects = targetedMobEffects.getEffects().size() != 1;

		// convert potion effect(s) to a single component
		Component effectText = Component.empty();
		if(!multipleEffects) {
			effectText = Component.translatable(targetedMobEffects.getEffects().get(0).getDescriptionId()).withStyle(ChatFormatting.DARK_PURPLE);
		}

		// convert target type to a single component
		Component targetText = Component.translatable(targetedMobEffects.getTargetType().getDescriptionId());

		// create tooltip
		if(multipleEffects && predicateText.isPresent()) {
			return ImmutableList.of(Component.translatable(PREFIX + "effect.multiple.predicate", targetText, predicateText.get()));
		}
		if(!multipleEffects && predicateText.isPresent()) {
			return ImmutableList.of(Component.translatable(PREFIX + "effect.single.predicate", effectText, targetText, predicateText.get()));
		}
		if(multipleEffects) {
			return ImmutableList.of(Component.translatable(PREFIX + "effect.multiple", targetText));
		}
		return ImmutableList.of(Component.translatable(PREFIX + "effect.single", effectText, targetText));
	}

	//// EQUALITY ////


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EffectBehavior)) return false;
		EffectBehavior other = (EffectBehavior) o;
		return Double.compare(other.chance, chance) == 0 && targetedMobEffects.equals(other.targetedMobEffects) && trigger == other.trigger && predicates.equals(other.predicates);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), targetedMobEffects, trigger, predicates, chance);
	}
}
