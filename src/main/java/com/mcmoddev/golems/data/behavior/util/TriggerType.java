package com.mcmoddev.golems.data.behavior.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum TriggerType implements StringRepresentable {
	HURT("hurt"),
	ATTACK("attack"),
	TICK("tick");

	public static final Codec<TriggerType> CODEC = StringRepresentable.fromEnum(TriggerType::values);

	private final String name;

	TriggerType(final String nameIn) {
		name = nameIn;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
