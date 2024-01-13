package com.mcmoddev.golems.data.model;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.datafixers.util.Either;
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
			EGCodecUtils.listOrElementCodec(Model.EITHER_CODEC).optionalFieldOf("layers", ImmutableList.of()).forGetter(Model::getLayers)
	).apply(instance, Model::new));
	public static final Codec<Holder<Model>> HOLDER_CODEC = RegistryFileCodec.create(EGRegistry.Keys.MODELS, Model.CODEC, true);
	public static final Codec<Either<Layer, Holder<Model>>> EITHER_CODEC = Codec.either(Layer.CODEC, Model.HOLDER_CODEC);

	private final List<Either<Layer, Holder<Model>>> layers;

	public Model(List<Either<Layer, Holder<Model>>> layers) {
		this.layers = ImmutableList.copyOf(layers);
	}

	//// GETTERS ////

	public List<Either<Layer, Holder<Model>>> getLayers() {
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

		private List<Either<Layer, Holder<Model>>> layers;

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
		public Builder(final List<Either<Layer, Holder<Model>>> layers) {
			this.layers = new ArrayList<>(layers);
		}

		/**
		 * @param layer the layer to add
		 * @return the builder instance
		 */
		public Builder add(final Layer layer) {
			this.layers.add(Either.left(layer));
			return this;
		}

		/**
		 * @param model the model to add
		 * @return the builder instance
		 */
		public Builder add(final Holder<Model> model) {
			this.layers.add(Either.right(model));
			return this;
		}

		/**
		 * @param collection the layers to add
		 * @return the builder instance
		 */
		public Builder addAll(final Collection<Either<Layer, Holder<Model>>> collection) {
			this.layers.addAll(collection);
			return this;
		}

		/**
		 * @param predicate the predicate for layers to remove
		 * @return the builder instance
		 */
		public Builder remove(final Predicate<Either<Layer, Holder<Model>>> predicate) {
			this.layers.removeIf(predicate);
			return this;
		}

		/**
		 * @return the builder instance
		 */
		public Builder clear() {
			this.layers.clear();
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
