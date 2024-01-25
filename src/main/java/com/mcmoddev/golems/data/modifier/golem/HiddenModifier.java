package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mojang.serialization.Codec;

import javax.annotation.concurrent.Immutable;

/**
 * Sets whether the golem is hidden from the UI
 */
@Immutable
public class HiddenModifier extends Modifier {

	public static final Codec<HiddenModifier> CODEC = Codec.BOOL
			.xmap(HiddenModifier::new, HiddenModifier::isHidden)
			.fieldOf("hidden").codec();

	private final boolean hidden;

	public HiddenModifier(boolean hidden) {
		this.hidden = hidden;
	}

	//// GETTERS ////

	public boolean isHidden() {
		return hidden;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.hidden(isHidden());
	}

	@Override
	public Codec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.HIDDEN.get();
	}
}
