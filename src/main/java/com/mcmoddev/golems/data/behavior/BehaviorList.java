package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Immutable
public class BehaviorList implements Iterable<Behavior> {

	public static final Codec<BehaviorList> CODEC = EGCodecUtils.listOrElementCodec(Behavior.HOLDER_CODEC)
			.xmap(BehaviorList::new, BehaviorList::getBehaviors).fieldOf("behaviors").codec();
	public static final Codec<Holder<BehaviorList>> HOLDER_CODEC = RegistryFileCodec.create(EGRegistry.Keys.BEHAVIOR_LIST, CODEC, true);

	private final List<Holder<Behavior>> behaviors;

	public BehaviorList(List<Holder<Behavior>> behaviors) {
		this.behaviors = behaviors;
	}

	//// ITERABLE ////

	@Override
	public void forEach(Consumer<? super Behavior> action) {
		behaviors.stream().map(Holder::get).forEach(action);
	}

	@Override
	public Spliterator<Behavior> spliterator() {
		return behaviors.stream().map(Holder::get).spliterator();
	}

	@NotNull
	@Override
	public Iterator<Behavior> iterator() {
		return behaviors.stream().map(Holder::get).iterator();
	}

	//// GETTERS ////

	public List<Holder<Behavior>> getBehaviors() {
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
		this.iterator().forEachRemaining(b -> {
			if(b.getClass().isAssignableFrom(clazz) && b.isVariantInBounds(entity)) {
				builder.add((T) b);
			}
		});
		return builder.build();
	}

	/**
	 * @param clazz the {@link Behavior} class
	 * @param <T> the behavior class
	 * @return a list of behaviors with the given class
	 * @see #getActiveBehaviors(Class, IExtraGolem)
	 */
	public <T extends Behavior> List<T> getBehaviors(final Class<T> clazz) {
		final ImmutableList.Builder<T> builder = ImmutableList.builder();
		this.iterator().forEachRemaining(b -> {
			if(b.getClass().isAssignableFrom(clazz)) {
				builder.add((T) b);
			}
		});
		return builder.build();
	}

	/**
	 * @param clazz the {@link Behavior} class
	 * @return true if there is at least one active behavior with the given class
	 * @see #hasBehavior(Class)
	 */
	public boolean hasActiveBehavior(final Class<? extends Behavior> clazz, final IExtraGolem entity) {
		for(Holder<Behavior> b : behaviors) {
			if(b.get().getClass().isAssignableFrom(clazz) && b.get().isVariantInBounds(entity)) {
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
		for(Holder<Behavior> b : behaviors) {
			if(b.get().getClass().isAssignableFrom(clazz)) {
				return true;
			}
		}
		return false;
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

		private final List<Holder<Behavior>> behaviors;
		private final Registry<Behavior> registry;

		public Builder(final RegistryAccess registryAccess) {
			this(registryAccess, ImmutableList.of());
		}

		public Builder(final RegistryAccess registryAccess, BehaviorList behaviorList) {
			this(registryAccess, behaviorList.getBehaviors());
		}

		public Builder(final RegistryAccess registryAccess, List<Holder<Behavior>> behaviors) {
			this.behaviors = new ArrayList<>(behaviors);
			this.registry = registryAccess.registryOrThrow(EGRegistry.Keys.BEHAVIOR);
		}

		/**
		 * @param behavior the behavior to add
		 * @return the builder instance
		 */
		public Builder add(final Behavior behavior) {
			this.behaviors.add(registry.wrapAsHolder(behavior));
			return this;
		}

		/**
		 * @param collection the behaviors to add
		 * @return the builder instance
		 */
		public Builder addAll(final Collection<Behavior> collection) {
			for(Behavior b : collection) {
				this.behaviors.add(registry.wrapAsHolder(b));
			}
			return this;
		}

		/**
		 * @param collection the behavior holders to add
		 * @return the builder instance
		 */
		public Builder addAllHolders(final Collection<Holder<Behavior>> collection) {
			this.behaviors.addAll(collection);
			return this;
		}

		/**
		 * @param predicate the predicate for behaviors to remove
		 * @return the builder instance
		 */
		public Builder remove(final Predicate<Behavior> predicate) {
			this.behaviors.removeIf(b -> predicate.test(b.get()));
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
