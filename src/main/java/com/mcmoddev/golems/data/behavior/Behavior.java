package com.mcmoddev.golems.data.behavior;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.entity.goal.IVariantPredicate;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@Immutable
public abstract class Behavior implements IVariantPredicate {

	public static final Codec<Behavior> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> EGRegistry.BEHAVIOR_SERIALIZER_SUPPLIER.get().getCodec())
			.dispatch(Behavior::getCodec, Function.identity());
	public static final Codec<Holder<Behavior>> HOLDER_CODEC = RegistryFileCodec.create(EGRegistry.Keys.BEHAVIOR, DIRECT_CODEC, true);

	private final MinMaxBounds.Ints variant;
	private final Supplier<List<Component>> descriptions;

	public Behavior(MinMaxBounds.Ints variant) {
		this.variant = variant;
		this.descriptions = Suppliers.memoize(this::createDescriptions);
	}

	//// VARIANT PREDICATE ////

	@Override
	public MinMaxBounds.Ints getVariantBounds() {
		return variant;
	}

	//// GETTERS ////

	/**
	 * @return the {@link Codec} for this behavior
	 */
	public abstract Codec<? extends Behavior> getCodec();

	//// METHODS ////

	/**
	 * Called when the Golem registers behavior data
	 *
	 * @param entity the Golem
	 */
	public void onAttachData(final IExtraGolem entity) { }

	/**
	 * Called when the Golem registers goals
	 *
	 * @param entity the Golem
	 */
	public void onRegisterGoals(final IExtraGolem entity) { }

	/**
	 * Called when the Golem registers goals, not when it normally registers synched data.
	 * Use {@link #defineSynchedData(SynchedEntityData, EntityDataAccessor, Object)} to safely add data accessors.
	 *
	 * @param entity the Golem
	 */
	public void onRegisterSynchedData(final IExtraGolem entity) { }

	/**
	 * Called when any synched data is updated
	 *
	 * @param entity the Golem
	 */
	public void onSyncedDataUpdated(final IExtraGolem entity, final EntityDataAccessor<?> key) { }

	/**
	 * Called when the Golem update method is called
	 *
	 * @param entity the Golem
	 */
	public void onTick(final IExtraGolem entity) { }

	/**
	 * Called when the Golem attacks an entity
	 *
	 * @param entity the Golem
	 * @param target the entity that was hurt
	 */
	public void onAttack(final IExtraGolem entity, final Entity target) { }

	/**
	 * Called when the Golem performs a ranged attack on an entity
	 *
	 * @param entity the Golem
	 * @param target the entity that was hurt
	 */
	public void onRangedAttack(final IExtraGolem entity, final LivingEntity target, final float distanceFactor) { }

	/**
	 * Called when the entity is hurt
	 *
	 * @param entity the entity
	 * @param source the source of the damage
	 * @param amount the amount of damage
	 */
	public void onActuallyHurt(final IExtraGolem entity, final DamageSource source, final float amount) { }

	/**
	 * Called when a player interacts and the interaction was not already consumed
	 *
	 * @param entity the entity
	 * @param player the Player
	 * @param hand the Player's hand
	 */
	public void onMobInteract(final IExtraGolem entity, final Player player, final InteractionHand hand) { }

	/**
	 * Called when the entity is struck by lightning
	 *
	 * @param entity the entity
	 * @param lightningBolt the Lightning Bolt entity
	 */
	public void onStruckByLightning(final IExtraGolem entity, final LightningBolt lightningBolt) { }

	/**
	 * Called when the entity dies, before it is marked as removed
	 *
	 * @param entity the entity
	 * @param source the DamageSource that killed the entity
	 */
	public void onDie(final IExtraGolem entity, final DamageSource source) { }

	/**
	 * Called after reading additional data from NBT
	 *
	 * @param entity the entity
	 * @param tag    the entity NBT tag
	 */
	public void onWriteData(final IExtraGolem entity, final CompoundTag tag) { }

	/**
	 * Called after writing additional data to NBT
	 *
	 * @param entity the entity
	 * @param tag    the entity NBT tag
	 */
	public void onReadData(final IExtraGolem entity, final CompoundTag tag) { }

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
		Behavior behavior = (Behavior) o;
		return variant.equals(behavior.variant);
	}

	@Override
	public int hashCode() {
		return Objects.hash(variant);
	}

	//// HELPER METHODS ////

	/**
	 * Defines synched data after verifying that it is not already defined.
	 * @param data the synched entity data
	 * @param key the entity data accessor key
	 * @param value the default value
	 * @param <T> the entity data accessor type
	 */
	protected static <T> void defineSynchedData(final SynchedEntityData data, final EntityDataAccessor<T> key, T value) {
		if(data.hasItem(key)) {
			return;
		}
		data.define(key, value);
	}

	/**
	 * Simplifies codec creation, especially if no other fields are added
	 * @param instance the record codec builder with additional parameters, if any
	 */
	protected static <T extends Behavior> Products.P1<RecordCodecBuilder.Mu<T>, MinMaxBounds.Ints> codecStart(RecordCodecBuilder.Instance<T> instance) {
		return instance.group(EGCodecUtils.MIN_MAX_INTS_CODEC.optionalFieldOf("variant", MinMaxBounds.Ints.ANY).forGetter(Behavior::getVariantBounds));
	}

	protected static boolean removeGoal(final Mob entity, final Class<? extends Goal> goalToRemove) {
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
