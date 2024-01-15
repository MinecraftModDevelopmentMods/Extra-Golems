package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mojang.serialization.Codec;

import javax.annotation.concurrent.Immutable;

/**
 * Sets whether the golem is hidden from the UI
 */
@Immutable
public class HiddenGolemModifier extends GolemModifier {

	public static final Codec<HiddenGolemModifier> CODEC = Codec.BOOL
			.xmap(HiddenGolemModifier::new, HiddenGolemModifier::isHidden)
			.fieldOf("hidden").codec();

	private final boolean hidden;

	public HiddenGolemModifier(boolean hidden) {
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
	public Codec<? extends GolemModifier> getCodec() {
		return EGRegistry.GolemModifierReg.HIDDEN.get();
	}
}
