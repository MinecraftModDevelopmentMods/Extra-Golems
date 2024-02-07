package com.mcmoddev.golems.data.golem;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum GolemPart implements StringRepresentable {
	ALL("all"),
	BODY("body"),
	LEGS("legs"),
	ARMS("arms"),
	LEFT_ARM("left_arm"),
	RIGHT_ARM("right_arm");

	public static final Codec<GolemPart> CODEC = StringRepresentable.fromEnum(GolemPart::values);

	private final String name;

	GolemPart(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
