package com.mcmoddev.golems.data.golem;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum SwimAbility implements StringRepresentable {
	SINK("sink"),
	FLOAT("float"),
	SWIM("swim");

	public static final Codec<SwimAbility> CODEC = StringRepresentable.fromEnum(SwimAbility::values);

	private final String name;

	SwimAbility(final String nameIn) {
		this.name = nameIn;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
