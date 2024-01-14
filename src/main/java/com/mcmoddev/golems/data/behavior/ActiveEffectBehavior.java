package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.TargetedMobEffects;
import com.mcmoddev.golems.data.behavior.util.TriggerType;
import com.mcmoddev.golems.data.behavior.util.WorldPredicate;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.PassiveEffectsGoal;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.function.Predicate;

/**
 * This behavior allows an entity to passively apply a mob effect
 **/
@Immutable
public class ActiveEffectBehavior extends Behavior<GolemBase> {

	public static final Codec<ActiveEffectBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(TargetedMobEffects.CODEC.fieldOf("effect").forGetter(ActiveEffectBehavior::getTargetedMobEffects))
			.and(TriggerType.CODEC.fieldOf("trigger").forGetter(ActiveEffectBehavior::getTrigger))
			.and(EGCodecUtils.listOrElementCodec(WorldPredicate.CODEC).optionalFieldOf("predicate", ImmutableList.of(WorldPredicate.ALWAYS)).forGetter(ActiveEffectBehavior::getPredicates))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 1.0D).forGetter(ActiveEffectBehavior::getChance))
			.apply(instance, ActiveEffectBehavior::new));

	/** The mob effects to apply **/
	private final TargetedMobEffects targetedMobEffects;
	/** The trigger to apply the effects **/
	private final TriggerType trigger;
	/** The predicate to check before applying **/
	private final List<WorldPredicate> predicates;
	/** The conditions to summon the entity as a single predicate **/
	private final Predicate<GolemBase> predicate;
	/** The chance to apply **/
	private final double chance;

	public ActiveEffectBehavior(MinMaxBounds.Ints variant, TargetedMobEffects targetedMobEffects, TriggerType trigger, List<WorldPredicate> predicates, double chance) {
		super(variant);
		this.targetedMobEffects = targetedMobEffects;
		this.trigger = trigger;
		this.predicates = predicates;
		this.predicate = WorldPredicate.and(predicates);
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
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.ACTIVE_EFFECT.get();
	}

	//// METHODS ////

	@Override
	public void onActuallyHurt(GolemBase entity, DamageSource source, float amount) {
		if(this.trigger == TriggerType.HURT && entity.getRandom().nextDouble() < chance) {
			targetedMobEffects.apply(entity);
		}
	}

	@Override
	public void onHurtTarget(GolemBase entity, Entity target) {
		if(this.trigger == TriggerType.ATTACK && entity.getRandom().nextDouble() < chance) {
			targetedMobEffects.apply(entity);
		}
	}

	@Override
	public void onTick(GolemBase entity) {
		if(this.trigger == TriggerType.TICK && entity.getRandom().nextDouble() < chance) {
			targetedMobEffects.apply(entity);
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
		final String key = "entitytip.active_effect." + targetedMobEffects.getTargetType().getSerializedName();
		for(MobEffectInstance effect : targetedMobEffects.getEffects()) {
			builder.add(Component.translatable(key, effect.getEffect().getDisplayName(), predicateText));
		}
		return builder.build();
	}
}
