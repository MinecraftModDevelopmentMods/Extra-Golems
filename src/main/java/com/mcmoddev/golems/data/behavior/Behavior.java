package com.mcmoddev.golems.data.behavior;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IMultitextured;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@Immutable
public abstract class Behavior<T extends LivingEntity & IMultitextured> {

	public static final Codec<Behavior> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> EGRegistry.BEHAVIOR_SERIALIZERS_SUPPLIER.get().getCodec())
			.dispatch(Behavior::getCodec, Function.identity());

	private final MinMaxBounds.Ints variant;
	private final Supplier<List<Component>> descriptions;

	public Behavior(MinMaxBounds.Ints variant) {
		this.variant = variant;
		this.descriptions = Suppliers.memoize(this::createDescriptions);
	}

	//// GETTERS ////

	/**
	 * @return the range of variant IDs required for this behavior
	 */
	public MinMaxBounds.Ints getVariant() {
		return variant;
	}

	/**
	 * @return the {@link Codec} for this behavior
	 */
	public abstract Codec<? extends Behavior<?>> getCodec();

	//// METHODS ////

	/**
	 * @param entity the entity
	 * @return true if the variant is in the range defined by {@link #getVariant()}
	 * @see #canApply(int)
	 */
	public boolean canApply(final T entity) {
		// TODO make sure all users call this before the onX methods
		return this.getVariant().matches(entity.getTextureId());
	}

	/**
	 * @param variant the golem variant
	 * @return true if the variant is in the range defined by {@link #getVariant()}
	 */
	public boolean canApply(final int variant) {
		return this.getVariant().matches(variant);
	}

	/**
	 * Called when the Golem registers goals
	 *
	 * @param entity the Golem
	 */
	public void onRegisterGoals(final T entity) { }

	/**
	 * Called when the Golem update method is called
	 *
	 * @param entity the Golem
	 */
	public void onTick(final T entity) { }

	/**
	 * Called when the Golem hurts an entity
	 *
	 * @param entity the Golem
	 * @param target the entity that was hurt
	 */
	public void onHurtTarget(final T entity, final Entity target) { }

	/**
	 * Called when the entity is hurt
	 *
	 * @param entity the entity
	 * @param source the source of the damage
	 * @param amount the amount of damage
	 */
	public void onActuallyHurt(final T entity, final DamageSource source, final float amount) { }

	/**
	 * Called when a player interacts and the interaction was not already consumed
	 *
	 * @param entity the entity
	 * @param player the Player
	 * @param hand the Player's hand
	 */
	public void onMobInteract(final T entity, final Player player, final InteractionHand hand) { }

	/**
	 * Called when the entity dies, before it is marked as removed
	 *
	 * @param entity the entity
	 * @param source the DamageSource that killed the entity
	 */
	public void onDie(final T entity, final DamageSource source) { }

	/**
	 * Called after reading additional data from NBT
	 *
	 * @param entity the entity
	 * @param tag    the entity NBT tag
	 */
	public void onWriteData(final T entity, final CompoundTag tag) { }

	/**
	 * Called after writing additional data to NBT
	 *
	 * @param entity the entity
	 * @param tag    the entity NBT tag
	 */
	public void onReadData(final T entity, final CompoundTag tag) { }

	/**
	 * @return a list of description text components to cache for later use
	 */
	public List<Component> createDescriptions() {
		return ImmutableList.of();
	}

	/**
	 * Called when building the Guide Book to add descriptions
	 *
	 * @param list the current description list
	 */
	public void onAddDescriptions(List<Component> list) {
		list.addAll(this.descriptions.get());
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Behavior)) return false;
		Behavior<?> behavior = (Behavior<?>) o;
		return variant.equals(behavior.variant);
	}

	@Override
	public int hashCode() {
		return Objects.hash(variant);
	}

	//// HELPER METHODS ////

	/**
	 * Simplifies codec creation, especially if no other fields are added
	 * @param instance the record codec builder with additional parameters, if any
	 */
	protected static <T extends Behavior<?>> Products.P1<RecordCodecBuilder.Mu<T>, MinMaxBounds.Ints> codecStart(RecordCodecBuilder.Instance<T> instance) {
		return instance.group(EGCodecUtils.MIN_MAX_INTS_CODEC.optionalFieldOf("variant", MinMaxBounds.Ints.ANY).forGetter(Behavior::getVariant));
	}

	protected static boolean removeGoal(final GolemBase entity, final Class<? extends Goal> goalToRemove) {
		final List<Goal> goalsToRemove = new ArrayList<>();
		entity.goalSelector.availableGoals.forEach(g -> {
			if (g.getGoal().getClass() == goalToRemove) {
				goalsToRemove.add(g.getGoal());
			}
		});
		// remove the matching goals
		goalsToRemove.forEach(entity.goalSelector::removeGoal);
		return !goalsToRemove.isEmpty();
	}
}
