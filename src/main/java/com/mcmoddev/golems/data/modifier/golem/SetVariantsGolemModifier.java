package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mojang.serialization.Codec;

public class SetVariantsGolemModifier extends GolemModifier {

	public static final Codec<SetVariantsGolemModifier> CODEC = Codec.INT
			.xmap(SetVariantsGolemModifier::new, SetVariantsGolemModifier::getAmount)
			.fieldOf("amount").codec();

	private final int amount;

	public SetVariantsGolemModifier(int amount) {
		this.amount = amount;
	}

	//// GETTERS ////

	public int getAmount() {
		return amount;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.variants(getAmount());
	}

	@Override
	public Codec<? extends GolemModifier> getCodec() {
		return EGRegistry.GolemModifierReg.SET_VARIANTS.get();
	}
}
