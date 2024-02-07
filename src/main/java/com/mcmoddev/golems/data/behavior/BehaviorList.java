package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.model.LayerList;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryFileCodec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Immutable
public class BehaviorList implements Iterable<Behavior> {

	public static final Codec<BehaviorList> CODEC = Behavior.DIRECT_CODEC.listOf()
			.xmap(BehaviorList::new, BehaviorList::getBehaviors).fieldOf("behaviors").codec();
	public static final Codec<Holder<BehaviorList>> HOLDER_CODEC = RegistryFileCodec.create(EGRegistry.Keys.BEHAVIOR_LIST, CODEC, true);

	private final List<Behavior> behaviors;
	private final Map<Class<? extends Behavior>, List<Behavior>> behaviorsByClass;

	public BehaviorList(List<Behavior> behaviors) {
		this.behaviors = behaviors;
		// collect behaviors by class
		final Map<Class<? extends Behavior>, List<Behavior>> builder = new HashMap<>();
		for(Behavior b : behaviors) {
			builder.computeIfAbsent(b.getClass(), c -> new ArrayList<>()).add(b);
		}
		// convert to immutable lists
		builder.replaceAll((key, value) -> ImmutableList.copyOf(value));
		// create map
		this.behaviorsByClass = ImmutableMap.copyOf(builder);
	}

	//// ITERABLE ////

	@Override
	public void forEach(Consumer<? super Behavior> action) {
		behaviors.forEach(action);
	}

	@Override
	public Spliterator<Behavior> spliterator() {
		return behaviors.spliterator();
	}

	@NotNull
	@Override
	public Iterator<Behavior> iterator() {
		return behaviors.iterator();
	}

	//// GETTERS ////

	public List<Behavior> getBehaviors() {
		return behaviors;
	}

	/**
	 * @param clazz the {@link Behavior} class
	 * @param <T> the behavior class
	 * @return a list of active behaviors with the given class
	 * @see #getBehaviors(Class)
	 */
	public <T extends Behavior> List<T> getActiveBehaviors(final Class<T> clazz, final IExtraGolem entity) {
		final ImmutableList.Builder<T> builder = ImmutableList.builder();
		for(T b : getBehaviors(clazz)) {
			if(b.isVariantInBounds(entity)) {
				builder.add(b);
			}
		}
		return builder.build();
	}

	/**
	 * @param entity the entity
	 * @return an iterable of the behaviors that are currently active
	 */
	public Iterable<Behavior> getActiveBehaviors(final IExtraGolem entity) {
		return Iterables.filter(this.behaviors, b -> b.isVariantInBounds(entity));
	}

	/**
	 * @param clazz the {@link Behavior} class
	 * @param <T> the behavior class
	 * @return a list of behaviors with the given class
	 * @see #getActiveBehaviors(Class, IExtraGolem)
	 */
	public <T extends Behavior> List<T> getBehaviors(final Class<T> clazz) {
		return (List<T>) behaviorsByClass.getOrDefault(clazz, ImmutableList.of());
	}

	/**
	 * @param clazz the {@link Behavior} class
	 * @param entity the entity
	 * @return true if there is at least one active behavior with the given class
	 * @see #hasBehavior(Class)
	 */
	public boolean hasActiveBehavior(final Class<? extends Behavior> clazz, final IExtraGolem entity) {
		for(Behavior b : getBehaviors(clazz)) {
			if(b.isVariantInBounds(entity)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param clazz the {@link Behavior} class
	 * @return true if there is at least one behavior with the given class
	 * @see #hasActiveBehavior(Class, IExtraGolem)
	 */
	public boolean hasBehavior(final Class<? extends Behavior> clazz) {
		return behaviorsByClass.containsKey(clazz);
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BehaviorList)) return false;
		BehaviorList other = (BehaviorList) o;
		return Objects.equals(behaviors, other.behaviors);
	}

	@Override
	public int hashCode() {
		return Objects.hash(behaviors);
	}

	//// CLASSES ////

	public static class Builder {

		private final List<Behavior> behaviors;

		public Builder() {
			this(ImmutableList.of());
		}

		public Builder(BehaviorList behaviorList) {
			this(behaviorList.getBehaviors());
		}

		public Builder(List<Behavior> behaviors) {
			this.behaviors = new ArrayList<>(behaviors);
		}

		/**
		 * @param behavior the behavior to add
		 * @return the builder instance
		 */
		public Builder add(final Behavior behavior) {
			this.behaviors.add(behavior);
			return this;
		}

		/**
		 * @param collection the behaviors to add
		 * @return the builder instance
		 */
		public Builder addAll(final Collection<Behavior> collection) {
			this.behaviors.addAll(collection);
			return this;
		}

		/**
		 * @param predicate the predicate for behaviors to remove
		 * @return the builder instance
		 */
		public Builder remove(final Predicate<Behavior> predicate) {
			this.behaviors.removeIf(predicate);
			return this;
		}

		/**
		 * @return the builder instance
		 */
		public Builder clear() {
			this.behaviors.clear();
			return this;
		}

		/**
		 * @return a new {@link LayerList} instance
		 */
		public BehaviorList build() {
			return new BehaviorList(this.behaviors);
		}

	}
}
