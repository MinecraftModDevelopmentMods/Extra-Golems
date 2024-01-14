package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.BurnInSunGoal;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * This behavior allows an entity to burn in sunlight
 * and seek shelter from the sun during the day
 **/
@Immutable
public class BurnInSunBehavior extends Behavior<GolemBase> {

	public static final Codec<BurnInSunBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 0.25D).forGetter(BurnInSunBehavior::getChance))
			.apply(instance, BurnInSunBehavior::new));

	/** The percent chance [0,1] to apply each tick **/
	private final double chance;

	public BurnInSunBehavior(MinMaxBounds.Ints variant, double chance) {
		super(variant);
		this.chance = chance;
	}

	//// GETTERS ////

	public double getChance() {
		return chance;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.BURN_IN_SUN.get();
	}

	//// METHODS ////

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		// TODO adjust goals to use variant
		entity.goalSelector.addGoal(1, new RestrictSunGoal(entity));
		entity.goalSelector.addGoal(1, new BurnInSunGoal(entity, (float) chance));
		entity.goalSelector.addGoal(2, new FleeSunGoal(entity, 1.1D));
	}

	@Override
	public List<Component> createDescriptions() {
		return ImmutableList.of(Component.translatable("entitytip.burn_in_sun").withStyle(ChatFormatting.DARK_RED));
	}
}
