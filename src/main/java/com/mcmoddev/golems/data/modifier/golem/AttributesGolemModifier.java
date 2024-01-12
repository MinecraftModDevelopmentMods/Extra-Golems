package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Attributes;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mojang.serialization.Codec;

public class AttributesGolemModifier extends GolemModifier {

	public static final Codec<AttributesGolemModifier> CODEC = Attributes.CODEC.xmap(AttributesGolemModifier::new, AttributesGolemModifier::getAttributes)
			.fieldOf("attributes").codec();
	
	private final Attributes attributes;

	public AttributesGolemModifier(Attributes attributes) {
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
	public Codec<? extends GolemModifier> getCodec() {
		return EGRegistry.GolemModifierReg.ATTRIBUTES.get();
	}
}
