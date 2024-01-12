package com.mcmoddev.golems.data.model;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
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
public class Model {

	public static final Codec<Model> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EGCodecUtils.listOrElementCodec(Layer.CODEC).optionalFieldOf("layers", ImmutableList.of()).forGetter(Model::getLayers)
	).apply(instance, Model::new));
	public static final Codec<Holder<Model>> HOLDER_CODEC = RegistryFileCodec.create(EGRegistry.Keys.MODELS, CODEC, true);

	private final List<Layer> layers;

	public Model(List<Layer> layers) {
		this.layers = ImmutableList.copyOf(layers);
	}

	//// GETTERS ////

	public List<Layer> getLayers() {
		return layers;
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Model)) return false;
		Model other = (Model) o;
		return Objects.equals(layers, other.layers);
	}

	@Override
	public int hashCode() {
		return Objects.hash(layers);
	}


	//// BUILDER ////

	public static class Builder {

		private List<Layer> layers;

		/**
		 * Creates a builder with an empty list of layers
		 */
		public Builder() {
			this.layers = new ArrayList<>();
		}

		/**
		 * Creates a builder with the given list of layers
		 * @param layers a list of layers
		 */
		public Builder(final List<Layer> layers) {
			this.layers = new ArrayList<>(layers);
		}

		/**
		 * @param layer the layer to add
		 * @return the builder instance
		 */
		public Builder add(final Layer layer) {
			this.layers.add(layer);
			return this;
		}

		/**
		 * @param collection the layers to add
		 * @return the builder instance
		 */
		public Builder addAll(final Collection<Layer> collection) {
			this.layers.addAll(collection);
			return this;
		}

		/**
		 * @param predicate the predicate for layers to remove
		 * @return the builder instance
		 */
		public Builder remove(final Predicate<Layer> predicate) {
			this.layers.removeIf(predicate);
			return this;
		}

		/**
		 * @return a new {@link Model} instance
		 */
		public Model build() {
			return new Model(this.layers);
		}
	}
}
