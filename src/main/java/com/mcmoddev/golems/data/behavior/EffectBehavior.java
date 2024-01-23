package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.TargetedMobEffects;
import com.mcmoddev.golems.data.behavior.util.TriggerType;
import com.mcmoddev.golems.data.behavior.util.WorldPredicate;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.PredicateUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * This behavior allows an entity to passively apply a mob effect
 **/
@Immutable
public class EffectBehavior extends Behavior {

	public static final Codec<EffectBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(TargetedMobEffects.CODEC.fieldOf("effect").forGetter(EffectBehavior::getTargetedMobEffects))
			.and(TriggerType.CODEC.optionalFieldOf("trigger", TriggerType.TICK).forGetter(EffectBehavior::getTrigger))
			.and(EGCodecUtils.listOrElementCodec(WorldPredicate.CODEC).optionalFieldOf("predicate", ImmutableList.of(WorldPredicate.ALWAYS)).forGetter(EffectBehavior::getPredicates))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 1.0D).forGetter(EffectBehavior::getChance))
			.apply(instance, EffectBehavior::new));

	/** The mob effects to apply **/
	private final TargetedMobEffects targetedMobEffects;
	/** The trigger to apply the effects **/
	private final TriggerType trigger;
	/** The predicate to check before applying **/
	private final List<WorldPredicate> predicates;
	/** The conditions to summon the entity as a single predicate **/
	private final Predicate<IExtraGolem> predicate;
	/** The chance to apply **/
	private final double chance;

	public EffectBehavior(MinMaxBounds.Ints variant, TargetedMobEffects targetedMobEffects, TriggerType trigger, List<WorldPredicate> predicates, double chance) {
		super(variant);
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

	public List<WorldPredicate> getPredicates() {
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
	public List<Component> createDescriptions() {
		final ImmutableList.Builder<Component> builder = ImmutableList.builder();
		// verify predicates
		if(getPredicates().isEmpty()) {
			return builder.build();
		}
		// create predicate description
		// TODO add predicates to lang file
		final Component predicateText = Component.translatable("entitytip.when_x", Component.translatable(predicates.get(0).getDescriptionId()));
		for(int i = 1, n = predicates.size(); i < n; i++) {
			predicateText.getSiblings().add(Component.translatable("entitytip.and_x", Component.translatable(predicates.get(i).getDescriptionId())));
		}
		// TODO add this tooltip to lang file
		final String key = "entitytip.effect." + targetedMobEffects.getTargetType().getSerializedName();
		for(MobEffectInstance effect : targetedMobEffects.getEffects()) {
			builder.add(Component.translatable(key, effect.getEffect().getDisplayName(), predicateText));
		}
		return builder.build();
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
