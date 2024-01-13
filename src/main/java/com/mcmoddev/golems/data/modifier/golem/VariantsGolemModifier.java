package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.concurrent.Immutable;

/**
 * Replaces the number of maximum variants with the given value
 */
@Immutable
public class VariantsGolemModifier extends GolemModifier {

	public static final Codec<VariantsGolemModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("amount").forGetter(VariantsGolemModifier::getAmount),
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(VariantsGolemModifier::replace)
	).apply(instance, VariantsGolemModifier::new));

	private final int amount;
	private final boolean replace;

	public VariantsGolemModifier(int amount, boolean replace) {
		this.amount = amount;
		this.replace = replace;
	}

	//// GETTERS ////

	public int getAmount() {
		return amount;
	}

	public boolean replace() {
		return replace;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		if(replace()) {
			builder.variants(getAmount());
		} else {
			builder.variants(builder.getVariants() + getAmount());
		}
	}

	@Override
	public Codec<? extends GolemModifier> getCodec() {
		return EGRegistry.GolemModifierReg.VARIANTS.get();
	}
}
