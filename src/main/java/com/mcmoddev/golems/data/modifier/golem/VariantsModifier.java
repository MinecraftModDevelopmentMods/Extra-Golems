package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.concurrent.Immutable;

/**
 * Replaces the number of maximum variants with the given value
 */
@Immutable
public class VariantsModifier extends Modifier {

	public static final Codec<VariantsModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("amount").forGetter(VariantsModifier::getAmount),
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(VariantsModifier::replace)
	).apply(instance, VariantsModifier::new));

	private final int amount;
	private final boolean replace;

	public VariantsModifier(int amount, boolean replace) {
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
			builder.addVariants(getAmount());
		}
	}

	@Override
	public Codec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.VARIANTS.get();
	}
}
