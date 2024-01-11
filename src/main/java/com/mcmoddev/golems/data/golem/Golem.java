package com.mcmoddev.golems.data.golem;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.Behaviors;
import com.mcmoddev.golems.data.model.Model;
import com.mcmoddev.golems.util.DeferredHolderSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@Immutable
public class Golem {

	public static final Codec<Golem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Golem.HOLDER_CODEC.optionalFieldOf("parent").forGetter(o -> Optional.ofNullable(o.parent)),
			Attributes.HOLDER_CODEC.optionalFieldOf("attributes").forGetter(o -> Optional.ofNullable(o.attributes)),
			DeferredHolderSet.codec(ForgeRegistries.BLOCKS.getRegistryKey()).optionalFieldOf("blocks", new DeferredHolderSet<>(ImmutableList.of())).forGetter(Golem::getBlocks),
			Codec.intRange(1, 127).optionalFieldOf("variants", 1).forGetter(Golem::getVariants),
			Codec.BOOL.optionalFieldOf("hidden", false).forGetter(Golem::isHidden),
			ParticleTypes.CODEC.optionalFieldOf("particle").forGetter(o -> Optional.ofNullable(o.particle)),
			Model.HOLDER_CODEC.optionalFieldOf("model", Holder.direct(new Model(ImmutableList.of()))).forGetter(Golem::getModel),
			Behaviors.HOLDER_CODEC.optionalFieldOf("brain", Holder.direct(new Behaviors(ImmutableList.of()))).forGetter(Golem::getBehaviors)
	).apply(instance, Golem::new));
	public static final Codec<Holder<Golem>> HOLDER_CODEC = RegistryFileCodec.create(EGRegistry.Keys.GOLEM, CODEC, true);

	@Nullable
	private final Holder<Golem> parent;
	@Nullable
	private final Holder<Attributes> attributes;
	private final DeferredHolderSet<Block> blocks;
	// TODO RepairItems
	// TODO sound type
	private final SoundType soundType;
	private final int variants;
	private final boolean hidden;
	@Nullable
	private final ParticleOptions particle;
	private final Holder<Model> model;
	private final Holder<Behaviors> behaviors;

	public Golem(Optional<Holder<Golem>> parent, Optional<Holder<Attributes>> attributes,
				 DeferredHolderSet<Block> blocks, int variants, boolean hidden, Optional<ParticleOptions> particle,
				 Holder<Model> model, Holder<Behaviors> behaviors) {
		this.parent = parent.orElse(null);
		this.attributes = attributes.orElse(null);
		this.blocks = blocks;
		this.variants = variants;
		this.hidden = hidden;
		this.particle = particle.orElse(null);
		this.model = model;
		this.behaviors = behaviors;
	}

	//// GETTERS ////

	@Nullable
	public Holder<Golem> getParent() {
		return parent;
	}

	@Nullable
	public Holder<Attributes> getAttributes() {
		return attributes;
	}

	public DeferredHolderSet<Block> getBlocks() {
		return blocks;
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

	public Holder<Model> getModel() {
		return model;
	}

	public Holder<Behaviors> getBehaviors() {
		return behaviors;
	}

	//// BUILDER ////

	public static class Builder {
		@Nullable
		private Holder<Golem> parent;
		private Holder<Attributes> attributes;
		private int variants;
		private boolean hidden;
		@Nullable
		private ParticleOptions particle;
		private Holder<Model> model;
		private Holder<Behaviors> behaviors;

		//// CONSTRUCTOR ////

		private Builder() {}

		public static Builder from(final Golem golem) {
			final Builder builder = new Golem.Builder();
			final boolean hasParent = golem.getParent() != null;
			Golem parent = null;
			// apply settings from parent
			if(hasParent) {
				parent = golem.getParent().get();
				builder.parent(golem.getParent())
						.attributes(parent.getAttributes())
						.variants(parent.getVariants())
						.hidden(parent.isHidden())
						.particle(parent.getParticle())
						.model(parent.getModel())
						.behaviors(parent.getBehaviors());
			}
			// attributes
			if(golem.getAttributes() != null) {
				builder.attributes(golem.getAttributes());
			}
			// variants
			if(!hasParent || golem.getVariants() != parent.getVariants()) {
				builder.variants(golem.getVariants());
			}
			// hidden
			if(!hasParent || golem.isHidden() != parent.isHidden()) {
				builder.hidden(golem.isHidden());
			}
			// particle
			if(golem.getParticle() != null) {
				builder.particle(golem.getParticle());
			}
			// model
			if(!hasParent || !golem.getModel().get().equals(parent.getModel().get())) {
				builder.model(golem.getModel());
			}
			// behaviors
			if(!hasParent || !golem.getBehaviors().get().equals(parent.getBehaviors().get())) {
				builder.behaviors(golem.getBehaviors());
			}
			return builder;
		}

		//// GETTERS ////

		public Holder<Model> getModel() {
			return model;
		}

		public Holder<Behaviors> getBehaviors() {
			return behaviors;
		}


		//// METHODS ////

		/**
		 * @param parent the parent golem
		 * @return the builder instance
		 */
		public Builder parent(final Holder<Golem> parent) {
			this.parent = parent;
			return this;
		}

		/**
		 * @param attributes the golem attributes
		 * @return the builder instance
		 */
		public Builder attributes(final Holder<Attributes> attributes) {
			this.attributes = attributes;
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
		 * @param model the model holder
		 * @return the builder instance
		 */
		public Builder model(final Holder<Model> model) {
			this.model = model;
			return this;
		}

		/**
		 * @param behaviors the behaviors holder
		 * @return the builder instance
		 */
		public Builder behaviors(final Holder<Behaviors> behaviors) {
			this.behaviors = behaviors;
			return this;
		}

		/**
		 * @return a new {@link Golem} instance
		 */
		public Golem build() {
			return new Golem(Optional.ofNullable(parent), Optional.ofNullable(attributes), blocks, variants, hidden, Optional.ofNullable(particle), model, behaviors);
		}
	}
}
