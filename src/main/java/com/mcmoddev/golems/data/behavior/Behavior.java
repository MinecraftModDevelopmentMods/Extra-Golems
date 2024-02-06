package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.data.behavior.util.TriggerType;
import com.mcmoddev.golems.data.behavior.util.GolemPredicate;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.entity.goal.IVariantPredicate;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.EGComponentUtils;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Immutable
public abstract class Behavior implements IVariantPredicate {

	public static final Codec<Behavior> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> EGRegistry.BEHAVIOR_SERIALIZER_SUPPLIER.get().getCodec())
			.dispatch(Behavior::getCodec, Function.identity());

	public static final String PREFIX = "golem.description.behavior.";

	protected final MinMaxBounds.Ints variant;
	protected final TooltipPredicate tooltipPredicate;
	private final List<Component> descriptions;

	public Behavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate) {
		this.variant = variant;
		this.tooltipPredicate = tooltipPredicate;
		this.descriptions = new ArrayList<>();
	}

	//// VARIANT PREDICATE ////

	@Override
	public MinMaxBounds.Ints getVariantBounds() {
		return variant;
	}

	//// GETTERS ////

	public TooltipPredicate getTooltipPredicate() {
		return tooltipPredicate;
	}

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
	 * Called after the entity attack target changes
	 *
	 * @param entity the Golem
	 * @param target the updated attack target
	 */
	public void onTarget(final IExtraGolem entity, final @Nullable LivingEntity target) { }

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
	 * @param registryAccess the registry access
	 * @return a list of description text components to cache for later use
	 */
	public List<Component> createDescriptions(RegistryAccess registryAccess) {
		return ImmutableList.of();
	}

	/**
	 * Called when building the Guide Book to add descriptions
	 *
	 * @param registryAccess the registry access
	 * @param list the current description list
	 * @param tooltipFlag the tooltip flag
	 */
	public void onAddDescriptions(RegistryAccess registryAccess, List<Component> list, TooltipFlag tooltipFlag) {
		// load descriptions
		if(this.descriptions.isEmpty()) {
			this.descriptions.addAll(createDescriptions(registryAccess));
		}
		// test tooltip predicate and add descriptions
		if(getTooltipPredicate().test(tooltipFlag)) {
			list.addAll(this.descriptions);
		}
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
	 * Simplifies codec creation, especially if no other fields are added
	 * @param instance the record codec builder with additional parameters, if any
	 */
	protected static <T extends Behavior> Products.P2<RecordCodecBuilder.Mu<T>, MinMaxBounds.Ints, TooltipPredicate> codecStart(RecordCodecBuilder.Instance<T> instance) {
		return instance.group(
				EGCodecUtils.MIN_MAX_INTS_CODEC.optionalFieldOf("variant", MinMaxBounds.Ints.ANY).forGetter(Behavior::getVariantBounds),
				TooltipPredicate.CODEC.optionalFieldOf("tooltip", TooltipPredicate.NORMAL).forGetter(Behavior::getTooltipPredicate));
	}

	/**
	 * @param trigger a trigger type, where {@link TriggerType#TICK} will be ignored
	 * @param predicates a list of world predicates, where {@link GolemPredicate#ALWAYS} and {@link GolemPredicate#NEVER} will be ignored
	 * @return a single component combining the trigger type and world predicates, if any apply
	 */
	protected static Optional<Component> createTriggerAndPredicateDescription(final TriggerType trigger, final List<GolemPredicate> predicates) {
		// create filtered list of predicates, ignoring ALWAYS and NEVER
		final List<GolemPredicate> filteredPredicates = predicates.stream()
				.filter(p -> p != GolemPredicate.ALWAYS && p != GolemPredicate.NEVER)
				.collect(ImmutableList.toImmutableList());

		// create component from predicates list
		final Optional<Component> oPredicates = EGComponentUtils.combineWithAnd(filteredPredicates, GolemPredicate::getDescriptionId);
		// create component from trigger
		final Optional<Component> oTrigger = trigger == TriggerType.TICK ? Optional.empty() : Optional.of(Component.translatable(trigger.getDescriptionId()));

		// use either component, or combine both
		if(oPredicates.isPresent() && oTrigger.isPresent()) {
			return Optional.of(Component.translatable("golem.description.predicate.multiple", oTrigger.get(), oPredicates.get()));
		} else if(oPredicates.isPresent()) {
			return oPredicates;
		} else if(oTrigger.isPresent()) {
			return oTrigger;
		}
		return Optional.empty();
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
