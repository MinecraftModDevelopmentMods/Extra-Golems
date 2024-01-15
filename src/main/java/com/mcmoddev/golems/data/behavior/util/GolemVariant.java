package com.mcmoddev.golems.data.behavior.util;

import com.mcmoddev.golems.entity.GolemBase;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Immutable
public class GolemVariant {

	public static final Codec<GolemVariant> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.optionalFieldOf("golem").forGetter(o -> Optional.ofNullable(o.golem)),
			// TODO update this from 127 when changing the max variant
			Codec.intRange(0, 127).optionalFieldOf("variant").forGetter(o -> Optional.ofNullable(o.variant))
	).apply(instance, GolemVariant::new));

	public static final Codec<GolemVariant> FROM_GOLEM_CODEC = Codec.either(ResourceLocation.CODEC, GolemVariant.DIRECT_CODEC)
			.xmap(either -> either.map(id -> new GolemVariant(Optional.of(id), Optional.empty()), Function.identity()),
					o -> o.golem != null && o.variant == null ? Either.left(o.golem) : Either.right(o));

	// TODO update this from 127 when changing the max variant
	public static final Codec<GolemVariant> FROM_VARIANT_CODEC = Codec.either(Codec.intRange(0, 127), GolemVariant.DIRECT_CODEC)
			.xmap(either -> either.map(variant -> new GolemVariant(Optional.empty(), Optional.of(variant)), Function.identity()),
					o -> o.variant != null && o.golem == null ? Either.left(o.variant) : Either.right(o));

	public static final Codec<GolemVariant> XOR_CODEC = Codec.either(GolemVariant.FROM_GOLEM_CODEC, GolemVariant.FROM_VARIANT_CODEC)
			.xmap(either -> either.map(Function.identity(), Function.identity()),
					o -> o.golem != null && o.variant == null ? Either.left(o) : Either.right(o));

	public static final Codec<GolemVariant> EITHER_CODEC = Codec.either(GolemVariant.XOR_CODEC, GolemVariant.DIRECT_CODEC)
			.xmap(either -> either.map(Function.identity(), Function.identity()),
					o -> (o.golem == null || o.variant == null) ? Either.left(o) : Either.right(o));

	protected final @Nullable ResourceLocation golem;
	protected final @Nullable Integer variant;

	public GolemVariant(Optional<ResourceLocation> golem, Optional<Integer> variant) {
		this.golem = golem.orElse(null);
		this.variant = variant.orElse(null);
	}

	//// METHODS ////

	/**
	 * Updates the golem and variant of the given entity
	 * @param entity the golem entity
	 * @return true if either the golem or the variant changed
	 */
	public boolean apply(final GolemBase entity) {
		boolean flag = false;
		// update golem
		if(golem != null && !golem.equals(entity.getMaterial())) {
			entity.setMaterial(golem);
			flag = true;
		}
		// update variant
		if(variant != null && variant != entity.getTextureId()) {
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
		if (!(o instanceof GolemVariant)) return false;
		GolemVariant other = (GolemVariant) o;
		return Objects.equals(golem, other.golem) && Objects.equals(variant, other.variant);
	}

	@Override
	public int hashCode() {
		return Objects.hash(golem, variant);
	}
}
