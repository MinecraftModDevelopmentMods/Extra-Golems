package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.InertGoal;
import com.mcmoddev.golems.entity.goal.LookAtWhenActiveGoal;
import com.mcmoddev.golems.entity.goal.LookRandomlyWhenActiveGoal;
import com.mcmoddev.golems.entity.goal.UseFuelGoal;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This behavior allows an entity to use fuel, accept fuel items,
 * only move and attack while fueled, and save/load fuel
 **/
@Immutable
public class UseFuelBehavior extends Behavior<GolemBase> {

	public static final Codec<UseFuelBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(Codec.intRange(1, Integer.MAX_VALUE).fieldOf("max_fuel").forGetter(UseFuelBehavior::getMaxFuel))
			.and(Codec.intRange(1, Integer.MAX_VALUE).fieldOf("burn_time").forGetter(UseFuelBehavior::getBurnTime))
			.apply(instance, UseFuelBehavior::new));

	/** The maximum amount of fuel the entity can hold **/
	protected final int maxFuel;
	/** The number of ticks it takes to deplete one unit of fuel **/
	protected final int burnTime;

	public UseFuelBehavior(MinMaxBounds.Ints variant, int maxFuel, int burnTime) {
		super(variant);
		this.maxFuel = maxFuel;
		this.burnTime = burnTime;
	}

	//// GETTERS ////

	public int getMaxFuel() {
		return maxFuel;
	}

	public int getBurnTime() {
		return burnTime;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.USE_FUEL.get();
	}

	//// METHODS ////

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		removeGoal(entity, LookAtPlayerGoal.class);
		removeGoal(entity, RandomLookAroundGoal.class);
		// TODO adjust goals to account for texture variant
		entity.goalSelector.addGoal(0, new InertGoal<>(entity));
		entity.goalSelector.addGoal(1, new UseFuelGoal<>(entity, burnTime));
		entity.goalSelector.addGoal(7, new LookAtWhenActiveGoal<>(entity, Player.class, 6.0F));
		entity.goalSelector.addGoal(8, new LookRandomlyWhenActiveGoal<>(entity));
	}

	@Override
	public void onMobInteract(final GolemBase entity, final Player player, final InteractionHand hand) {
		if (!player.isCrouching() && !player.getItemInHand(hand).isEmpty()) {
			entity.consumeFuel(player, hand);
		}
	}

	@Override
	public void onWriteData(final GolemBase entity, final CompoundTag tag) {
		entity.saveFuel(tag);
	}

	@Override
	public void onReadData(final GolemBase entity, final CompoundTag tag) {
		entity.loadFuel(tag);
	}

	@Override
	public List<Component> createDescriptions() {
		return ImmutableList.of(Component.translatable("entitytip.use_fuel").withStyle(ChatFormatting.GRAY));
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UseFuelBehavior)) return false;
		if (!super.equals(o)) return false;
		UseFuelBehavior that = (UseFuelBehavior) o;
		return maxFuel == that.maxFuel && burnTime == that.burnTime;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), maxFuel, burnTime);
	}
}
