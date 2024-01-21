package com.mcmoddev.golems.data.behavior.util;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Optional;

@Immutable
public class UpdateTarget {

	public static final Codec<UpdateTarget> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.optionalFieldOf("golem").forGetter(o -> Optional.ofNullable(o.golem)),
			// TODO update this from 127 when changing the max variant
			IntProvider.codec(0, 127).optionalFieldOf("variant").forGetter(o -> Optional.ofNullable(o.variant)),
			Codec.BOOL.optionalFieldOf("cycle").forGetter(o -> Optional.ofNullable(o.cycle))
	).apply(instance, UpdateTarget::new));

	protected final @Nullable ResourceLocation golem;
	protected final @Nullable IntProvider variant;
	protected final @Nullable Boolean cycle;

	public UpdateTarget(Optional<ResourceLocation> golem, Optional<IntProvider> variant, Optional<Boolean> cycle) {
		this.golem = golem.orElse(null);
		this.variant = variant.orElse(null);
		this.cycle = cycle.orElse(null);
	}

	//// METHODS ////

	/**
	 * Updates the golem and variant of the given entity
	 * @param entity the golem entity
	 * @return true if either the golem or the variant changed
	 */
	public boolean apply(final IExtraGolem entity) {
		boolean flag = false;
		// update golem
		if(golem != null && !golem.equals(entity.getGolemId())) {
			entity.setGolemId(golem);
			flag = true;
		}
		// update variant
		if(variant != null) {
			// sample the int provider
			int sample = variant.sample(entity.asMob().getRandom());
			// update the variant if necessary
			if(sample != entity.getVariant()) {
				entity.setVariant(sample);
				flag = true;
			}
		}
		// cycle variant (only when golem and variant are both unspecified)
		if(null == golem && null == variant && cycle != null && cycle) {
			entity.cycleVariant();
			flag = true;
		}
		return flag;
	}

	//// GETTERS ////

	@Nullable
	public ResourceLocation getGolem() {
		return golem;
	}

	@Nullable
	public IntProvider getVariant() {
		return variant;
	}

	@Nullable
	public Boolean getCycle() {
		return cycle;
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UpdateTarget)) return false;
		UpdateTarget other = (UpdateTarget) o;
		return Objects.equals(golem, other.golem) && Objects.equals(variant, other.variant) && Objects.equals(cycle, other.cycle);
	}

	@Override
	public int hashCode() {
		return Objects.hash(golem, variant, cycle);
	}
}
