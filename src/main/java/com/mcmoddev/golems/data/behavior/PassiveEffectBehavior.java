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
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This behavior allows an entity to passively apply a mob effect
 **/
@Immutable
public class PassiveEffectBehavior extends Behavior<GolemBase> {

	public static final Codec<PassiveEffectBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(TargetedMobEffects.CODEC.fieldOf("effect").forGetter(PassiveEffectBehavior::getTargetedMobEffects))
			.and(EGCodecUtils.listOrElementCodec(WorldPredicate.CODEC).optionalFieldOf("predicate", ImmutableList.of(WorldPredicate.ALWAYS)).forGetter(PassiveEffectBehavior::getPredicates))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 1.0D).forGetter(PassiveEffectBehavior::getChance))
			.apply(instance, PassiveEffectBehavior::new));

	/** The mob effects to apply **/
	private final TargetedMobEffects targetedMobEffects;
	/** The predicate to check before applying **/
	private final List<WorldPredicate> predicates;
	/** The conditions to summon the entity as a single predicate **/
	private final Predicate<GolemBase> predicate;
	/** The chance to apply **/
	private final double chance;

	public PassiveEffectBehavior(MinMaxBounds.Ints variant, TargetedMobEffects targetedMobEffects, List<WorldPredicate> predicates, double chance) {
		super(variant);
		this.targetedMobEffects = targetedMobEffects;
		this.predicates = predicates;
		this.predicate = WorldPredicate.and(predicates);
		this.chance = chance;
	}

	//// GETTERS ////

	public TargetedMobEffects getTargetedMobEffects() {
		return targetedMobEffects;
	}

	public List<WorldPredicate> getPredicates() {
		return predicates;
	}

	public double getChance() {
		return chance;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.PASSIVE_EFFECT.get();
	}

	//// METHODS ////

	@Override
	public void onTick(GolemBase entity) {
		if(!predicates.isEmpty() && predicate.test(entity) && entity.getRandom().nextDouble() < chance) {
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
		final String key = "entitytip.passive_effect." + targetedMobEffects.getTargetType().getSerializedName();
		for(MobEffectInstance effect : targetedMobEffects.getEffects()) {
			builder.add(Component.translatable(key, effect.getEffect().getDisplayName(), predicateText));
		}
		return builder.build();
	}
}
