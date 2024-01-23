package com.mcmoddev.golems.data.model;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Immutable
public class LayerList implements Iterable<Either<Layer, Holder<LayerList>>>, Supplier<List<Layer>> {

	public static final Codec<LayerList> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EGCodecUtils.listOrElementCodec(LayerList.EITHER_CODEC).optionalFieldOf("layers", ImmutableList.of()).forGetter(LayerList::getLayers)
	).apply(instance, LayerList::new));
	public static final Codec<Holder<LayerList>> HOLDER_CODEC = RegistryFileCodec.create(EGRegistry.Keys.MODELS, LayerList.CODEC, true);
	public static final Codec<Either<Layer, Holder<LayerList>>> EITHER_CODEC = Codec.either(Layer.CODEC, LayerList.HOLDER_CODEC);

	private final List<Either<Layer, Holder<LayerList>>> layers;
	private final List<Layer> flatList;
	private final List<Layer> flatListView;

	public LayerList(List<Either<Layer, Holder<LayerList>>> layers) {
		this.layers = ImmutableList.copyOf(layers);
		this.flatList = new ArrayList<>();
		this.flatListView = Collections.unmodifiableList(this.flatList);
	}

	//// ITERABLE ////

	@NotNull
	@Override
	public Iterator<Either<Layer, Holder<LayerList>>> iterator() {
		return layers.iterator();
	}

	//// GETTERS ////

	public List<Either<Layer, Holder<LayerList>>> getLayers() {
		return layers;
	}

	/**
	 * DO NOT CALL UNTIL THE SERVER IS RUNNING
	 * @return the lazy-populated list of layers where all holders have been resolved into layer lists.
	 */
	public List<Layer> get() {
		if(this.flatList.isEmpty()) {
			for(Either<Layer, Holder<LayerList>> either : this.layers) {
				either.map(this.flatList::add, holder -> this.flatList.addAll(holder.get().get()));
			}
		}
		return this.flatListView;
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LayerList)) return false;
		LayerList other = (LayerList) o;
		return Objects.equals(layers, other.layers);
	}

	@Override
	public int hashCode() {
		return Objects.hash(layers);
	}

	//// BUILDER ////

	public static class Builder {

		private List<Either<Layer, Holder<LayerList>>> layers;

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
		public Builder(final List<Either<Layer, Holder<LayerList>>> layers) {
			this.layers = new ArrayList<>(layers);
		}

		/**
		 * Creates a builder that is a copy of the given model
		 * @param layerList a model
		 */
		public Builder(LayerList layerList) {
			this(layerList.getLayers());
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
		public Builder add(final Holder<LayerList> model) {
			this.layers.add(Either.right(model));
			return this;
		}

		/**
		 * @param collection the layers to add
		 * @return the builder instance
		 */
		public Builder addAll(final Collection<Either<Layer, Holder<LayerList>>> collection) {
			this.layers.addAll(collection);
			return this;
		}

		/**
		 * @param predicate the predicate for layers to remove
		 * @return the builder instance
		 */
		public Builder remove(final Predicate<Either<Layer, Holder<LayerList>>> predicate) {
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
		 * @return a new {@link LayerList} instance
		 */
		public LayerList build() {
			return new LayerList(this.layers);
		}
	}
}
