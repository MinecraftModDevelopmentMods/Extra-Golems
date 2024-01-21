package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.model.LayerList;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.entity.IVariantProvider;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.entity.LivingEntity;
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

@SuppressWarnings("rawtypes")
@Immutable
public class BehaviorList implements Iterable<Behavior> {

	public static final Codec<BehaviorList> CODEC = EGCodecUtils.listOrElementCodec(Behavior.DIRECT_CODEC)
			.xmap(BehaviorList::new, BehaviorList::getBehaviors);
	public static final Codec<Holder<BehaviorList>> HOLDER_CODEC = RegistryFileCodec.create(EGRegistry.Keys.BEHAVIOR_LISTS, CODEC, true);

	private final List<Behavior> behaviors;

	public BehaviorList(List<Behavior> behaviors) {
		this.behaviors = behaviors;
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
		for(Behavior b : behaviors) {
			if(b.getClass().isAssignableFrom(clazz) && b.isVariantInBounds(entity)) {
				builder.add((T) b);
			}
		}
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
		for(Behavior b : behaviors) {
			if(b.getClass().isAssignableFrom(clazz)) {
				builder.add((T) b);
			}
		}
		return builder.build();
	}

	/**
	 * @param clazz the {@link Behavior} class
	 * @return true if there is at least one active behavior with the given class
	 * @see #hasBehavior(Class)
	 */
	public boolean hasActiveBehavior(final Class<? extends Behavior> clazz, final IExtraGolem entity) {
		for(Behavior b : behaviors) {
			if(b.getClass().isAssignableFrom(clazz) && b.isVariantInBounds(entity)) {
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
		for(Behavior b : behaviors) {
			if(b.getClass().isAssignableFrom(clazz)) {
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

	@SuppressWarnings("rawtypes")
	public static class Builder {

		private List<Behavior> behaviors;

		public Builder() {
			this.behaviors = new ArrayList<>();
		}

		public Builder(List<Behavior> behaviors) {
			this.behaviors = new ArrayList<>(behaviors);
		}

		public Builder(BehaviorList behaviorList) {
			this(behaviorList.getBehaviors());
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
