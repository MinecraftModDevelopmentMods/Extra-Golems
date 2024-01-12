package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.model.Model;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Immutable
public class BehaviorList {

	public static final Codec<BehaviorList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EGCodecUtils.listOrElementCodec(Behavior.DIRECT_CODEC).optionalFieldOf("behaviors", ImmutableList.of()).forGetter(BehaviorList::getBehaviors)
	).apply(instance, BehaviorList::new));
	public static final Codec<Holder<BehaviorList>> HOLDER_CODEC = RegistryFileCodec.create(EGRegistry.Keys.BEHAVIOR_LISTS, CODEC, true);

	private final List<Behavior> behaviors;

	public BehaviorList(List<Behavior> behaviors) {
		this.behaviors = behaviors;
	}

	//// GETTERS ////

	public List<Behavior> getBehaviors() {
		return behaviors;
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

	public static class Builder {

		private List<Behavior> behaviors;

		public Builder() {
			this.behaviors = new ArrayList<>();
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
		public Builder removeAll(final Predicate<Behavior> predicate) {
			this.behaviors.removeIf(predicate);
			return this;
		}

		/**
		 * @return a new {@link Model} instance
		 */
		public BehaviorList build() {
			return new BehaviorList(this.behaviors);
		}
	}
}
