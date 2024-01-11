package com.mcmoddev.golems.data.behavior;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.util.ExtraCodecs;

import javax.annotation.concurrent.Immutable;
import java.util.function.Function;

@Immutable
public abstract class Behavior {

	public static final Codec<Behavior> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> EGRegistry.BEHAVIOR_SERIALIZERS_SUPPLIER.get().getCodec())
			.dispatch(Behavior::getCodec, Function.identity());

	private final MinMaxBounds.Ints variant;

	public Behavior(MinMaxBounds.Ints variant) {
		this.variant = variant;
	}

	//// METHODS ////

	public abstract Codec<? extends Behavior> getCodec();

	/**
	 * Simplifies codec creation, especially if no other fields are added
	 * @param instance the record codec builder with additional parameters, if any
	 */
	protected static <T extends Behavior> Products.P1<RecordCodecBuilder.Mu<T>, MinMaxBounds.Ints> codecStart(RecordCodecBuilder.Instance<T> instance) {
		return instance.group(EGCodecUtils.INTS_CODEC.optionalFieldOf("variant", MinMaxBounds.Ints.ANY).forGetter(Behavior::getVariant));
	}

	//// GETTERS ////

	public MinMaxBounds.Ints getVariant() {
		return variant;
	}
}
