package com.mcmoddev.golems.data.golem;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.BehaviorList;
import com.mcmoddev.golems.data.model.LayerList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;
import java.util.function.Consumer;

@Immutable
public class Golem {

	public static final Codec<Golem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Golem.HOLDER_CODEC.optionalFieldOf("parent").forGetter(o -> Optional.ofNullable(o.parent)),
			Attributes.CODEC.optionalFieldOf("attributes").forGetter(o -> Optional.ofNullable(o.attributes)),
			BuildingBlocks.CODEC.optionalFieldOf("blocks", BuildingBlocks.EMPTY).forGetter(Golem::getBlocks),
			RepairItems.CODEC.optionalFieldOf("repair_items", RepairItems.EMPTY).forGetter(Golem::getRepairItems),
			Codec.intRange(1, 127).optionalFieldOf("variants", 1).forGetter(Golem::getVariants),
			Codec.BOOL.optionalFieldOf("hidden", false).forGetter(Golem::isHidden),
			ParticleTypes.CODEC.optionalFieldOf("particle").forGetter(o -> Optional.ofNullable(o.particle)),
			LayerList.HOLDER_CODEC.optionalFieldOf("model", Holder.direct(new LayerList(ImmutableList.of()))).forGetter(Golem::getLayers),
			BehaviorList.HOLDER_CODEC.optionalFieldOf("brain", Holder.direct(new BehaviorList(ImmutableList.of()))).forGetter(Golem::getBehaviors),
			ResourceLocation.CODEC.optionalFieldOf("group").forGetter(o -> Optional.ofNullable(o.group))
	).apply(instance, Golem::new));
	public static final Codec<Holder<Golem>> HOLDER_CODEC = RegistryFileCodec.create(EGRegistry.Keys.GOLEMS, CODEC, true);

	private final @Nullable Holder<Golem> parent;
	private final @Nullable Attributes attributes;
	private final BuildingBlocks blocks;
	private final RepairItems repairItems;
	private final int variants;
	private final boolean hidden;
	private final @Nullable ParticleOptions particle;
	private final Holder<LayerList> layers;
	private final Holder<BehaviorList> behaviors;
	private final @Nullable ResourceLocation group;

	public Golem(Optional<Holder<Golem>> parent, Optional<Attributes> attributes,
				 BuildingBlocks blocks, RepairItems repairItems,
				 int variants, boolean hidden, Optional<ParticleOptions> particle,
				 Holder<LayerList> layers, Holder<BehaviorList> behaviors, Optional<ResourceLocation> group) {
		this.parent = parent.orElse(null);
		this.attributes = attributes.orElse(null);
		if(parent.isEmpty() && attributes.isEmpty()) {
			throw new IllegalArgumentException("Failed to parse Golem because both parent and attributes are not defined!");
		}
		this.blocks = blocks;
		this.repairItems = repairItems;
		this.variants = variants;
		this.hidden = hidden;
		this.particle = particle.orElse(null);
		this.layers = layers;
		this.behaviors = behaviors;
		this.group = group.orElse(null);
	}

	//// GETTERS ////

	@Nullable
	public Holder<Golem> getParent() {
		return parent;
	}

	@Nullable
	public Attributes getAttributes() {
		return attributes;
	}

	public BuildingBlocks getBlocks() {
		return blocks;
	}

	public RepairItems getRepairItems() {
		return repairItems;
	}

	public int getVariants() {
		return variants;
	}

	public boolean isHidden() {
		return hidden;
	}

	@Nullable
	public ParticleOptions getParticle() {
		return particle;
	}

	public Holder<LayerList> getLayers() {
		return layers;
	}

	public Holder<BehaviorList> getBehaviors() {
		return behaviors;
	}

	@Nullable
	public ResourceLocation getGroup() {
		return group;
	}

	//// BUILDER ////

	public static class Builder {
		private Holder<Golem> parent;
		private Attributes.Builder attributes;
		private BuildingBlocks.Builder blocks;
		private RepairItems.Builder repairItems;
		private int variants;
		private boolean hidden;
		private ParticleOptions particle;
		private LayerList.Builder layers;
		private BehaviorList.Builder behaviors;
		private ResourceLocation group;

		//// CONSTRUCTOR ////

		private Builder(final RegistryAccess registryAccess) {
			this.blocks = new BuildingBlocks.Builder();
			this.attributes = new Attributes.Builder();
			this.repairItems = new RepairItems.Builder();
			this.layers = new LayerList.Builder();
			this.behaviors = new BehaviorList.Builder(registryAccess);
		}

		public static Builder from(final RegistryAccess registryAccess, final Golem golem) {
			final Builder builder = new Golem.Builder(registryAccess);
			final boolean hasParent = golem.getParent() != null;
			final Golem parent = hasParent ? golem.getParent().get() : null;
			// apply settings from parent
			if(hasParent) {
				builder.parent = golem.getParent();
				builder.attributes(b -> b.copy(parent.getAttributes()))
						.blocks(b -> b.addAll(parent.getBlocks().getList()))
						.repairItems(b -> b.addAll(parent.getRepairItems().getMap()))
						.variants(parent.getVariants())
						.hidden(parent.isHidden())
						.particle(parent.getParticle())
						.layers(b -> b.addAll(parent.getLayers().get().getLayers()))
						.behaviors(b -> b.addAllHolders(parent.getBehaviors().get().getBehaviors()))
						.group(parent.getGroup());
			}
			// attributes (merges parent)
			if(golem.getAttributes() != null) {
				builder.attributes(b -> b.copy(golem.getAttributes()));
			}
			// blocks (replaces parent)
			if(!hasParent || !golem.getBlocks().equals(parent.getBlocks())) {
				builder.blocks(b -> {
					b.clear();
					b.addAll(golem.getBlocks().getList());
				});
			}
			// repair items (replaces parent)
			if(!hasParent || !golem.getRepairItems().getMap().equals(parent.getRepairItems().getMap())) {
				builder.repairItems(b -> {
					b.clear();
					b.addAll(golem.getRepairItems().getMap());
				});
			}
			// variants (replaces parent)
			if(!hasParent || golem.getVariants() != parent.getVariants()) {
				builder.variants(golem.getVariants());
			}
			// hidden (replaces parent)
			if(!hasParent || golem.isHidden() != parent.isHidden()) {
				builder.hidden(golem.isHidden());
			}
			// particle (replaces parent)
			if(golem.getParticle() != null) {
				builder.particle(golem.getParticle());
			}
			// model (replaces parent)
			if(!hasParent || !golem.getLayers().get().equals(parent.getLayers().get())) {
				builder.layers(b -> {
					b.clear();
					b.addAll(golem.getLayers().get().getLayers());
				});
			}
			// behaviors (replaces parent)
			if(!hasParent || !golem.getBehaviors().get().equals(parent.getBehaviors().get())) {
				builder.behaviors(b -> {
					b.clear();
					b.addAllHolders(golem.getBehaviors().get().getBehaviors());
				});
			}
			// group (replaces parent)
			if(golem.getGroup() != null) {
				builder.group(golem.getGroup());
			}
			return builder;
		}

		//// METHODS ////

		/**
		 * @param action the action to perform on the attributes builder
		 * @return the builder instance
		 */
		public Builder attributes(final Consumer<Attributes.Builder> action) {
			action.accept(this.attributes);
			return this;
		}

		/**
		 * @param attributes the new builder to use.
		 * Warning: this causes previous calls to {@link #attributes(Consumer)} to be rendered useless.
		 * @return the builder instance
		 * @see #attributes(Consumer)
		 */
		public Builder attributes(final Attributes.Builder attributes) {
			this.attributes = attributes;
			return this;
		}

		/**
		 * @param action the action to perform on the blocks
		 * @return the builder instance
		 */
		public Builder blocks(final Consumer<BuildingBlocks.Builder> action) {
			action.accept(this.blocks);
			return this;
		}

		/**
		 * @param blocks the new builder to use.
		 * Warning: this causes previous calls to {@link #blocks(Consumer)} to be rendered useless.
		 * @return the builder instance
		 * @see #blocks(Consumer)
		 */
		public Builder blocks(final BuildingBlocks.Builder blocks) {
			this.blocks = blocks;
			return this;
		}

		/**
		 * @param action the action to perform on the repair items builder
		 * @return the builder instance
		 */
		public Builder repairItems(final Consumer<RepairItems.Builder> action) {
			action.accept(this.repairItems);
			return this;
		}

		/**
		 * @param repairItems the new builder to use.
		 * Warning: this causes previous calls to {@link #repairItems(Consumer)} to be rendered useless.
		 * @return the builder instance
		 * @see #repairItems(Consumer)
		 */
		public Builder repairItems(final RepairItems.Builder repairItems) {
			this.repairItems = repairItems;
			return this;
		}

		/**
		 * @param variants the maximum variants
		 * @return the builder instance
		 */
		public Builder variants(final int variants) {
			this.variants = variants;
			return this;
		}

		/**
		 * @param amount the amount of maximum variants to add
		 * @return the builder instance
		 */
		public Builder addVariants(final int amount) {
			this.variants += amount;
			return this;
		}

		/**
		 * @param hidden true to hide from the UI
		 * @return the builder instance
		 */
		public Builder hidden(final boolean hidden) {
			this.hidden = hidden;
			return this;
		}

		/**
		 * @param particle the particle options
		 * @return the builder instance
		 */
		public Builder particle(final ParticleOptions particle) {
			this.particle = particle;
			return this;
		}

		/**
		 * @param action the action to perform on the model builder
		 * @return the builder instance
		 */
		public Builder layers(final Consumer<LayerList.Builder> action) {
			action.accept(this.layers);
			return this;
		}

		/**
		 * @param layers the new builder to use.
		 * Warning: this causes previous calls to {@link #layers(Consumer)} to be rendered useless.
		 * @return the builder instance
		 * @see #layers(Consumer)
		 */
		public Builder layers(final LayerList.Builder layers) {
			this.layers = layers;
			return this;
		}

		/**
		 * @param action the action to perform on the behavior list builder
		 * @return the builder instance
		 */
		public Builder behaviors(final Consumer<BehaviorList.Builder> action) {
			action.accept(this.behaviors);
			return this;
		}

		/**
		 * @param behaviors the new builder to use.
		 * Warning: this causes previous calls to {@link #behaviors(Consumer)} to be rendered useless.
		 * @return the builder instance
		 * @see #behaviors(Consumer)
		 */
		public Builder behaviors(final BehaviorList.Builder behaviors) {
			this.behaviors = behaviors;
			return this;
		}

		/**
		 * @param group the group for the UI
		 * @return the builder instance
		 */
		public Builder group(final ResourceLocation group) {
			this.group = group;
			return this;
		}

		/**
		 * @return a new {@link Golem} instance
		 */
		public Golem build() {
			return new Golem(Optional.ofNullable(parent), Optional.of(attributes.build()), blocks.build(),
					repairItems.build(), variants, hidden, Optional.ofNullable(particle),
					Holder.direct(layers.build()), Holder.direct(behaviors.build()), Optional.ofNullable(group));
		}
	}
}
