package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Attributes;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mojang.serialization.MapCodec;

import javax.annotation.concurrent.Immutable;

/**
 * Merges the given {@link Attributes} with the existing ones by replacing
 * values with the ones specified here, if any
 */
@Immutable
public class AttributesModifier extends Modifier {

	public static final MapCodec<AttributesModifier> CODEC = Attributes.CODEC
			.xmap(AttributesModifier::new, AttributesModifier::getAttributes)
			.fieldOf("attributes");

	private final Attributes attributes;

	public AttributesModifier(Attributes attributes) {
		this.attributes = attributes;
	}

	//// GETTERS ////

	public Attributes getAttributes() {
		return attributes;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.attributes(b -> b.copy(getAttributes()));
	}

	@Override
	public MapCodec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.ATTRIBUTES.get();
	}
}
