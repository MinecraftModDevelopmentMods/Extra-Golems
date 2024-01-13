package com.mcmoddev.golems.data.behavior;

import com.mcmoddev.golems.entity.GolemBase;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class GolemVariantCombo {

	public static final Codec<GolemVariantCombo> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.apply(instance, GolemVariantCombo::new));

	protected final @Nullable ResourceLocation golem;
	protected final @Nullable Integer variant;

	public GolemVariantCombo(Optional<ResourceLocation> golem, Optional<Integer> variant) {
		this.golem = golem.orElse(null);
		this.variant = variant.orElse(null);
	}

	/**
	 * Simplifies codec creation, especially if no other fields are added
	 * @param instance the record codec builder with additional parameters, if any
	 */
	protected static <T extends GolemVariantCombo> Products.P2<RecordCodecBuilder.Mu<T>, Optional<ResourceLocation>, Optional<Integer>> codecStart(RecordCodecBuilder.Instance<T> instance) {
		return instance.group(
				ResourceLocation.CODEC.optionalFieldOf("golem").forGetter(o -> Optional.ofNullable(o.getGolem())),
				// TODO update this from 127 when changing the max variant
				Codec.intRange(0, 127).optionalFieldOf("variant").forGetter(o -> Optional.ofNullable(o.getVariant()))
		);
	}

	//// METHODS ////

	/**
	 * Updates the golem and variant of the given entity
	 * @param entity the golem entity
	 * @return true if either the golem or the variant changed
	 */
	public boolean update(final GolemBase entity) {
		boolean flag = false;
		// update golem
		if(golem != null) {
			entity.setMaterial(golem);
			flag = true;
		}
		// update variant
		if(variant != null) {
			entity.setTextureId(variant);
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
	public Integer getVariant() {
		return variant;
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GolemVariantCombo)) return false;
		GolemVariantCombo other = (GolemVariantCombo) o;
		return Objects.equals(golem, other.golem) && Objects.equals(variant, other.variant);
	}

	@Override
	public int hashCode() {
		return Objects.hash(golem, variant);
	}
}
