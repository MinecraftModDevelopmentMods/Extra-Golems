package com.mcmoddev.golems.data.modifier.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mojang.serialization.Codec;

public class AddVariantsGolemModifier extends GolemModifier {

	public static final Codec<AddVariantsGolemModifier> CODEC = Codec.INT
			.xmap(AddVariantsGolemModifier::new, AddVariantsGolemModifier::getAmount)
			.fieldOf("amount").codec();

	private final int amount;

	public AddVariantsGolemModifier(int amount) {
		this.amount = amount;
	}

	//// GETTERS ////

	public int getAmount() {
		return amount;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.variants(builder.getVariants() + getAmount());
	}

	@Override
	public Codec<? extends GolemModifier> getCodec() {
		return EGRegistry.GolemModifierReg.ADD_VARIANTS.get();
	}
}
