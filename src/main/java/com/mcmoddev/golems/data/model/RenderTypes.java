package com.mcmoddev.golems.data.model;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum RenderTypes implements StringRepresentable {
	SOLID("solid"),
	CUTOUT("cutout"),
	TRANSLUCENT("translucent");

	public static final Codec<RenderTypes> CODEC = StringRepresentable.fromEnum(RenderTypes::values);

	private final String name;

	RenderTypes(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
